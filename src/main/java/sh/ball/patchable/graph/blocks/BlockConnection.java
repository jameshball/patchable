package sh.ball.patchable.graph.blocks;

public record BlockConnection(Block source, int sourceIndex, Block dest, int destIndex) {}
