package io.github.tropheusj.bonsais.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;

@Mixin(SaplingBlock.class)
public interface SaplingBlockAccessor {
	@Accessor("treeGrower")
	AbstractTreeGrower bonsais$treeGrower();
}
