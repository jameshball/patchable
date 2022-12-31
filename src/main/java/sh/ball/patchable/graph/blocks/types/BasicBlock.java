package sh.ball.patchable.graph.blocks.types;

import java.util.ArrayList;
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

  private final List<BlockPort> inputPorts;
  private final List<BlockPort> outputPorts;
  private Node node = null;
  private final BlockConnection[] inputs;
  private final List<Node> inputNodes;
  private final List<Node> outputNodes;
  private final double[] inputBuffer;
  private final double[] outputBuffer;
  private final Paint color;
  private final String name;
  private final List<Node> nodes = new ArrayList<>();
  private final double minWidth;
  private final double minHeight;

  private BlockProcessor processor;
  private int previousSampleNumber = -1;
  protected AudioDevice device;

  public BasicBlock(BlockProcessor processor, List<BlockPort> inputPorts, List<BlockPort> outputPorts, Paint color, String name) {
    this(processor, inputPorts, outputPorts, color, name, 40, 0);
  }

  public BasicBlock(BlockProcessor processor, List<BlockPort> inputPorts, List<BlockPort> outputPorts, Paint color, String name, double minWidth, double minHeight) {
    this.processor = processor;
    this.color = color;
    this.name = name;
    this.inputPorts = inputPorts;
    this.outputPorts = outputPorts;
    this.inputNodes = BlockDesigner.inputNodes(inputPorts);
    this.outputNodes = BlockDesigner.outputNodes(outputPorts);
    this.inputs = new BlockConnection[inputPorts.size()];
    this.inputBuffer = new double[inputPorts.size()];
    this.outputBuffer = new double[outputPorts.size()];
    this.minWidth = minWidth;
    this.minHeight = minHeight;
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

  public void addNode(Node node) {
    nodes.add(node);
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
      processor.process(inputBuffer, outputBuffer);
    }

    return outputBuffer[index];
  }

  @Override
  public List<BlockConnection> getInputs() {
    return List.of(inputs);
  }

  @Override
  public void setInput(BlockConnection connection) {
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
  public BlockConnection removeInput(int index) {
    if (index >= totalInputs()) {
      throw new IllegalArgumentException("AddBlock only has " + totalInputs() + " inputs");
    }
    BlockConnection input = inputs[index];
    inputs[index] = null;
    return input;
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
      Stream<Node> nodeStream = Stream.concat(Stream.concat(Stream.of(inputNodes), nodes.stream()), Stream.of(region, outputNodes));
      node = BlockDesigner.createNode(color, name, minWidth, minHeight, nodeStream.toList());
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
