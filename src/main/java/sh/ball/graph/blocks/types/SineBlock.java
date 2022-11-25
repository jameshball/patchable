package sh.ball.graph.blocks.types;

import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

import sh.ball.audio.engine.AudioDevice;

public class SineBlock extends BasicBlock {

  private static final int DEFAULT_SAMPLE_RATE = 192000;
  private double phase = 0;
  private int sampleRate = DEFAULT_SAMPLE_RATE;

  public SineBlock() {
    super(1, 1, Paint.valueOf("green"), "Sine");
    setProcessor((inputs, outputs) -> {
      double frequency = inputs[0];
      phase += frequency / sampleRate;
      outputs[0] = Math.sin(phase * 2 * Math.PI);
    });
  }

  @Override
  public void audioDeviceChanged(AudioDevice audioDevice) {
    sampleRate = audioDevice.sampleRate();
  }
}
