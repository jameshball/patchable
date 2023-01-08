package sh.ball.patchable.graph;

import static sh.ball.patchable.gui.Gui.logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import sh.ball.patchable.audio.engine.AudioDevice;
import sh.ball.patchable.audio.engine.AudioEngine;
import sh.ball.patchable.graph.blocks.Block;

import java.util.ArrayList;
import java.util.List;
import sh.ball.patchable.graph.blocks.BlockConnection;
import sh.ball.patchable.graph.blocks.types.*;

public class GraphController {

  private final Group group;
  private final List<Block> blocks = new ArrayList<>();
  private final List<Block> selectedBlocks = new ArrayList<>();
  private final List<Double> selectedOffsetsX = new ArrayList<>();
  private final List<Double> selectedOffsetsY = new ArrayList<>();
  private final AudioEngine audioEngine;
  private final Map<BlockConnection, Node> inputToCableMap = new HashMap<>();
  private final Pane pane;

  private Block startBlock;
  private Line cable;
  private Rectangle selectionRectangle;
  private Block returnBlock;
  private int outputIndex = -1;
  private double mouseDownX;
  private double mouseDownY;

  public GraphController(Pane pane, Group group, ContextMenu contextMenu, AudioEngine audioEngine) {
    this.pane = pane;
    pane.setId("main-pane");
    pane.setOnMousePressed(e -> pane.requestFocus());

    Menu newBlock = new Menu("New Block");

    Map<String, Callable<Block>> blockMap = Map.of(
        "Sine", SineBlock::new,
        "Slider", () -> new SliderBlock(0, 1000, 1),
        "Spinner", () -> new SpinnerBlock(0, 1000, 1, 0.01),
        "Add", AddBlock::new,
        "Multiply", MultiplyBlock::new,
        "Label", LabelBlock::new
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

    Pane subPane = new Pane();
    subPane.prefWidthProperty().bind(pane.widthProperty());
    subPane.prefHeightProperty().bind(pane.heightProperty());
    pane.getChildren().add(0, subPane);

    subPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
      deselectAll();
      if (event.isSecondaryButtonDown()) {
        selectionRectangle = new Rectangle();
        selectionRectangle.setStroke(Paint.valueOf("white"));
        selectionRectangle.setFill(Paint.valueOf("transparent"));
        selectionRectangle.getStrokeDashArray().addAll(5.0, 5.0);
        Point2D mouse = group.sceneToLocal(event.getSceneX(), event.getSceneY());
        selectionRectangle.setX(mouse.getX());
        selectionRectangle.setY(mouse.getY());
        mouseDownX = mouse.getX();
        mouseDownY = mouse.getY();

        group.getChildren().add(selectionRectangle);
      }
    });

    subPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
      if (event.isSecondaryButtonDown()) {
        if (selectionRectangle != null) {
          Point2D mouse = group.sceneToLocal(event.getSceneX(), event.getSceneY());
          selectionRectangle.setX(Math.min(mouse.getX(), mouseDownX));
          selectionRectangle.setWidth(Math.abs(mouse.getX() - mouseDownX));
          selectionRectangle.setY(Math.min(mouse.getY(), mouseDownY));
          selectionRectangle.setHeight(Math.abs(mouse.getY() - mouseDownY));
        }
      }
    });

    subPane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
      if (event.getButton() == MouseButton.SECONDARY) {
        if (selectionRectangle != null) {
          for (Block block : blocks) {
            Node node = block.getNode();
            Bounds blockBounds = node.localToScene(node.getBoundsInLocal());
            Bounds selectionBounds = selectionRectangle.localToScene(selectionRectangle.getBoundsInLocal());
            if (selectionBounds.intersects(blockBounds)) {
              selectBlock(block);
            }
          }
          group.getChildren().remove(selectionRectangle);
          selectionRectangle = null;
        }
      }
    });
  }

  private void selectBlock(Block block) {
    selectedBlocks.add(block);
    block.getNode().setEffect(new ColorAdjust(0, 0, 0.5, 0));
    block.getNode().setCursor(Cursor.MOVE);
  }

  public void addCable(BlockConnection connection, Node newNode) {
    Line line = new Line();
    int sourceIndex = connection.sourceIndex();
    int destIndex = connection.destIndex();
    List<Node> sourceOutputs = connection.source().getOutputNodes();
    List<Node> destInputs = connection.dest().getInputNodes();

    Node output = sourceOutputs.get(sourceIndex);
    Node input = destInputs.get(destIndex);

    ChangeListener<Bounds> bindCableEnd = (observable, oldValue, outputBounds) -> {
      outputBounds = connection.source().getNode().sceneToLocal(output.localToScene(output.getBoundsInLocal()));
      line.startXProperty().bind(connection.source().getNode().layoutXProperty().add(outputBounds.getCenterX()));
      line.startYProperty().bind(connection.source().getNode().layoutYProperty().add(outputBounds.getCenterY()));
    };

    // Once layout is done (indicated by boundsInParentProperty updating), we can get the bounds of
    // the nodes and bind the line to them relative to the source and destination nodes
    output.boundsInParentProperty().addListener(bindCableEnd);

    ChangeListener<Bounds> bindCableStart = (observable, oldValue, inputBounds) -> {
      inputBounds = connection.dest().getNode().sceneToLocal(input.localToScene(input.getBoundsInLocal()));
      line.endXProperty().bind(connection.dest().getNode().layoutXProperty().add(inputBounds.getCenterX()));
      line.endYProperty().bind(connection.dest().getNode().layoutYProperty().add(inputBounds.getCenterY()));
    };

    input.boundsInParentProperty().addListener(bindCableStart);

    bindCableStart.changed(null, null, null);
    bindCableEnd.changed(null, null, null);

    group.getChildren().add(line);
    inputToCableMap.put(connection, line);
  }

  public void addConnection(BlockConnection connection) {
    connection.dest().addConnection(connection);
    addCable(connection, null);
  }

  private void checkForConnectionStart(Block block, MouseEvent event) {
    List<Node> outputs = block.getOutputNodes();
    for (int j = 0; j < outputs.size(); j++) {
      Node output = outputs.get(j);
      Point2D mouse = output.sceneToLocal(event.getSceneX(), event.getSceneY());
      if (output.contains(mouse)) {
        Bounds bounds = block.getNode().sceneToLocal(output.localToScene(output.getBoundsInLocal()));
        startBlock = block;
        outputIndex = j;
        cable = new Line(0, 0, event.getSceneX(), event.getSceneY());
        cable.startXProperty()
            .bind(block.getNode().layoutXProperty().add(bounds.getCenterX()));
        cable.startYProperty()
            .bind(block.getNode().layoutYProperty().add(bounds.getCenterY()));
        group.getChildren().add(cable);
        break;
      }
    }
  }

  private void checkForConnectionEnd(MouseEvent event) {
    boolean connected = false;
    // check if we're over a node
    for (Block endBlock : blocks) {
      if (startBlock != endBlock) {
        List<Node> inputs = endBlock.getInputNodes();
        for (int j = 0; j < inputs.size(); j++) {
          Node input = inputs.get(j);
          Point2D mouse = input.sceneToLocal(event.getSceneX(), event.getSceneY());
          if (input.contains(mouse)) {
            if (cable != null) {
              Bounds endBounds = input.getBoundsInParent();

              // bind the cable to the other node
              cable.endXProperty()
                  .bind(endBlock.getNode().layoutXProperty().add(endBounds.getCenterX()));
              cable.endYProperty()
                  .bind(endBlock.getNode().layoutYProperty().add(endBounds.getCenterY()));

              if (endBlock.getInput(j) == null) {
                BlockConnection blockConnection = new BlockConnection(startBlock, outputIndex,
                    endBlock, j);
                endBlock.addConnection(blockConnection);
                connected = true;
                inputToCableMap.put(blockConnection, cable);
                cable = null;
                startBlock = null;
                outputIndex = -1;
              }
            } else {
              // remove the input from the block
              BlockConnection connection = endBlock.getInput(j);
              if (connection != null) {
                endBlock.removeConnection(connection);
                Node cable = inputToCableMap.remove(connection);
                group.getChildren().remove(cable);
              }
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

  private void selectSingleBlock(Block block, MouseEvent event) {
    deselectAll();
    selectedOffsetsX.clear();
    selectedOffsetsY.clear();
    selectBlock(block);
    selectedOffsetsX.add(event.getSceneX() - block.getNode().getLayoutX());
    selectedOffsetsY.add(event.getSceneY() - block.getNode().getLayoutY());
  }

  public void addBlock(Block block) {
    if (block.type().equals("return")) {
      returnBlock = block;
    }

    blocks.add(block);
    Node node = block.getNode();
    group.getChildren().add(node);

    node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
      if (selectedBlocks.contains(block)) {
        selectedOffsetsX.clear();
        selectedOffsetsY.clear();
        for (Block selectedBlock : selectedBlocks) {
          selectedOffsetsX.add(event.getSceneX() - selectedBlock.getNode().getLayoutX());
          selectedOffsetsY.add(event.getSceneY() - selectedBlock.getNode().getLayoutY());
        }
        event.consume();
      }
      if (event.isPrimaryButtonDown() && !selectedBlocks.contains(block)) {
        checkForConnectionStart(block, event);
      }
      if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
        selectSingleBlock(block, event);
        pane.requestFocus();
        event.consume();
      }
      if (!selectedBlocks.contains(block)) {
        deselectAll();
      }
    });

    node.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
      if (selectedBlocks.contains(block)) {
        for (int i = 0; i < selectedBlocks.size(); i++) {
          Block selectedBlock = selectedBlocks.get(i);
          selectedBlock.getNode().setLayoutX(event.getSceneX() - selectedOffsetsX.get(i));
          selectedBlock.getNode().setLayoutY(event.getSceneY() - selectedOffsetsY.get(i));
        }
        event.consume();
      }
    });

    block.getOutputNodes().forEach(output -> output.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
      if (event.isPrimaryButtonDown() && !selectedBlocks.contains(block)) {
        cable.setEndX(event.getSceneX());
        cable.setEndY(event.getSceneY());
      }
    }));

    node.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
      if (event.getButton() == MouseButton.SECONDARY) {
        Point2D mouse = node.sceneToLocal(event.getSceneX(), event.getSceneY());
        if (node.contains(mouse)) {
          selectSingleBlock(block, event);
        }
      }
      if (event.getButton() == MouseButton.PRIMARY) {
        checkForConnectionEnd(event);
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
      startBlock = null;
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

  public void deselectAll() {
    selectedBlocks.clear();
    for (Block block : blocks) {
      block.getNode().setEffect(null);
      block.getNode().setCursor(Cursor.DEFAULT);
    }
  }

  public void deleteSelected() {
    for (Block block : selectedBlocks) {
      if (!block.type().equals("return")) {
        group.getChildren().remove(block.getNode());
        blocks.remove(block);
      }

      // remove all connections to this block
      for (int i = 0; i < block.totalInputs(); i++) {
        BlockConnection connection = block.getInput(i);
        if (connection != null) {
          block.removeConnection(connection);
          Node cable = inputToCableMap.remove(connection);
          group.getChildren().remove(cable);
        }
      }

      // remove all connections from this block by looking at the other blocks
      for (Block otherBlock : blocks) {
        for (int i = 0; i < otherBlock.totalInputs(); i++) {
          BlockConnection connection = otherBlock.getInput(i);
          if (connection != null && connection.source().equals(block)) {
            otherBlock.removeConnection(connection);
            Node cable = inputToCableMap.remove(connection);
            group.getChildren().remove(cable);
          }
        }
      }

    }
    selectedBlocks.clear();
  }

  public void mergeSelected() {
    List<Block> blocks = selectedBlocks.stream().filter(block -> !block.type().equals("return")).toList();
    if (blocks.size() > 1) {
      ModuleBlock module = new ModuleBlock(blocks);
      for (Block block : blocks) {
        group.getChildren().remove(block.getNode());
        this.blocks.remove(block);
      }
      module.getRemovedConnections().forEach(connection -> {
        Node cable = inputToCableMap.remove(connection);
        group.getChildren().remove(cable);
      });
      addBlock(module);
      module.getNewConnections().forEach(connection -> addCable(connection, module.getNode()));
    }
  }
}
