package sh.ball.patchable.graph.blocks.types;

import java.util.List;
import javafx.scene.paint.Paint;

import sh.ball.patchable.audio.engine.AudioDevice;
import sh.ball.patchable.graph.blocks.BlockDesigner;
import sh.ball.patchable.graph.blocks.BlockPort;

public class SineBlock extends BasicBlock {

  private static final int DEFAULT_SAMPLE_RATE = 192000;
  private double phase = 0;
  private int sampleRate = DEFAULT_SAMPLE_RATE;

  public SineBlock() {
    super(
        List.of(new BlockPort("Frequency"), new BlockPort("Phase offset")),
        List.of(new BlockPort("Sine value")),
        BlockDesigner.GREEN,
        "Sine"
    );
    setProcessor((sampleNumber, inputs, outputs) -> {
      double frequency = inputs[0];
      double phaseOffset = inputs[1];
      phase += frequency / sampleRate;
      outputs[0] = Math.sin(phase * 2 * Math.PI + phaseOffset * 2 * Math.PI);
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
