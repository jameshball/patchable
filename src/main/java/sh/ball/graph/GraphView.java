package sh.ball.graph;

import static sh.ball.gui.Gui.logger;

import java.util.logging.Level;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import sh.ball.audio.engine.AudioEngine;
import sh.ball.graph.blocks.types.ReturnBlock;
import sh.ball.graph.blocks.types.SineBlock;
import sh.ball.graph.blocks.types.SliderBlock;
import sh.ball.graph.blocks.types.SpinnerBlock;

public class GraphView {

  private final Group group = new Group();
  private final Pane pane = new Pane(group);
  private final GraphController controller;

  public GraphView(ContextMenu contextMenu, AudioEngine audioEngine) {
    controller = new GraphController(group, contextMenu, audioEngine);

    pane.setFocusTraversable(true);

    pane.setPrefWidth(300);
    pane.setPrefHeight(300);

    controller.setReturnBlock(new ReturnBlock());
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

  public void setKeyEventProperty(KeyEvent event) {
    controller.setKeyEventProperty(event);
  }

  public Parent getParent() {
    return pane;
  }
}
