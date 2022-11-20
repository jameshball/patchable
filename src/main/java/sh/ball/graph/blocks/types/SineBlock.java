package sh.ball.graph.blocks.types;

import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.List;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.audio.engine.AudioDeviceListener;
import sh.ball.graph.blocks.Block;
import sh.ball.graph.blocks.BlockData;
import sh.ball.graph.blocks.BlockDesigner;

public class SineBlock implements Block {

  private static final int DEFAULT_SAMPLE_RATE = 192000;

  private Block input = null;
  private double phase = 0;
  private int sampleRate = DEFAULT_SAMPLE_RATE;

  public SineBlock() {}

  @Override
  public BlockData process() {
    double frequency = 0;
    if (input != null) {
      BlockData data = input.process();
      frequency = data.data[0];
    }
    phase += frequency / sampleRate;
    return new BlockData(Math.sin(phase * 2 * Math.PI));
  }

  @Override
  public List<Block> getInputs() {
    return List.of();
  }

  @Override
  public void addInput(Block block) {
    if (input != null) {
      throw new IllegalStateException("SineBlock already has an input");
    }
    if (block.totalOutputs() != 1) {
      throw new IllegalArgumentException("SineBlock only accepts blocks with 1 output");
    }
    input = block;
  }

  @Override
  public int totalInputs() {
    return 1;
  }

  @Override
  public int totalOutputs() {
    return 2;
  }

  @Override
  public int currentInputs() {
    return input == null ? 0 : input.totalOutputs();
  }

  @Override
  public Node getNode() {
    return BlockDesigner.createNode(Paint.valueOf("green"), "Sine", 70, 20);
  }

  @Override
  public void audioDeviceChanged(AudioDevice audioDevice) {
    sampleRate = audioDevice.sampleRate();
  }
}
