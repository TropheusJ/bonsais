package io.github.tropheusj.bonsais;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BonsaiPotBlockEntity extends BlockEntity {
	protected BonsaiPotBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	public BonsaiPotBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(Bonsais.BONSAI_POT_TYPE, blockPos, blockState);
	}
}
