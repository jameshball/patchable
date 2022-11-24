package sh.ball.graph.blocks.types;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Paint;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.graph.blocks.Block;
import sh.ball.graph.blocks.BlockDesigner;

public class SpinnerBlock implements Block {

  private final Node node;
  private final Spinner<Double> spinner;
  private final Node outputNode;

  public SpinnerBlock(double min, double max, double value, double increment) {
    spinner = new Spinner<>(min, max, value, increment);
    outputNode = BlockDesigner.outputNodes(totalOutputs()).get(0);
    node = BlockDesigner.createNode(Paint.valueOf("blue"), "", 70, 20, List.of(spinner, outputNode));
  }

  @Override
  public double process(int sampleNumber, int index) {
    return spinner.getValue();
  }

  @Override
  public List<Block> getInputs() {
    return List.of();
  }

  @Override
  public void setInput(Block block, int index) {
    throw new IllegalStateException("SpinnerBlock doesn't have any inputs");
  }

  @Override
  public void removeInput(int index) {
    throw new IllegalStateException("SpinnerBlock doesn't have any inputs");
  }

  @Override
  public int totalInputs() {
    return 0;
  }

  @Override
  public int totalOutputs() {
    return 1;
  }

  @Override
  public int currentInputs() {
    return 0;
  }

  @Override
  public Node getNode() {
    return node;
  }

  @Override
  public List<Node> getInputNodes() {
    return List.of();
  }

  @Override
  public List<Node> getOutputNodes() {
    return List.of(outputNode);
  }

  @Override
  public void audioDeviceChanged(AudioDevice audioDevice) {
    // do nothing
  }
}
