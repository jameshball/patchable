package sh.ball.graph.blocks.types;

import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

import java.util.List;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.graph.blocks.Block;
import sh.ball.graph.blocks.BlockDesigner;

public class SineBlock implements Block {

  private static final int DEFAULT_SAMPLE_RATE = 192000;

  private Block input = null;
  private double phase = 0;
  private int sampleRate = DEFAULT_SAMPLE_RATE;
  private final List<Node> inputNodes;
  private final List<Node> outputNodes;

  private int previousSampleNumber = -1;
  private double buffer = 0;

  public SineBlock() {
    inputNodes = BlockDesigner.inputNodes(totalInputs());
    outputNodes = BlockDesigner.outputNodes(totalOutputs());
  }

  @Override
  public double process(int sampleNumber, int index) {
    if (sampleNumber != previousSampleNumber) {
      previousSampleNumber = sampleNumber;
      double frequency = 0;
      if (input != null) {
        frequency = input.process(sampleNumber, 0);
      }
      phase += frequency / sampleRate;
      buffer = Math.sin(phase * 2 * Math.PI);
    }

    return buffer;
  }

  @Override
  public List<Block> getInputs() {
    return List.of();
  }

  @Override
  public void setInput(Block block, int index) {
    if (input != null) {
      throw new IllegalStateException("SineBlock already has an input");
    }
    if (block.totalOutputs() != 1) {
      throw new IllegalArgumentException("SineBlock only accepts blocks with 1 output");
    }
    input = block;
  }

  @Override
  public void removeInput(int index) {
    input = null;
  }

  @Override
  public int totalInputs() {
    return 1;
  }

  @Override
  public int totalOutputs() {
    return 1;
  }

  @Override
  public int currentInputs() {
    return input == null ? 0 : input.totalOutputs();
  }

  @Override
  public Node getNode() {
    return BlockDesigner.createNode(Paint.valueOf("green"), "Sine", 70, 20, Stream.concat(inputNodes.stream(), outputNodes.stream()).toList());
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
    sampleRate = audioDevice.sampleRate();
  }
}
