package sh.ball.graph.blocks;

import javafx.scene.Node;

import java.util.List;
import sh.ball.audio.engine.AudioDeviceListener;

public interface Block extends AudioDeviceListener {
  double process(int sampleNumber, int index);
  List<Block> getInputs();
  void setInput(Block block, int index);
  void removeInput(int index);
  int totalInputs();
  int totalOutputs();
  int currentInputs();
  Node getNode();
  List<Node> getInputNodes();
  List<Node> getOutputNodes();
}
