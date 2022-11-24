package sh.ball.graph.blocks.types;

import java.util.List;
import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.graph.blocks.Block;
import sh.ball.graph.blocks.BlockDesigner;

public class AddBlock implements Block {

  private Block leftInput = null;
  private Block rightInput = null;

  private final List<Node> inputNodes;
  private final List<Node> outputNodes;

  private int previousSampleNumber = -1;
  private double buffer = 0;

  public AddBlock() {
    inputNodes = BlockDesigner.inputNodes(totalInputs());
    outputNodes = BlockDesigner.outputNodes(totalOutputs());
  }

  @Override
  public double process(int sampleNumber, int index) {
    if (sampleNumber != previousSampleNumber) {
      previousSampleNumber = sampleNumber;
      if (leftInput != null) {
        buffer = leftInput.process(sampleNumber, 0);
      } else {
        buffer = 0;
      }
      if (rightInput != null) {
        buffer += rightInput.process(sampleNumber, 0);
      }
    }

    return buffer;
  }

  @Override
  public List<Block> getInputs() {
    return List.of();
  }

  @Override
  public void setInput(Block block, int index) {
    if (leftInput != null && rightInput != null) {
      throw new IllegalStateException("AddBlock already has an input");
    }
    if (index >= totalInputs()) {
      throw new IllegalArgumentException("AddBlock only has " + totalInputs() + " inputs");
    }
    if (index == 0) {
      leftInput = block;
    } else {
      rightInput = block;
    }
  }

  @Override
  public void removeInput(int index) {
    if (index >= totalInputs()) {
      throw new IllegalArgumentException("AddBlock only has " + totalInputs() + " inputs");
    }
    if (index == 0) {
      leftInput = null;
    } else {
      rightInput = null;
    }
  }

  @Override
  public int totalInputs() {
    return 2;
  }

  @Override
  public int totalOutputs() {
    return 1;
  }

  @Override
  public int currentInputs() {
    return (leftInput == null ? 0 : 1) + (rightInput == null ? 0 : 1);
  }

  @Override
  public Node getNode() {
    return BlockDesigner.createNode(Paint.valueOf("green"), "Add", 70, 20, Stream.concat(inputNodes.stream(), outputNodes.stream()).toList());
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
    // do nothing
  }
}
