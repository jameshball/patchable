package sh.ball.patchable.graph.blocks.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.Node;
import sh.ball.patchable.graph.blocks.Block;
import sh.ball.patchable.graph.blocks.BlockConnection;
import sh.ball.patchable.graph.blocks.BlockDesigner;
import sh.ball.patchable.graph.blocks.BlockPort;

public class ModuleBlock extends BasicBlock {

  private final List<Block> blocks = new ArrayList<>();
  private final List<BlockConnection> newConnections;
  private final Map<Integer, BlockConnection> pseudoConnections = new HashMap<>();
  private final List<BlockConnection> removedConnections;


  public ModuleBlock(List<Block> blocks) {
    super(BlockDesigner.BLUE, "Module");

    this.blocks.addAll(blocks);

    Set<Block> blockSet = new HashSet<>(blocks);

    List<BlockPort> inputPorts = new ArrayList<>();
    List<BlockPort> outputPorts = new ArrayList<>();

    List<BlockConnection> removedConnections = new ArrayList<>();
    List<BlockConnection> newConnections = new ArrayList<>();
    List<Block> outputBlocks = new ArrayList<>();
    List<Integer> outputIndices = new ArrayList<>();

    for (Block block : blocks) {
      List<BlockPort> blockInputPorts = block.getInputPorts();
      List<BlockConnection> blockInputs = block.getInputs();
      for (int i = 0; i < blockInputs.size(); i++) {
        BlockConnection connection = blockInputs.get(i);
        if (connection == null || !blockSet.contains(connection.source())) {
          inputPorts.add(blockInputPorts.get(i));
          if (connection != null) {
            connection.dest().removeInput(connection.destIndex());
            connection.source().removeOutput(connection);
            newConnections.add(new BlockConnection(connection.source(), connection.sourceIndex(), this, inputPorts.size() - 1));
          }

          pseudoConnections.put(inputPorts.size() - 1, new BlockConnection(null, -1, block, i));
        }

        if (connection != null) {
          removedConnections.add(connection);
        }
      }

      List<BlockPort> blockOutputPorts = block.getOutputPorts();
      List<List<BlockConnection>> blockOutputs = block.getOutputs();
      boolean addedPort = false;
      for (int i = 0; i < blockOutputs.size(); i++) {
        List<BlockConnection> connections = new ArrayList<>(blockOutputs.get(i));
        if (connections.isEmpty()) {
          outputPorts.add(blockOutputPorts.get(i));
        } else {
          for (BlockConnection connection : connections) {
            if (!blockSet.contains(connection.dest())) {
              if (!addedPort) {
                outputBlocks.add(block);
                outputIndices.add(i);
                outputPorts.add(blockOutputPorts.get(i));
                addedPort = true;
              }
              connection.dest().removeInput(connection.destIndex());
              connection.source().removeOutput(connection);
              newConnections.add(new BlockConnection(this, outputPorts.size() - 1, connection.dest(), connection.destIndex()));
            }

            removedConnections.add(connection);
          }
        }
      }
    }

    this.inputPorts = inputPorts;
    this.outputPorts = outputPorts;
    this.inputNodes = BlockDesigner.inputNodes(this.inputPorts);
    this.outputNodes = BlockDesigner.outputNodes(this.outputPorts);
    this.inputs = new BlockConnection[this.inputPorts.size()];
    this.outputs = new ArrayList<>();
    for (int i = 0; i < this.outputPorts.size(); i++) {
      this.outputs.add(new ArrayList<>());
    }
    this.inputBuffer = new double[this.inputPorts.size()];
    this.outputBuffer = new double[this.outputPorts.size()];

    newConnections.forEach(this::addConnection);

    this.newConnections = newConnections;
    this.removedConnections = removedConnections;

    setProcessor((sampleNumber, inputs, outputs) -> {
      for (int i = 0; i < outputBlocks.size(); i++) {
        outputBuffer[i] = outputBlocks.get(i).process(sampleNumber, outputIndices.get(i));
      }
    });

    double avgPosX = blocks.stream().map(Block::getNode).map(Node::getLayoutX).reduce(Double::sum).orElse(0.0) / blocks.size();
    double avgPosY = blocks.stream().map(Block::getNode).map(Node::getLayoutY).reduce(Double::sum).orElse(0.0) / blocks.size();

    getNode().setLayoutX(avgPosX);
    getNode().setLayoutY(avgPosY);
  }

  public List<BlockConnection> getNewConnections() {
    return newConnections;
  }

  public List<BlockConnection> getRemovedConnections() {
    return removedConnections;
  }

  @Override
  public BlockConnection removeInput(int index) {
    BlockConnection connection = super.removeInput(index);
    if (connection != null) {
      BlockConnection pseudoConnection = pseudoConnections.get(index);
      pseudoConnection.dest().removeInput(pseudoConnection.destIndex());
    }

    return connection;
  }

  @Override
  public void addInput(BlockConnection connection) {
    super.addInput(connection);
    BlockConnection pseudoConnection = pseudoConnections.get(connection.destIndex());
    pseudoConnection.dest().addInput(new BlockConnection(connection.source(), connection.sourceIndex(), pseudoConnection.dest(), pseudoConnection.destIndex()));
  }

  @Override
  public String type() {
    return "module";
  }

}
