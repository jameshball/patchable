package sh.ball.patchable.graph.blocks;

import javafx.scene.Node;

import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sh.ball.patchable.audio.engine.AudioDeviceListener;

public interface Block extends AudioDeviceListener {
  double process(int sampleNumber, int index);
  List<BlockConnection> getInputs();
  void setInput(BlockConnection input);
  BlockConnection getInput(int index);
  BlockConnection removeInput(int index);
  int totalInputs();
  int totalOutputs();
  int currentInputs();
  Node getNode();
  List<Node> getInputNodes();
  List<Node> getOutputNodes();
  List<Element> save(Document document);
  void load(Element root);
  String type();
}
