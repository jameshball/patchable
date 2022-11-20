package sh.ball.graph.blocks;

import java.util.Objects;

public class BlockData {

  public final double[] data;

  public BlockData(double... data) {
    this.data = Objects.requireNonNullElseGet(data, () -> new double[0]);
  }

  public BlockData(float... data) {
    this.data = new double[data.length];
    for (int i = 0; i < data.length; i++) {
      this.data[i] = (float) data[i];
    }
  }
}
