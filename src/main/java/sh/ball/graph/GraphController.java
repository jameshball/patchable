package sh.ball.graph;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.audio.engine.AudioDeviceListener;
import sh.ball.audio.engine.AudioEngine;
import sh.ball.graph.blocks.Block;

import java.util.ArrayList;
import java.util.List;
import sh.ball.graph.blocks.BlockData;

public class GraphController {

  private final Group group;
  private final List<Block> blocks = new ArrayList<>();
  private final List<Node> nodes = new ArrayList<>();
  private final AudioEngine audioEngine;
  private final Property<KeyEvent> keyEventProperty = new SimpleObjectProperty<>();

  private double offsetX;
  private double offsetY;
  private int blockIndex = -1;
  private Line cable;
  private Block returnBlock;


  public GraphController(Group group, ContextMenu contextMenu, AudioEngine audioEngine) {
    MenuItem cut = new MenuItem("Cut");
    MenuItem copy = new MenuItem("Copy");
    MenuItem paste = new MenuItem("Paste");
    contextMenu.getItems().addAll(cut, copy, paste);

    this.audioEngine = audioEngine;
    this.group = group;
  }

  public void setReturnBlock(Block block) {
    this.returnBlock = block;
    addBlock(block);
  }

  public void addBlock(Block block) {
    blocks.add(block);
    Node node = block.getNode();
    nodes.add(node);
    group.getChildren().add(node);

    node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
      if (event.isPrimaryButtonDown()) {
        KeyEvent key = keyEventProperty.getValue();
        if (key != null && key.isShortcutDown()) {
          offsetX = event.getSceneX() - node.getLayoutX();
          offsetY = event.getSceneY() - node.getLayoutY();
        }
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
        KeyEvent key = keyEventProperty.getValue();
        if (key != null && key.isShortcutDown()) {
          node.setLayoutX(event.getSceneX() - offsetX);
          node.setLayoutY(event.getSceneY() - offsetY);
        }
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
              if (endBlock.currentInputs() + startBlock.totalOutputs() <= endBlock.totalInputs()) {
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

  public void start() throws Exception {
    AudioDevice device = audioEngine.getDefaultOutputDevice();
    blocks.forEach(block -> block.audioDeviceChanged(device));

    audioEngine.play(() -> {
      BlockData data = returnBlock.process();
      if (data.data.length == 2) {
        return new float[]{(float) data.data[0], (float) data.data[1]};
      } else if (data.data.length == 1) {
        return new float[]{(float) data.data[0], 0};
      } else {
        return new float[]{0, 0};
      }
    }, device);
  }

  public void setKeyEventProperty(KeyEvent event) {
    keyEventProperty.setValue(event);
  }
}
