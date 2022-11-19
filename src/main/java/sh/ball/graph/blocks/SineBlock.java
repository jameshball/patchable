package sh.ball.graph.blocks;

import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class SineBlock implements Block {

  private double phase = 0;

  public SineBlock() {}

  @Override
  public BlockData process() {
    phase += 0.01;
    return new BlockData(Math.sin(phase), Math.cos(phase));
  }

  @Override
  public List<Block> getInputs() {
    return List.of();
  }

  @Override
  public void addInput(Block block) {
    throw new IllegalStateException("SineBlock doesn't have any inputs");
  }

  @Override
  public int totalInputs() {
    return 0;
  }

  @Override
  public int totalOutputs() {
    return 2;
  }

  @Override
  public int currentInputs() {
    return 0;
  }

  @Override
  public Node getNode() {
    return BlockDesigner.createNode(Paint.valueOf("green"), "Sine", 70, 20);
  }
}
