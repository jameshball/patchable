package sh.ball.graph.blocks;

import javafx.scene.Node;

import java.util.List;

public interface Block {
  BlockData process();
  List<Block> getInputs();
  void addInput(Block block);
  int totalInputs();
  int totalOutputs();
  int currentInputs();
  Node getNode();
}
