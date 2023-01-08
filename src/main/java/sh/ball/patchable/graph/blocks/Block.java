package sh.ball.patchable.graph.blocks;

import javafx.scene.Node;

import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sh.ball.patchable.audio.engine.AudioDeviceListener;

public interface Block extends AudioDeviceListener {
  double process(int sampleNumber, int index);
  List<BlockConnection> getInputs();
  List<List<BlockConnection>> getOutputs();
  List<BlockPort> getInputPorts();
  List<BlockPort> getOutputPorts();
  void addInput(BlockConnection connection);
  void addOutput(BlockConnection connection);
  void addConnection(BlockConnection connection);
  BlockConnection getInput(int index);
  void removeConnection(BlockConnection connection);
  BlockConnection removeInput(int index);
  void removeOutput(BlockConnection input);
  String type();
  int totalInputs();
  int totalOutputs();
  int currentInputs();
  Node getNode();
  void addNonInheritableNode(Node node);
  void addInheritableNode(Node node);
  List<Node> getInheritableNodes();
  List<Node> getInputNodes();
  List<Node> getOutputNodes();
  List<Element> save(Document document);
  void load(Element root);
}
