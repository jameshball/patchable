package sh.ball.graph.blocks.types;

import java.util.List;
import javafx.scene.control.Slider;
import javafx.scene.paint.Paint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SliderBlock extends BasicBlock {
  private final Slider slider;

  public SliderBlock(double min, double max, double value) {
    super(0, 1, Paint.valueOf("blue"), "");
    slider = new Slider(min, max, value);
    addNode(slider);
    setProcessor((inputs, outputs) -> outputs[0] = slider.getValue());
  }

  @Override
  public String type() {
    return "slider";
  }

  @Override
  public List<Element> save(Document document) {
    Element element = document.createElement("slider");
    element.setAttribute("min", String.valueOf(slider.getMin()));
    element.setAttribute("max", String.valueOf(slider.getMax()));
    element.setAttribute("value", String.valueOf(slider.getValue()));
    return List.of(element);
  }

  @Override
  public void load(Element root) {
    Element element = (Element) root.getElementsByTagName("slider").item(0);
    slider.setMin(Double.parseDouble(element.getAttribute("min")));
    slider.setMax(Double.parseDouble(element.getAttribute("max")));
    slider.setValue(Double.parseDouble(element.getAttribute("value")));
  }
}
