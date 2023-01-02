package sh.ball.patchable.graph.blocks.types;

import java.util.List;
import javafx.scene.paint.Paint;
import sh.ball.patchable.graph.blocks.BlockDesigner;
import sh.ball.patchable.graph.blocks.BlockPort;

public class MultiplyBlock extends BasicBlock {

  public MultiplyBlock() {
    super(
        (sampleNumber, inputs, outputs) -> outputs[0] = inputs[0] * inputs[1],
        List.of(new BlockPort("A"), new BlockPort("B")),
        List.of(new BlockPort("A × B")),
        BlockDesigner.RED,
        "Multiply"
    );
  }

  @Override
  public String type() {
    return "multiply";
  }
}
