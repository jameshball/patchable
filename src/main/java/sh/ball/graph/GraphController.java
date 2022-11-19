package sh.ball.graph;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import sh.ball.graph.blocks.Block;

import java.util.ArrayList;
import java.util.List;

public class GraphController {

  private final Group group;
  private final List<Block> blocks = new ArrayList<>();
  private final List<Node> nodes = new ArrayList<>();

  private double offsetX;
  private double offsetY;
  private int blockIndex = -1;
  private Line cable;


  public GraphController(Group group, ContextMenu contextMenu) {
    MenuItem cut = new MenuItem("Cut");
    MenuItem copy = new MenuItem("Copy");
    MenuItem paste = new MenuItem("Paste");
    contextMenu.getItems().addAll(cut, copy, paste);
    this.group = group;
  }

  public void addBlock(Block block) {
    blocks.add(block);
    Node node = block.getNode();
    nodes.add(node);
    group.getChildren().add(node);

    node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
      if (event.isPrimaryButtonDown()) {
        offsetX = event.getSceneX() - node.getLayoutX();
        offsetY = event.getSceneY() - node.getLayoutY();
      } else if (event.isSecondaryButtonDown()) {
        // check if we're over a node
        for (int i = 0; i < nodes.size(); i++) {
          Node other = nodes.get(i);
          Point2D mouse = other.sceneToLocal(event.getSceneX(), event.getSceneY());
          if (other.contains(mouse)) {
            Bounds bounds = other.getBoundsInLocal();
            blockIndex = i;
            cable = new Line(0, 0, event.getSceneX(), event.getSceneY());
            cable.startXProperty().bind(other.layoutXProperty().add(bounds.getCenterX()));
            cable.startYProperty().bind(other.layoutYProperty().add(bounds.getMaxY() - 1));
            cable.setViewOrder(other.getViewOrder() + 1);
            group.getChildren().add(cable);
            break;
          }
        }
      }
    });

    node.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
      if (event.isPrimaryButtonDown()) {
        node.setLayoutX(event.getSceneX() - offsetX);
        node.setLayoutY(event.getSceneY() - offsetY);
      } else if (event.isSecondaryButtonDown()) {
        if (cable != null) {
          cable.setEndX(event.getSceneX());
          cable.setEndY(event.getSceneY());
        }
      }
    });

    node.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
      if (event.getButton() == MouseButton.SECONDARY) {
        boolean connected = false;
        // check if we're over a node
        for (int i = 0; i < nodes.size(); i++) {
          if (blockIndex != i) {
            Node other = nodes.get(i);
            Point2D mouse = other.sceneToLocal(event.getSceneX(), event.getSceneY());
            if (other.contains(mouse)) {
              Bounds endBounds = other.getBoundsInLocal();

              // bind the cable to the other node
              cable.endXProperty().bind(other.layoutXProperty().add(endBounds.getCenterX()));
              cable.endYProperty().bind(other.layoutYProperty().add(endBounds.getMinY() + 1));

              // add the input to the block
              Block startBlock = blocks.get(blockIndex);
              Block endBlock = blocks.get(i);
              if (endBlock.currentInputs() < endBlock.totalInputs()) {
                endBlock.addInput(startBlock);
                other.setViewOrder(cable.getViewOrder() + 1);
                connected = true;
              }

              break;
            }
          }
        }

        // if we're not over a node, remove the cable
        if (cable != null && !connected) {
          group.getChildren().remove(cable);
          cable = null;
        }
      }
    });
  }
}
