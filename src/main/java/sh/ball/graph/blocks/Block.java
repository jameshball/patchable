package sh.ball.graph.blocks;

import javafx.scene.Node;

import java.util.List;
import sh.ball.audio.engine.AudioDeviceListener;

public interface Block extends AudioDeviceListener {
  double process(int sampleNumber, int index);
  List<BlockInput> getInputs();
  void setInput(BlockInput input, int index);
  void removeInput(int index);
  int totalInputs();
  int totalOutputs();
  int currentInputs();
  Node getNode();
  List<Node> getInputNodes();
  List<Node> getOutputNodes();
}
