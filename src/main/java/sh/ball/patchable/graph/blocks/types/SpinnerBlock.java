package sh.ball.patchable.graph.blocks.types;

import java.util.List;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sh.ball.patchable.graph.blocks.BlockDesigner;
import sh.ball.patchable.graph.blocks.BlockPort;

public class SpinnerBlock extends BasicBlock {

  private final Spinner<Double> spinner;
  private double min;
  private double max;
  private double increment;

  public SpinnerBlock(double min, double max, double value, double increment) {
    super(List.of(), List.of(new BlockPort("Spinner value")), BlockDesigner.BLUE, "", 100, 0);
    spinner = new Spinner<>(min, max, value, increment);
    addInheritableNode(spinner);
    setProcessor((sampleNumber, inputs, outputs) -> outputs[0] = spinner.getValue());
    this.min = min;
    this.max = max;
    this.increment = increment;
  }

  @Override
  public String type() {
    return "spinner";
  }

  @Override
  public List<Element> save(Document document) {
    Element element = document.createElement(type());
    element.setAttribute("min", String.valueOf(min));
    element.setAttribute("max", String.valueOf(max));
    element.setAttribute("value", String.valueOf(spinner.getValue()));
    element.setAttribute("increment", String.valueOf(increment));
    return List.of(element);
  }

  @Override
  public void load(Element root) {
    Element element = (Element) root.getElementsByTagName(type()).item(0);
    min = Double.parseDouble(element.getAttribute("min"));
    max = Double.parseDouble(element.getAttribute("max"));
    double value = Double.parseDouble(element.getAttribute("value"));
    increment = Double.parseDouble(element.getAttribute("increment"));
    spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, value, increment));
  }
}
