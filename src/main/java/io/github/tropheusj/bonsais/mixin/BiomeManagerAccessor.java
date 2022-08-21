package io.github.tropheusj.bonsais.mixin;

import net.minecraft.world.level.biome.BiomeManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeManager.class)
public interface BiomeManagerAccessor {
	@Accessor("biomeZoomSeed")
	long bonsais$biomeZoomSeed();
}