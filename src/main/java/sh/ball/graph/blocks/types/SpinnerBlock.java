package sh.ball.graph.blocks.types;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Paint;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.graph.blocks.Block;
import sh.ball.graph.blocks.BlockData;
import sh.ball.graph.blocks.BlockDesigner;

public class SpinnerBlock implements Block {

  private final Node node;
  private final Spinner<Double> spinner;

  public SpinnerBlock(float min, float max, float value, float increment) {
    spinner = new Spinner<>(min, max, value, increment);
    node = BlockDesigner.createNode(Paint.valueOf("blue"), "", 70, 20, spinner);
  }

  @Override
  public BlockData process() {
    return new BlockData(spinner.getValue());
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
