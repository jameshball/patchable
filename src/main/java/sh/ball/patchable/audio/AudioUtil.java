package sh.ball.patchable.audio;

public class AudioUtil {
  private static final int MIN_FRAME_LATENCY = 512;

  public static int calculateBufferSize(int sampleRate, int frameSize, int latencyMs) {
    return Math.max((int) (sampleRate * latencyMs * 0.0005), MIN_FRAME_LATENCY) * frameSize;
  }
}
