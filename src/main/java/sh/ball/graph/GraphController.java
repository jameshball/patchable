package sh.ball.graph;

import static sh.ball.gui.Gui.logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.audio.engine.AudioEngine;
import sh.ball.graph.blocks.Block;

import java.util.ArrayList;
import java.util.List;
import sh.ball.graph.blocks.BlockConnection;
import sh.ball.graph.blocks.types.AddBlock;
import sh.ball.graph.blocks.types.MultiplyBlock;
import sh.ball.graph.blocks.types.ReturnBlock;
import sh.ball.graph.blocks.types.SineBlock;
import sh.ball.graph.blocks.types.SliderBlock;
import sh.ball.graph.blocks.types.SpinnerBlock;

public class GraphController {

  private final Group group;
  private final List<Block> blocks = new ArrayList<>();
  private final AudioEngine audioEngine;
  private final Property<KeyEvent> keyEventProperty = new SimpleObjectProperty<>();
  private final Map<BlockConnection, Node> inputToCableMap = new HashMap<>();

  private double offsetX;
  private double offsetY;
  private int blockIndex = -1;
  private Line cable;
  private Block returnBlock;
  private int outputIndex = -1;

  public GraphController(Group group, ContextMenu contextMenu, AudioEngine audioEngine) {
    Menu newBlock = new Menu("New Block");

    Map<String, Callable<Block>> blockMap = Map.of(
        "Sine", SineBlock::new,
        "Slider", () -> new SliderBlock(0, 1000, 1),
        "Spinner", () -> new SpinnerBlock(0, 1000, 1, 0.01),
        "Add", AddBlock::new,
        "Multiply", MultiplyBlock::new
    );

    for (String name : blockMap.keySet()) {
      MenuItem item = new MenuItem(name);
      item.setOnAction(e -> {
        try {
          Block block = blockMap.get(name).call();
          addBlock(block);
        } catch (Exception ex) {
          logger.log(Level.SEVERE, "Failed to create block", ex);
        }
      });
      newBlock.getItems().add(item);
    }

    contextMenu.getItems().addAll(newBlock);

    this.audioEngine = audioEngine;
    this.group = group;
  }

  public void addConnection(BlockConnection connection) {
    Line line = new Line();
    int sourceIndex = connection.sourceIndex();
    int destIndex = connection.destIndex();
    List<Node> sourceOutputs = connection.source().getOutputNodes();
    List<Node> destInputs = connection.dest().getInputNodes();

    Node output = sourceOutputs.get(sourceIndex);
    Node input = destInputs.get(destIndex);
    Bounds inputBounds = input.getBoundsInParent();
    connection.dest().setInput(connection);

    output.boundsInParentProperty().addListener((observable, oldValue, outputBounds) -> {
      line.startXProperty().bind(output.getParent().layoutXProperty().add(outputBounds.getCenterX()));
      line.startYProperty().bind(output.getParent().layoutYProperty().add(outputBounds.getCenterY()));
      line.endXProperty().bind(input.getParent().layoutXProperty().add(inputBounds.getCenterX()));
      line.endYProperty().bind(input.getParent().layoutYProperty().add(inputBounds.getCenterY()));
    });

    group.getChildren().add(line);
    inputToCableMap.put(connection, line);
  }

  public void addBlock(Block block) {
    if (block.type().equals("return")) {
      returnBlock = block;
    }

    blocks.add(block);
    Node node = block.getNode();

    if (block.getOutputNodes().size() > 0) {
      Node output = block.getOutputNodes().get(0);
      Bounds outputBounds = output.getBoundsInParent();
    }
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
        for (int i = 0; i < blocks.size(); i++) {
          Block other = blocks.get(i);
          List<Node> outputs = other.getOutputNodes();
          for (int j = 0; j < outputs.size(); j++) {
            Node output = outputs.get(j);
            Point2D mouse = output.sceneToLocal(event.getSceneX(), event.getSceneY());
            if (output.contains(mouse)) {
              Bounds bounds = output.getBoundsInParent();
              blockIndex = i;
              outputIndex = j;
              cable = new Line(0, 0, event.getSceneX(), event.getSceneY());
              cable.startXProperty()
                  .bind(output.getParent().layoutXProperty().add(bounds.getCenterX()));
              cable.startYProperty()
                  .bind(output.getParent().layoutYProperty().add(bounds.getCenterY()));
              group.getChildren().add(cable);
              break;
            }
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
        for (int i = 0; i < blocks.size(); i++) {
          if (blockIndex != i) {
            Block other = blocks.get(i);
            List<Node> inputs = other.getInputNodes();
            for (int j = 0; j < inputs.size(); j++) {
              Node input = inputs.get(j);
              Point2D mouse = input.sceneToLocal(event.getSceneX(), event.getSceneY());
              if (input.contains(mouse)) {
                if (cable != null) {
                  Bounds endBounds = input.getBoundsInParent();

                  // bind the cable to the other node
                  cable.endXProperty().bind(input.getParent().layoutXProperty().add(endBounds.getCenterX()));
                  cable.endYProperty().bind(input.getParent().layoutYProperty().add(endBounds.getCenterY()));

                  // add the input to the block
                  Block startBlock = blocks.get(blockIndex);
                  Block endBlock = blocks.get(i);
                  if (endBlock.currentInputs() < endBlock.totalInputs()) {
                    BlockConnection blockConnection = new BlockConnection(startBlock, outputIndex, endBlock, j);
                    endBlock.setInput(blockConnection);
                    connected = true;
                    inputToCableMap.put(blockConnection, cable);
                    cable = null;
                    blockIndex = -1;
                    outputIndex = -1;
                  }
                } else {
                  // remove the input from the block
                  Block endBlock = blocks.get(i);
                  BlockConnection blockConnection = endBlock.removeInput(j);
                  Node cable = inputToCableMap.remove(blockConnection);
                  group.getChildren().remove(cable);
                }

                break;
              }
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

  public void save() {
    GraphState graphState = new GraphState(blocks, new ArrayList<>(inputToCableMap.keySet()));
    try {
      graphState.save(new File("project.patchable"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void load() {
    try {
      group.getChildren().clear();
      blocks.clear();
      inputToCableMap.clear();
      cable = null;
      blockIndex = -1;
      outputIndex = -1;

      GraphState graphState = GraphState.load(new FileInputStream("project.patchable"));
      graphState.blocks().forEach(this::addBlock);
      graphState.connections().forEach(this::addConnection);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void start() throws Exception {
    AudioDevice device = audioEngine.getDefaultOutputDevice();
    blocks.forEach(block -> block.audioDeviceChanged(device));
    AtomicInteger i = new AtomicInteger();

    audioEngine.play(() -> {
      int sample = i.getAndIncrement();
      double left = returnBlock.process(sample, 0);
      double right = returnBlock.process(sample, 1);
      return new float[]{(float) left, (float) right};
    }, device);
  }

  public void setKeyEventProperty(KeyEvent event) {
    keyEventProperty.setValue(event);
  }
}
