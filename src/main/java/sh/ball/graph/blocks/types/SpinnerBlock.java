package sh.ball.graph.blocks.types;

import javafx.scene.control.Spinner;
import javafx.scene.paint.Paint;

public class SpinnerBlock extends BasicBlock {

  private final Spinner<Double> spinner;

  public SpinnerBlock(double min, double max, double value, double increment) {
    super(0, 1, Paint.valueOf("blue"), "");
    spinner = new Spinner<>(min, max, value, increment);
    addNode(spinner);
    setProcessor((inputs, outputs) -> outputs[0] = spinner.getValue());
  }
}
