package io.github.tropheusj.bonsais;

import org.jetbrains.annotations.ApiStatus.Internal;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface ServerLevelExtensions {
	void bonsais$enterTreeMode();

	boolean bonsais$inTreeMode();

	@Internal
	void bonsais$recordStateChange(BlockPos pos, BlockState state);

	@Internal
	Object2ObjectLinkedOpenHashMap<BlockPos, BlockState> bonsais$getChangedStates();

	// TODO consider long2object
	Object2ObjectLinkedOpenHashMap<BlockPos, BlockState> bonsais$exitTreeMode();
}
