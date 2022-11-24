package sh.ball.graph.blocks.types;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.paint.Paint;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.graph.blocks.Block;
import sh.ball.graph.blocks.BlockDesigner;

public class SliderBlock implements Block {

  private final Node node;
  private final Slider slider;
  private final Node outputNode;

  public SliderBlock(double min, double max, double value) {
    slider = new Slider(min, max, value);
    outputNode = BlockDesigner.outputNodes(totalOutputs()).get(0);
    node = BlockDesigner.createNode(Paint.valueOf("blue"), "", 70, 20, List.of(slider, outputNode));
  }

  @Override
  public double process(int sampleNumber, int index) {
    return slider.getValue();
  }

  @Override
  public List<Block> getInputs() {
    return List.of();
  }

  @Override
  public void setInput(Block block, int index) {
    throw new IllegalStateException("ValueBlock doesn't have any inputs");
  }

  @Override
  public void removeInput(int index) {
    throw new IllegalStateException("ValueBlock doesn't have any inputs");
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
