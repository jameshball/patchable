package sh.ball.patchable.graph.blocks.types;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import javafx.scene.control.TextArea;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sh.ball.patchable.graph.blocks.BlockDesigner;

public class LabelBlock extends BasicBlock {

  private final TextArea textArea = BlockDesigner.createEditableLabel();

  public LabelBlock() {
    this("label");
  }

  public LabelBlock(String defaultText) {
    super();
    textArea.setText(defaultText);
    setNode(textArea);
  }

  @Override
  public String type() {
    return "label";
  }

  @Override
  public List<Element> save(Document document) {
    Element element = document.createElement(type());
    String encodedText = Base64.getEncoder().encodeToString(textArea.getText().getBytes());
    element.setAttribute("text", encodedText);
    return List.of(element);
  }

  @Override
  public void load(Element root) {
    Element element = (Element) root.getElementsByTagName(type()).item(0);
    String encodedText = element.getAttribute("text");
    String text = new String(Base64.getDecoder().decode(encodedText));
    textArea.setText(text);
  }
}
