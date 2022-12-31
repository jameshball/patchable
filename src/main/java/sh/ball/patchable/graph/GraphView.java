package sh.ball.patchable.graph;

import static sh.ball.patchable.gui.Gui.logger;

import java.util.logging.Level;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sh.ball.patchable.audio.engine.AudioEngine;
import sh.ball.patchable.graph.blocks.types.ReturnBlock;
import sh.ball.patchable.graph.blocks.types.SineBlock;
import sh.ball.patchable.graph.blocks.types.SliderBlock;

public class GraphView {

  private final Group group = new Group();
  private final Pane pane = new Pane(group);
  private final GraphController controller;
  private final ContextMenu contextMenu;

  public GraphView(ContextMenu contextMenu, AudioEngine audioEngine) {
    this.contextMenu = contextMenu;
    controller = new GraphController(pane, group, contextMenu, audioEngine);

    pane.setFocusTraversable(true);

    pane.setPrefWidth(300);
    pane.setPrefHeight(300);

    controller.addBlock(new ReturnBlock());
    controller.addBlock(new SineBlock());
    controller.addBlock(new SliderBlock(0, 1000, 440));

    new Thread(() -> {
      try {
        controller.start();
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Error in graph controller", e);
      }
    }).start();
  }

  public Parent getParent() {
    return pane;
  }

  public void save() {
    controller.save();
  }

  public void load() {
    controller.load();
  }

  public EventHandler<? super KeyEvent> keyEventHandler(Stage stage, Scene scene) {
    return e -> {
      if (e.isShiftDown()) {
        contextMenu.show(scene.getWindow(), stage.getX(), stage.getY() + stage.getHeight() / 2 - 20);
      }
      if (e.isControlDown() && e.getCode().getName().equals("S")) {
        save();
      }
      if (e.isControlDown() && e.getCode().getName().equals("O")) {
        load();
      }
      if (e.getCode().equals(KeyCode.ESCAPE)) {
        controller.deselectAll();
      }
      if (e.getCode().equals(KeyCode.DELETE)) {
        controller.deleteSelected();
      }
    };
  }
}
