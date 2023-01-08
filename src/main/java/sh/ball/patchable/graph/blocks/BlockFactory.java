package sh.ball.patchable.graph.blocks;

import sh.ball.patchable.graph.blocks.types.AddBlock;
import sh.ball.patchable.graph.blocks.types.LabelBlock;
import sh.ball.patchable.graph.blocks.types.MultiplyBlock;
import sh.ball.patchable.graph.blocks.types.ReturnBlock;
import sh.ball.patchable.graph.blocks.types.SineBlock;
import sh.ball.patchable.graph.blocks.types.SliderBlock;
import sh.ball.patchable.graph.blocks.types.SpinnerBlock;

public class BlockFactory {

  public static Block create(String type) {
    return switch (type) {
      case "add" -> new AddBlock();
      case "multiply" -> new MultiplyBlock();
      case "return" -> new ReturnBlock();
      case "sine" -> new SineBlock();
      case "slider" -> new SliderBlock(0, 1, 0);
      case "spinner" -> new SpinnerBlock(0, 1, 0, 0.01);
      case "label" -> new LabelBlock();
      default -> null;
    };
  }

}
