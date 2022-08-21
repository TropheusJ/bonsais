package io.github.tropheusj.bonsais.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.tropheusj.bonsais.Bonsais;
import io.github.tropheusj.bonsais.ServerLevelExtensions;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Level.class)
public class LevelMixin {
	@Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true)
	private void bonsais$getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
		if (this instanceof ServerLevelExtensions ex && ex.bonsais$inTreeMode()) {
			Object2ObjectLinkedOpenHashMap<BlockPos, BlockState> changes = ex.bonsais$getChangedStates();
			BlockState defaultState = Bonsais.DEFAULTS.getOrDefault(pos, Bonsais.AIR);
			BlockState state = changes.getOrDefault(pos, defaultState);
			cir.setReturnValue(state);
		}
	}

//	@Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
//	private void bonsais$getFluidState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
//		if (this instanceof ServerLevelExtensions ex && ex.bonsais$inTreeMode()) {
//			// TODO
//		}
//	}

	@Inject(
			method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getChunkAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/chunk/LevelChunk;"
			),
			cancellable = true
	)
	private void bonsais$setBlock(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
		if (this instanceof ServerLevelExtensions ex && ex.bonsais$inTreeMode()) {
			ex.bonsais$recordStateChange(pos.immutable(), state);
			cir.setReturnValue(true);
		}
	}

	@Inject(
			method = "removeBlock",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private void bonsais$removeBlock(BlockPos pos, boolean move, CallbackInfoReturnable<Boolean> cir) {
		if (this instanceof ServerLevelExtensions ex && ex.bonsais$inTreeMode()) {
			ex.bonsais$recordStateChange(pos.immutable(), Bonsais.AIR);
			cir.setReturnValue(true);
		}
	}

	@Inject(
			method = "destroyBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
			),
			cancellable = true
	)
	private void bonsais$destroyBlock(BlockPos pos, boolean drop, Entity breakingEntity, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
		if (this instanceof ServerLevelExtensions ex && ex.bonsais$inTreeMode()) {
			ex.bonsais$recordStateChange(pos.immutable(), Bonsais.AIR);
			cir.setReturnValue(true);
		}
	}

	@Inject(at = @At("HEAD"), cancellable = true, method = {
			"addBlockEntityTicker",
			"updateSkyBrightness",
			"setSpawnSettings",
			"prepareWeather",
			"blockEntityChanged",
			"setThunderLevel",
			"setRainLevel",
			"updateNeighbourForOutputSignal",
			"setSkyFlashTime"
	})
	private void bonsais$simulateVoidReturns(CallbackInfo ci) {
		if (this instanceof ServerLevelExtensions ex && ex.bonsais$inTreeMode()) {
			ci.cancel();
		}
	}
}
