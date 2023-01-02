package sh.ball.patchable.graph.blocks;

public interface BlockProcessor {

  void process(int sampleNumber, double[] inputs, double[] outputs);
}
