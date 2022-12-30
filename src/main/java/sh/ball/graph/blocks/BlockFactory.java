package sh.ball.graph.blocks;

import sh.ball.graph.blocks.types.AddBlock;
import sh.ball.graph.blocks.types.MultiplyBlock;
import sh.ball.graph.blocks.types.ReturnBlock;
import sh.ball.graph.blocks.types.SineBlock;
import sh.ball.graph.blocks.types.SliderBlock;
import sh.ball.graph.blocks.types.SpinnerBlock;

public class BlockFactory {

  public static Block create(String type) {
    return switch (type) {
      case "add" -> new AddBlock();
      case "multiply" -> new MultiplyBlock();
      case "return" -> new ReturnBlock();
      case "sine" -> new SineBlock();
      case "slider" -> new SliderBlock(0, 1, 0);
      case "spinner" -> new SpinnerBlock(0, 1, 0, 0.01);
      default -> null;
    };
  }

}
