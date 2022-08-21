package io.github.tropheusj.bonsais;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tropheusj.bonsais.mixin.LevelRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Map.Entry;

public class BonsaiPotBlockEntityRenderer implements BlockEntityRenderer<BonsaiPotBlockEntity> {
	public static final int TICKS_PER_GROWTH = 10;

	public final BlockRenderDispatcher blockRenderer;

	public BonsaiPotBlockEntityRenderer(Context ctx) {
		this.blockRenderer = ctx.getBlockRenderDispatcher();
	}

	@Override
	public void render(BonsaiPotBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		Map<BlockPos, BlockState> treeData = entity.treeData;
		if (treeData == null)
			return;
		if (!visible(entity))
			return;
		matrices.pushPose();
		matrices.translate(0.45, 0.25, 0.45);
		matrices.scale(0.1f, 0.1f, 0.1f);
		int totalBlocks = treeData.size();
		long toRender = entity.getTreeAge() / TICKS_PER_GROWTH;
		int rendered = 0;
		for (Entry<BlockPos, BlockState> entry : treeData.entrySet()) {
			if (rendered > toRender)
				break;
			matrices.pushPose();
			BlockPos pos = entry.getKey();
			matrices.translate(pos.getX(), pos.getY(), pos.getZ());
			BlockState state = entry.getValue();
			blockRenderer.renderSingleBlock(state, matrices, vertexConsumers, light, overlay);
			matrices.popPose();
			rendered++;
		}
		matrices.popPose();
	}

	public boolean visible(BonsaiPotBlockEntity entity) {
		LevelRendererAccessor levelRendererAccess = (LevelRendererAccessor) Minecraft.getInstance().levelRenderer;
		Frustum captured = levelRendererAccess.bonsais$capturedFrustum();
		Frustum toUse = captured == null ? levelRendererAccess.bonsais$cullingFrustum() : captured;
		return toUse.isVisible(entity.bounds);
	}
}
