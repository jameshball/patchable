package sh.ball.patchable.graph.blocks;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

public class BlockDesigner {

  private static final int INPUT_WIDTH = 10;

  private static void addPopup(BlockPort port, Node node) {
    PopupControl popup = new PopupControl();
    Pane popupContent = new Pane();
    popupContent.setBackground(new Background(new BackgroundFill(Paint.valueOf("black"), new CornerRadii(INPUT_WIDTH / 2.0), Insets.EMPTY)));
    popupContent.setBorder(new Border(new BorderStroke(Paint.valueOf("white"), BorderStrokeStyle.SOLID, new CornerRadii(INPUT_WIDTH / 2.0), new BorderWidths(1))));

    Label label = new Label(port.name());
    label.setTextFill(Paint.valueOf("white"));
    label.setPadding(new Insets(2, 5, 2, 5));
    popupContent.getChildren().add(label);
    popup.getScene().setRoot(popupContent);

    popup.setAutoHide(true);
    node.setOnMouseEntered(e -> popup.show(node, e.getScreenX(), e.getScreenY()));
    node.setOnMouseExited(e -> popup.hide());
    node.setOnMouseMoved(e -> {
      popup.setX(e.getScreenX() + 10);
      popup.setY(e.getScreenY() - 5);
    });
  }

  public static List<Node> inputNodes(List<BlockPort> inputs) {
    List<Node> nodes = new ArrayList<>();
    for (BlockPort port : inputs) {
      Region input = new Region();
      input.setBackground(new Background(new BackgroundFill(Paint.valueOf("black"), new CornerRadii(INPUT_WIDTH), Insets.EMPTY)));
      input.setBorder(new Border(new BorderStroke(Paint.valueOf("white"), BorderStrokeStyle.SOLID, new CornerRadii(INPUT_WIDTH), new BorderWidths(1))));
      input.setMinWidth(INPUT_WIDTH);
      input.setMinHeight(INPUT_WIDTH);
      nodes.add(input);

      addPopup(port, input);
    }
    return nodes;
  }

  public static List<Node> outputNodes(List<BlockPort> outputs) {
    List<Node> nodes = new ArrayList<>();
    for (BlockPort port : outputs) {
      Region output = new Region();
      output.setBackground(new Background(new BackgroundFill(Paint.valueOf("white"), new CornerRadii(INPUT_WIDTH), new Insets(1))));
      output.setBorder(new Border(new BorderStroke(Paint.valueOf("black"), BorderStrokeStyle.SOLID, new CornerRadii(INPUT_WIDTH), new BorderWidths(1))));
      output.setMinWidth(INPUT_WIDTH);
      output.setMinHeight(INPUT_WIDTH);
      nodes.add(output);

      addPopup(port, output);
    }
    return nodes;
  }

  public static StackPane createNode(Paint color, String string, double minWidth, double minHeight) {
    Region region = new Region();
    region.setBackground(new Background(new BackgroundFill(color, new CornerRadii(6), Insets.EMPTY)));
    region.getStyleClass().add("block");

    StackPane stack = new StackPane();
    stack.getChildren().add(region);
    stack.setMinWidth(minWidth);
    stack.setMinHeight(minHeight);
    if (!string.equals("")) {
      Text text = new Text(string);
      text.setFill(Paint.valueOf("#ffffff"));
      stack.setMinWidth(Math.max(text.getLayoutBounds().getWidth() * 1.25, minWidth));
      stack.setMinHeight(Math.max(text.getLayoutBounds().getHeight() * 2.75, minHeight));
      stack.getChildren().add(text);
    }

    region.prefWidthProperty().bind(stack.widthProperty());
    region.prefHeightProperty().bind(stack.heightProperty());
    return stack;
  }

  public static StackPane createNode(Paint color, String string, double minWidth, double minHeight, List<Node> nodes) {
    StackPane stack = createNode(color, string, minWidth, minHeight);
    VBox box = new VBox();
    box.prefHeightProperty().bind(stack.heightProperty());
    box.prefWidthProperty().bind(stack.widthProperty());
    box.getChildren().addAll(nodes);
    stack.getChildren().add(box);
    return stack;
  }
}
