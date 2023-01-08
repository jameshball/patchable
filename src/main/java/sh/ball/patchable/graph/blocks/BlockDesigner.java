package sh.ball.patchable.graph.blocks;

import com.sun.javafx.scene.control.skin.Utils;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class BlockDesigner {

  public static final Paint RED = Paint.valueOf("#AA0000");
  public static final Paint GREEN = Paint.valueOf("#006700");
  public static final Paint BLUE = Paint.valueOf("#00008E");
  public static final Paint GREY = Paint.valueOf("#333333");

  private static final int INPUT_WIDTH = 10;
  
  public static Label createLabel(String string) {
    Label label = new Label(string);
    label.setBackground(new Background(new BackgroundFill(Paint.valueOf("black"), new CornerRadii(INPUT_WIDTH / 2.0), Insets.EMPTY)));
    label.setBorder(new Border(new BorderStroke(Paint.valueOf("white"), BorderStrokeStyle.SOLID, new CornerRadii(INPUT_WIDTH / 2.0), new BorderWidths(1))));

    label.setTextFill(Paint.valueOf("white"));
    label.setPadding(new Insets(2, 5, 2, 5));
    return label;
  }

  public static TextArea createEditableLabel() {
    TextArea text = new TextArea();
    text.setBackground(new Background(new BackgroundFill(Paint.valueOf("black"), new CornerRadii(INPUT_WIDTH / 2.0), Insets.EMPTY)));
    text.setBorder(new Border(new BorderStroke(Paint.valueOf("white"), BorderStrokeStyle.SOLID, new CornerRadii(INPUT_WIDTH / 2.0), new BorderWidths(1))));
    text.setStyle("-fx-text-fill: white;");
    text.textProperty().addListener((ob, o, n) -> {
      text.setPrefWidth(Utils.computeTextWidth(text.getFont(), text.getText(), 0) + 25);
      text.setPrefHeight(Utils.computeTextHeight(text.getFont(), text.getText(), 0, TextBoundsType.LOGICAL) + 10);
    });
    text.setPadding(new Insets(1, 1, 1, 1));
    return text;
  }

  public static void addPopup(String string, Node node) {
    PopupControl popup = new PopupControl();
    
    popup.getScene().setRoot(createLabel(string));

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

      addPopup(port.name(), input);
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

      addPopup(port.name(), output);
    }
    return nodes;
  }

  public static StackPane createNode(Paint color, double minWidth, double minHeight) {
    Region region = new Region();
    region.setBackground(new Background(new BackgroundFill(color, new CornerRadii(6), Insets.EMPTY)));
    region.getStyleClass().add("block");

    StackPane stack = new StackPane();
    stack.getChildren().add(region);
    stack.setMinWidth(minWidth);
    stack.setMinHeight(minHeight);
    return stack;
  }

  public static StackPane createNode(Paint color, String string, double minWidth, double minHeight, List<Node> inputNodes, List<Node> outputNodes, List<Node> nodes) {
    StackPane stack = createNode(color, minWidth, minHeight);
    VBox box = new VBox();
    box.setPadding(new Insets(0, 3, 0, 3));
    box.getChildren().addAll(inputNodes);
    box.setAlignment(Pos.CENTER);
    box.setSpacing(2);
    if (!string.equals("")) {
      Text text = new Text(string);
      text.setFill(Paint.valueOf("#ffffff"));
      stack.setMinWidth(Math.max(text.getLayoutBounds().getWidth() * 1.25, minWidth));
      stack.setMinHeight(Math.max(text.getLayoutBounds().getHeight() * 2.75, minHeight));
      box.getChildren().add(text);
    }
    box.getChildren().addAll(nodes);
    box.getChildren().addAll(outputNodes);
    stack.getChildren().add(box);
    return stack;
  }
}
