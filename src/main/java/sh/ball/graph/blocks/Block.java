package sh.ball.graph.blocks;

import javafx.scene.Node;

import java.util.List;
import sh.ball.audio.engine.AudioDeviceListener;

public interface Block extends AudioDeviceListener {
  BlockData process();
  List<Block> getInputs();
  void addInput(Block block);
  int totalInputs();
  int totalOutputs();
  int currentInputs();
  Node getNode();
}
