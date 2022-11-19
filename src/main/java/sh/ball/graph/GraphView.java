package sh.ball.graph;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.Pane;
import sh.ball.graph.blocks.ReturnBlock;
import sh.ball.graph.blocks.SineBlock;

public class GraphView {

  private final Group group = new Group();
  private final Pane pane = new Pane(group);
  private final ContextMenu contextMenu;

  private final GraphController controller;

  public GraphView(ContextMenu contextMenu) {
    this.contextMenu = contextMenu;
    this.controller = new GraphController(group, contextMenu);

    pane.setFocusTraversable(true);

    pane.setPrefWidth(300);
    pane.setPrefHeight(300);

    controller.addBlock(new ReturnBlock());
    controller.addBlock(new SineBlock());

    pane.setOnKeyPressed(event -> {
      System.out.println(event.getCode());
      if (event.isShiftDown()) {
        System.out.println("Shift down");
        contextMenu.show(pane, 0, 0);
      }
    });
  }

  public Parent getParent() {
    return pane;
  }
}
