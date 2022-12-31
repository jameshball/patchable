package sh.ball.patchable.graph.blocks.types;

import javafx.scene.Node;
import javafx.scene.paint.Paint;

import java.util.List;
import sh.ball.patchable.graph.blocks.BlockPort;

public class ReturnBlock extends BasicBlock {

  public ReturnBlock() {
    super(
        (inputs, outputs) -> System.arraycopy(inputs, 0, outputs, 0, outputs.length),
        List.of(new BlockPort("Left channel"), new BlockPort("Right channel")),
        List.of(new BlockPort("Left channel"), new BlockPort("Right channel")),
        Paint.valueOf("#333333"),
        "Output"
    );
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
