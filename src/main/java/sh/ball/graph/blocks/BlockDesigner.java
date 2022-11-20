package sh.ball.graph.blocks;

import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class BlockDesigner {

  public static StackPane createNode(Paint color, String string, double width, double height) {
    Rectangle rectangle = new Rectangle(width, height, color);
    rectangle.setArcHeight(10);
    rectangle.setArcWidth(10);
    Text text = new Text(string);
    StackPane stack = new StackPane();
    stack.getChildren().addAll(rectangle, text);
    return stack;
  }

  public static StackPane createNode(Paint color, String string, double width, double height, Node node) {
    StackPane stack = createNode(color, string, width, height);
    stack.getChildren().add(node);
    return stack;
  }
}
