package sh.ball.patchable.graph.blocks.types;

import java.util.List;
import javafx.scene.control.Slider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sh.ball.patchable.graph.blocks.BlockDesigner;
import sh.ball.patchable.graph.blocks.BlockPort;

public class SliderBlock extends BasicBlock {
  private final Slider slider;

  public SliderBlock(double min, double max, double value) {
    super(List.of(), List.of(new BlockPort("Slider value")), BlockDesigner.BLUE, "", 100, 0);
    slider = new Slider(min, max, value);
    addInheritableNode(slider);
    setProcessor((sampleNumber, inputs, outputs) -> outputs[0] = slider.getValue());
  }

  @Override
  public String type() {
    return "slider";
  }

  @Override
  public List<Element> save(Document document) {
    Element element = document.createElement(type());
    element.setAttribute("min", String.valueOf(slider.getMin()));
    element.setAttribute("max", String.valueOf(slider.getMax()));
    element.setAttribute("value", String.valueOf(slider.getValue()));
    return List.of(element);
  }

  @Override
  public void load(Element root) {
    Element element = (Element) root.getElementsByTagName(type()).item(0);
    slider.setMin(Double.parseDouble(element.getAttribute("min")));
    slider.setMax(Double.parseDouble(element.getAttribute("max")));
    slider.setValue(Double.parseDouble(element.getAttribute("value")));
  }
}
