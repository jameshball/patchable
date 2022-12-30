package sh.ball.graph.blocks.types;

import javafx.scene.Node;
import javafx.scene.paint.Paint;

import java.util.List;

public class ReturnBlock extends BasicBlock {

  public ReturnBlock() {
    super((inputs, outputs) -> System.arraycopy(inputs, 0, outputs, 0, outputs.length), 2, 2, Paint.valueOf("#ff0000"), "Return");
  }
  @Override
  public List<Node> getOutputNodes() {
    return List.of();
  }

  @Override
  public String type() {
    return "return";
  }
}
