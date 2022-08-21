package io.github.tropheusj.bonsais.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
	@Accessor("capturedFrustum")
	Frustum bonsais$capturedFrustum();

	@Accessor("cullingFrustum")
	Frustum bonsais$cullingFrustum();
}
