package sh.ball.graph.blocks.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.graph.blocks.Block;
import sh.ball.graph.blocks.BlockDesigner;
import sh.ball.graph.blocks.BlockConnection;
import sh.ball.graph.blocks.BlockProcessor;

public abstract class BasicBlock implements Block {

  private Node node = null;
  private final BlockConnection[] inputs;
  private final List<Node> inputNodes;
  private final List<Node> outputNodes;
  private final int totalInputs;
  private final int totalOutputs;
  private final double[] inputBuffer;
  private final double[] outputBuffer;
  private final Paint color;
  private final String name;
  private final List<Node> nodes = new ArrayList<>();

  private BlockProcessor processor;
  private int previousSampleNumber = -1;
  protected AudioDevice device;

  public BasicBlock(BlockProcessor processor, int totalInputs, int totalOutputs, Paint color, String name) {
    this.processor = processor;
    this.totalInputs = totalInputs;
    this.totalOutputs = totalOutputs;
    this.color = color;
    this.name = name;
    this.inputNodes = BlockDesigner.inputNodes(totalInputs);
    this.outputNodes = BlockDesigner.outputNodes(totalOutputs);
    this.inputs = new BlockConnection[totalInputs];
    this.inputBuffer = new double[totalInputs];
    this.outputBuffer = new double[totalOutputs];
  }

  public BasicBlock(int totalInputs, int totalOutputs, Paint color, String name) {
    this(null, totalInputs, totalOutputs, color, name);
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
    return totalInputs;
  }

  @Override
  public int totalOutputs() {
    return totalOutputs;
  }

  @Override
  public int currentInputs() {
    return (int) Stream.of(inputs).filter(Objects::nonNull).count();
  }

  @Override
  public Node getNode() {
    if (node == null) {
      Stream<Node> nodeStream = Stream.concat(Stream.concat(getInputNodes().stream(), getOutputNodes().stream()), nodes.stream());
      node = BlockDesigner.createNode(color, name, 70, 20, nodeStream.toList());
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