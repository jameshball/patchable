package sh.ball.graph.blocks.types;

import com.sun.javafx.geom.RoundRectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.graph.blocks.Block;
import sh.ball.graph.blocks.BlockData;
import sh.ball.graph.blocks.BlockDesigner;

public class ReturnBlock implements Block {

  private Block input = null;

  public ReturnBlock() {}

  @Override
  public BlockData process() {
    if (input == null) {
      return new BlockData();
    }
    return input.process();
  }

  @Override
  public List<Block> getInputs() {
    return List.of(input);
  }

  @Override
  public void addInput(Block block) {
    if (input != null) {
      throw new IllegalStateException("ReturnBlock already has an input");
    }
    input = block;
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
    return input == null ? 0 : input.totalOutputs();
  }

  @Override
  public Node getNode() {
    return BlockDesigner.createNode(Paint.valueOf("red"), "Return", 70, 20);
  }

  @Override
  public void audioDeviceChanged(AudioDevice audioDevice) {
    // do nothing
  }
}
