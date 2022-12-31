package sh.ball.patchable.graph.blocks.types;

import java.util.List;
import javafx.scene.paint.Paint;
import sh.ball.patchable.graph.blocks.BlockPort;

public class MultiplyBlock extends BasicBlock {

  public MultiplyBlock() {
    super(
        (inputs, outputs) -> outputs[0] = inputs[0] * inputs[1],
        List.of(new BlockPort("A"), new BlockPort("B")),
        List.of(new BlockPort("A × B")),
        Paint.valueOf("#AA0000"),
        "Multiply"
    );
  }

  @Override
  public String type() {
    return "multiply";
  }
}
