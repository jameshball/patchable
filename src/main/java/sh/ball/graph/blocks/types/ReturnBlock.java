package sh.ball.graph.blocks.types;

import javafx.scene.Node;
import javafx.scene.paint.Paint;

import java.util.List;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.graph.blocks.Block;
import sh.ball.graph.blocks.BlockDesigner;

public class ReturnBlock implements Block {

  private Block leftInput = null;
  private Block rightInput = null;

  private int previousSampleNumber = -1;
  private final double[] buffer = new double[totalOutputs()];

  private final List<Node> inputNodes;

  public ReturnBlock() {
    inputNodes = BlockDesigner.inputNodes(totalInputs());
  }

  @Override
  public double process(int sampleNumber, int index) {
    if (sampleNumber != previousSampleNumber) {
      previousSampleNumber = sampleNumber;
      if (leftInput != null) {
        buffer[0] = leftInput.process(sampleNumber, 0);
      } else {
        buffer[0] = 0;
      }
      if (rightInput != null) {
        buffer[1] = rightInput.process(sampleNumber, 0);
      } else {
        buffer[1] = 0;
      }
    }

    return buffer[index];
  }

  @Override
  public List<Block> getInputs() {
    return List.of(leftInput);
  }

  @Override
  public void setInput(Block block, int index) {
    if (leftInput != null && rightInput != null) {
      throw new IllegalStateException("ReturnBlock already has an input");
    }
    if (index >= totalInputs()) {
      throw new IllegalArgumentException("ReturnBlock only has " + totalInputs() + " inputs");
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
      throw new IllegalArgumentException("ReturnBlock only has " + totalInputs() + " inputs");
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
    return 2;
  }

  @Override
  public int currentInputs() {
    return (leftInput == null ? 0 : 1) + (rightInput == null ? 0 : 1);
  }

  @Override
  public Node getNode() {
    return BlockDesigner.createNode(Paint.valueOf("red"), "Return", 70, 20, inputNodes);
  }

  @Override
  public List<Node> getInputNodes() {
    return inputNodes;
  }

  @Override
  public List<Node> getOutputNodes() {
    return List.of();
  }

  @Override
  public void audioDeviceChanged(AudioDevice audioDevice) {
    // do nothing
  }
}
