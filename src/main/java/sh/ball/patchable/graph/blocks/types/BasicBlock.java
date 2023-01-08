package sh.ball.patchable.graph.blocks.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sh.ball.patchable.audio.engine.AudioDevice;
import sh.ball.patchable.graph.blocks.Block;
import sh.ball.patchable.graph.blocks.BlockDesigner;
import sh.ball.patchable.graph.blocks.BlockConnection;
import sh.ball.patchable.graph.blocks.BlockPort;
import sh.ball.patchable.graph.blocks.BlockProcessor;

public abstract class BasicBlock implements Block {

  protected List<BlockPort> inputPorts;
  protected List<BlockPort> outputPorts;
  private Node node = null;
  protected BlockConnection[] inputs;
  protected List<List<BlockConnection>> outputs;
  protected List<Node> inputNodes;
  protected List<Node> outputNodes;
  protected double[] inputBuffer;
  protected double[] outputBuffer;
  private final Paint color;
  private final String name;
  private final List<Node> inheritableNodes = new ArrayList<>();
  private final List<Node> nonInheritableNodes = new ArrayList<>();
  private final double minWidth;
  private final double minHeight;

  private BlockProcessor processor;
  protected int previousSampleNumber = -1;
  protected AudioDevice device;

  public BasicBlock(Paint color, String name) {
    this(List.of(), List.of(), color, name);
  }

  public BasicBlock(Paint color, String name, double minWidth, double minHeight) {
    this(List.of(), List.of(), color, name, minWidth, minHeight);
  }

  public BasicBlock(BlockProcessor processor, List<BlockPort> inputPorts, List<BlockPort> outputPorts, Paint color, String name) {
    this(processor, inputPorts, outputPorts, color, name, 40, 0);
  }

  public BasicBlock(BlockProcessor processor, List<BlockPort> inputPorts, List<BlockPort> outputPorts, Paint color, String name, double minWidth, double minHeight) {
    this(processor, inputPorts, outputPorts, color, name, minWidth, minHeight, null);
  }

  public BasicBlock() {
    this(null, List.of(), List.of(), null, null, 0, 0, null);
  }

  public BasicBlock(BlockProcessor processor, List<BlockPort> inputPorts, List<BlockPort> outputPorts, Paint color, String name, double minWidth, double minHeight, Node node) {
    this.processor = processor;
    this.color = color;
    this.name = name;
    this.inputPorts = inputPorts;
    this.outputPorts = outputPorts;
    this.inputNodes = BlockDesigner.inputNodes(inputPorts);
    this.outputNodes = BlockDesigner.outputNodes(outputPorts);
    this.inputs = new BlockConnection[inputPorts.size()];
    this.outputs = new ArrayList<>();
    for (int i = 0; i < outputPorts.size(); i++) {
      outputs.add(new ArrayList<>());
    }
    this.inputBuffer = new double[inputPorts.size()];
    this.outputBuffer = new double[outputPorts.size()];
    this.minWidth = minWidth;
    this.minHeight = minHeight;
    this.node = node;
  }

  public BasicBlock(List<BlockPort> inputPorts, List<BlockPort> outputPorts, Paint color, String name) {
    this(null, inputPorts, outputPorts, color, name);
  }

  public BasicBlock(List<BlockPort> inputPorts, List<BlockPort> outputPorts, Paint color, String name, double minWidth, double minHeight) {
    this(null, inputPorts, outputPorts, color, name, minWidth, minHeight);
  }

  public void setProcessor(BlockProcessor processor) {
    this.processor = processor;
  }

  @Override
  public void addNonInheritableNode(Node node) {
    nonInheritableNodes.add(node);
  }

  @Override
  public void addInheritableNode(Node node) {
    inheritableNodes.add(node);
  }

  public void setNode(Node node) {
    this.node = node;
  }

  @Override
  public double process(int sampleNumber, int index) {
    if (sampleNumber != previousSampleNumber) {
      previousSampleNumber = sampleNumber;
      for (int i = 0; i < totalInputs(); i++) {
        if (inputs[i] != null) {
          inputBuffer[i] = inputs[i].source().process(sampleNumber, inputs[i].sourceIndex());
        } else {
          inputBuffer[i] = 0;
        }
      }
      processor.process(sampleNumber, inputBuffer, outputBuffer);
    }

    return outputBuffer[index];
  }

  @Override
  public List<Node> getInheritableNodes() {
    return inheritableNodes;
  }

