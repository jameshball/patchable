package sh.ball.graph.blocks.types;

import javafx.scene.paint.Paint;

public class AddBlock extends BasicBlock {

  public AddBlock() {
    super((inputs, outputs) -> outputs[0] = inputs[0] + inputs[1], 2, 1, Paint.valueOf("#ff0000"), "Add");
  }
}
