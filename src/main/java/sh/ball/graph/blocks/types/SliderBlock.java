package sh.ball.graph.blocks.types;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.paint.Paint;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.graph.blocks.Block;
import sh.ball.graph.blocks.BlockData;
import sh.ball.graph.blocks.BlockDesigner;

public class SliderBlock implements Block {

  private final Node node;
  private final Slider slider;

  public SliderBlock(float min, float max, float value) {
    slider = new Slider(min, max, value);
    node = BlockDesigner.createNode(Paint.valueOf("blue"), "", 70, 20, slider);
  }

  @Override
  public BlockData process() {
    return new BlockData(slider.getValue());
  }

  @Override
  public List<Block> getInputs() {
    return List.of();
  }

  @Override
  public void addInput(Block block) {
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
  public void audioDeviceChanged(AudioDevice audioDevice) {
    // do nothing
  }
}