  @Override
  public List<BlockConnection> getInputs() {
    return Arrays.asList(inputs);
  }

  @Override
  public List<List<BlockConnection>> getOutputs() {
    return outputs;
  }

  @Override
  public void addInput(BlockConnection connection) {
    int index = connection.destIndex();
    if (index >= totalInputs()) {
      throw new IllegalArgumentException("Block only has " + totalInputs() + " inputs");
    }
    if (inputs[index] != null) {
      throw new IllegalStateException("Block already has an input at index " + index);
    }
    inputs[index] = connection;
  }

  @Override
  public void addOutput(BlockConnection connection) {
    int index = connection.sourceIndex();
    if (index >= totalOutputs()) {
      throw new IllegalArgumentException("Block only has " + totalOutputs() + " outputs");
    }
    outputs.get(index).add(connection);
  }

  @Override
  public void addConnection(BlockConnection connection) {
    boolean inputConnection = connection.dest() == this;
    boolean outputConnection = connection.source() == this;
    if (!inputConnection && !outputConnection) {
      throw new IllegalArgumentException("Connection must be either input or output");
    }

    if (inputConnection) {
      addInput(connection);
      connection.source().addOutput(connection);
    } else {
      addOutput(connection);
      connection.dest().addInput(connection);
    }
  }

  @Override
  public List<BlockPort> getInputPorts() {
    return inputPorts;
  }

  @Override
  public List<BlockPort> getOutputPorts() {
    return outputPorts;
  }

  @Override
  public BlockConnection getInput(int index) {
    if (index >= totalInputs()) {
      throw new IllegalArgumentException("Block only has " + totalInputs() + " inputs");
    }
    return inputs[index];
  }

  @Override
  public void removeConnection(BlockConnection connection) {
    boolean inputConnection = connection.dest() == this;
    boolean outputConnection = connection.source() == this;
    if (!inputConnection && !outputConnection) {
      throw new IllegalArgumentException("Connection must be either input or output");
    }

    if (inputConnection) {
      removeInput(connection.destIndex());
      connection.source().removeOutput(connection);
    } else {
      addOutput(connection);
      connection.dest().removeInput(connection.destIndex());
    }
  }

  @Override
  public BlockConnection removeInput(int index) {
    if (index >= totalInputs()) {
      throw new IllegalArgumentException("Block only has " + totalInputs() + " inputs");
    }
    BlockConnection input = inputs[index];
    inputs[index] = null;
    return input;
  }

  @Override
  public void removeOutput(BlockConnection connection) {
    if (connection.sourceIndex() >= totalOutputs()) {
      throw new IllegalArgumentException("Block only has " + totalOutputs() + " outputs");
    }
    outputs.get(connection.sourceIndex()).remove(connection);
  }

  @Override
  public int totalInputs() {
    return inputPorts.size();
  }

  @Override
  public int totalOutputs() {
    return outputPorts.size();
  }

  @Override
  public int currentInputs() {
    return (int) Stream.of(inputs).filter(Objects::nonNull).count();
  }

  @Override
  public Node getNode() {
    if (node == null) {
      HBox inputNodes = new HBox();
      inputNodes.setPadding(new Insets(3));
      inputNodes.setSpacing(3);
      inputNodes.getChildren().addAll(getInputNodes());

      HBox outputNodes = new HBox();
      outputNodes.setPadding(new Insets(3));
      outputNodes.setSpacing(3);
      outputNodes.setAlignment(Pos.BOTTOM_CENTER);
      outputNodes.getChildren().addAll(getOutputNodes());

      Region region = new Region();

      VBox.setVgrow(region, Priority.ALWAYS);
      Stream<Node> nodeStream = Stream.concat(nonInheritableNodes.stream(), Stream.concat(inheritableNodes.stream(), Stream.of(region)));
      node = BlockDesigner.createNode(color, name, minWidth, minHeight, List.of(inputNodes), List.of(outputNodes), nodeStream.toList());
    }
    return node;
  }

  @Override
  public List<Node> getInputNodes() {
    return inputNodes;
  }

  @Override
  public List<Node> getOutputNodes() {
    return outputNodes;
  }

  @Override
  public void audioDeviceChanged(AudioDevice audioDevice) {
    this.device = audioDevice;
  }

  @Override
  public List<Element> save(Document document) {
    return List.of();
  }

  @Override
  public void load(Element root) {
  }
}
