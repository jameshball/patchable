package sh.ball.patchable.audio.engine;

public interface AudioInputListener {
  void transmit(double[] samples);
}
