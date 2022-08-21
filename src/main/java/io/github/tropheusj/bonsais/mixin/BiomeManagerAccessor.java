package io.github.tropheusj.bonsais.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.biome.BiomeManager;

@Mixin(BiomeManager.class)
public interface BiomeManagerAccessor {
	@Accessor("biomeZoomSeed")
	long bonsais$biomeZoomSeed();
}
