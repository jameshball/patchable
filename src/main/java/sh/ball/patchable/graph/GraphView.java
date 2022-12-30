package sh.ball.patchable.graph;

import static sh.ball.patchable.gui.Gui.logger;

import java.util.logging.Level;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import sh.ball.patchable.audio.engine.AudioEngine;
import sh.ball.patchable.graph.blocks.types.ReturnBlock;
import sh.ball.patchable.graph.blocks.types.SineBlock;
import sh.ball.patchable.graph.blocks.types.SliderBlock;

public class GraphView {

  private final Group group = new Group();
  private final Pane pane = new Pane(group);
  private final GraphController controller;

  public GraphView(ContextMenu contextMenu, AudioEngine audioEngine) {
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

  public void deselectAll() {
    controller.deselectAll();
  }
}
