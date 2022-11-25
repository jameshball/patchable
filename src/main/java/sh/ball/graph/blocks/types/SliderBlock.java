package sh.ball.graph.blocks.types;

import javafx.scene.control.Slider;
import javafx.scene.paint.Paint;

public class SliderBlock extends BasicBlock {
  private final Slider slider;

  public SliderBlock(double min, double max, double value) {
    super(0, 1, Paint.valueOf("blue"), "");
    slider = new Slider(min, max, value);
    addNode(slider);
    setProcessor((inputs, outputs) -> outputs[0] = slider.getValue());
  }
}
