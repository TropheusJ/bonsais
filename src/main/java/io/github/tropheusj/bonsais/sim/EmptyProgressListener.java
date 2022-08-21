package io.github.tropheusj.bonsais.sim;

import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

import org.jetbrains.annotations.Nullable;

public enum EmptyProgressListener implements ChunkProgressListener {
	INSTANCE;

	@Override
	public void updateSpawnPos(ChunkPos spawnPos) {
	}

	@Override
	public void onStatusChange(ChunkPos pos, @Nullable ChunkStatus status) {
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}
}
