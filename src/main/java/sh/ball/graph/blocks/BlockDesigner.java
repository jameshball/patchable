package sh.ball.graph.blocks;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class BlockDesigner {

  private static final int INPUT_WIDTH = 10;

  public static List<Node> inputNodes(int totalInputs) {
    List<Node> nodes = new ArrayList<>();
    for (int i = 0; i < totalInputs; i++) {
      Rectangle input = new Rectangle(INPUT_WIDTH, INPUT_WIDTH, Paint.valueOf("black"));
      input.setArcHeight(5);
      input.setArcWidth(5);
      input.setTranslateX(2 + 1.25 * INPUT_WIDTH * i);
      StackPane.setAlignment(input, Pos.TOP_LEFT);
      nodes.add(input);
    }
    return nodes;
  }

  public static List<Node> outputNodes(int totalOutputs) {
    List<Node> nodes = new ArrayList<>();
    double width = totalOutputs * 1.25 * INPUT_WIDTH;
    for (int i = 0; i < totalOutputs; i++) {
      Rectangle output = new Rectangle(INPUT_WIDTH, INPUT_WIDTH, Paint.valueOf("white"));
      output.setArcHeight(5);
      output.setArcWidth(5);
      output.setTranslateX(1.25 * INPUT_WIDTH * i - width / 2);
      StackPane.setAlignment(output, Pos.BOTTOM_CENTER);
      nodes.add(output);
    }
    return nodes;
  }

  public static StackPane createNode(Paint color, String string, double width, double height) {
    Rectangle rectangle = new Rectangle(width, height, color);
    rectangle.setArcHeight(10);
    rectangle.setArcWidth(10);
    Text text = new Text(string);
    StackPane stack = new StackPane();
    stack.getChildren().addAll(rectangle, text);
    return stack;
  }

  public static StackPane createNode(Paint color, String string, double width, double height, List<Node> node) {
    StackPane stack = createNode(color, string, width, height);
    stack.getChildren().addAll(node);
    return stack;
  }
}
