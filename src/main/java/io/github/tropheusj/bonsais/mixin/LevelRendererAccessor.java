package io.github.tropheusj.bonsais.mixin;

import net.minecraft.client.renderer.LevelRenderer;

import net.minecraft.client.renderer.culling.Frustum;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
	@Accessor("capturedFrustum")
	Frustum bonsais$capturedFrustum();

	@Accessor("cullingFrustum")
	Frustum bonsais$cullingFrustum();
}
