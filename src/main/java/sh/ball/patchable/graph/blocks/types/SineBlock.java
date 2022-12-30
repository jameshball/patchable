package sh.ball.patchable.graph.blocks.types;

import javafx.scene.paint.Paint;

import sh.ball.patchable.audio.engine.AudioDevice;

public class SineBlock extends BasicBlock {

  private static final int DEFAULT_SAMPLE_RATE = 192000;
  private double phase = 0;
  private int sampleRate = DEFAULT_SAMPLE_RATE;

  public SineBlock() {
    super(1, 1, Paint.valueOf("#006700"), "Sine");
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

  @Override
  public String type() {
    return "sine";
  }
}
