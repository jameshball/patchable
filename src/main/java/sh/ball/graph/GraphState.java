package sh.ball.graph;


import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sh.ball.graph.blocks.Block;
import sh.ball.graph.blocks.BlockFactory;
import sh.ball.graph.blocks.BlockConnection;

public record GraphState(List<Block> blocks, List<BlockConnection> connections) {

  public void save(File file) throws Exception {
    DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
    Document document = documentBuilder.newDocument();

    Element root = document.createElement("project");
    document.appendChild(root);

    Element blocksElement = document.createElement("blocks");
    for (int i = 0; i < blocks.size(); i++) {
      Block block = blocks.get(i);
      Element blockElement = document.createElement("block");
      List<Element> blockElements = block.save(document);

      for (Element element : blockElements) {
        blockElement.appendChild(element);
      }

      blockElement.setAttribute("type", block.type());
      blockElement.setAttribute("id", String.valueOf(i));
      blockElement.setAttribute("layoutX", String.valueOf(block.getNode().getLayoutX()));
      blockElement.setAttribute("layoutY", String.valueOf(block.getNode().getLayoutY()));
      blocksElement.appendChild(blockElement);
    }

    root.appendChild(blocksElement);

    Element connectionsElement = document.createElement("connections");
    for (BlockConnection connection : connections) {
      Element connectionElement = document.createElement("connection");
      connectionElement.setAttribute("source", String.valueOf(blocks.indexOf(connection.source())));
      connectionElement.setAttribute("sourceIndex", String.valueOf(connection.sourceIndex()));
      connectionElement.setAttribute("dest", String.valueOf(blocks.indexOf(connection.dest())));
      connectionElement.setAttribute("destIndex", String.valueOf(connection.destIndex()));
      connectionsElement.appendChild(connectionElement);
    }

    root.appendChild(connectionsElement);

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource domSource = new DOMSource(document);
    StreamResult streamResult = new StreamResult(file);

    transformer.transform(domSource, streamResult);
  }

  public static GraphState load(InputStream inputStream) throws Exception {
    List<Block> blocks = new ArrayList<>();
    List<BlockConnection> blockConnections = new ArrayList<>();

    DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
    documentFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

    Document document = documentBuilder.parse(inputStream);

    document.getDocumentElement().normalize();

    Element root = document.getDocumentElement();
    Element blocksElement = (Element) root.getElementsByTagName("blocks").item(0);
    Map<Integer, Block> blockMap = new HashMap<>();

    for (int i = 0; i < blocksElement.getElementsByTagName("block").getLength(); i++) {
      Element blockElement = (Element) blocksElement.getElementsByTagName("block").item(i);
      String type = blockElement.getAttribute("type");
      int id = Integer.parseInt(blockElement.getAttribute("id"));
      double layoutX = Double.parseDouble(blockElement.getAttribute("layoutX"));
      double layoutY = Double.parseDouble(blockElement.getAttribute("layoutY"));

      Block block = BlockFactory.create(type);
      block.load(blockElement);
      blockMap.put(id, block);
      blocks.add(block);
      block.getNode().setLayoutX(layoutX);
      block.getNode().setLayoutY(layoutY);
    }

    Element connectionsElement = (Element) root.getElementsByTagName("connections").item(0);

    for (int i = 0; i < connectionsElement.getElementsByTagName("connection").getLength(); i++) {
      Element connectionElement = (Element) connectionsElement.getElementsByTagName("connection")
          .item(i);
      int source = Integer.parseInt(connectionElement.getAttribute("source"));
      int sourceIndex = Integer.parseInt(connectionElement.getAttribute("sourceIndex"));
      int dest = Integer.parseInt(connectionElement.getAttribute("dest"));
      int destIndex = Integer.parseInt(connectionElement.getAttribute("destIndex"));
      blockConnections.add(
          new BlockConnection(blockMap.get(source), sourceIndex, blockMap.get(dest), destIndex));
    }

    return new GraphState(blocks, blockConnections);
  }

}
