package sh.ball.graph.blocks.types;

import java.util.List;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.paint.Paint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SpinnerBlock extends BasicBlock {

  private final Spinner<Double> spinner;
  private double min;
  private double max;
  private double increment;

  public SpinnerBlock(double min, double max, double value, double increment) {
    super(0, 1, Paint.valueOf("blue"), "");
    spinner = new Spinner<>(min, max, value, increment);
    addNode(spinner);
    setProcessor((inputs, outputs) -> outputs[0] = spinner.getValue());
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
    Element element = document.createElement("spinner");
    element.setAttribute("min", String.valueOf(min));
    element.setAttribute("max", String.valueOf(max));
    element.setAttribute("value", String.valueOf(spinner.getValue()));
    element.setAttribute("increment", String.valueOf(increment));
    return List.of(element);
  }

  @Override
  public void load(Element root) {
    Element element = (Element) root.getElementsByTagName("spinner").item(0);
    min = Double.parseDouble(element.getAttribute("min"));
    max = Double.parseDouble(element.getAttribute("max"));
    double value = Double.parseDouble(element.getAttribute("value"));
    increment = Double.parseDouble(element.getAttribute("increment"));
    spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, value, increment));
  }
}
