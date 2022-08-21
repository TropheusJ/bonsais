package io.github.tropheusj.bonsais.mixin;

import io.github.tropheusj.bonsais.ServerLevelExtensions;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements ServerLevelExtensions {

	@Unique
	private Object2ObjectLinkedOpenHashMap<BlockPos, BlockState> bonsais$states = null;

	@Override
	public void bonsais$enterTreeMode() {
		bonsais$states = new Object2ObjectLinkedOpenHashMap<>();
	}

	@Override
	public boolean bonsais$inTreeMode() {
		return bonsais$states != null;
	}

	@Override
	public void bonsais$recordStateChange(BlockPos pos, BlockState state) {
		bonsais$states.put(pos, state);
	}

	@Override
	public Object2ObjectLinkedOpenHashMap<BlockPos, BlockState> bonsais$getChangedStates() {
		return bonsais$states;
	}

	@Override
	public Object2ObjectLinkedOpenHashMap<BlockPos, BlockState> bonsais$exitTreeMode() {
		Object2ObjectLinkedOpenHashMap<BlockPos, BlockState> changes = bonsais$states;
		bonsais$states = null;
		return changes;
	}

	// TODO
	// account for getting added entities and fluids, block entities

	@Inject(at = @At("HEAD"), cancellable = true, method = {
			"addPlayer",
			"removePlayerImmediately",
			"playSeededSound*",
			"globalLevelEvent",
			"levelEvent",
			"gameEvent",
			"sendGameEvents",
			"handleGameEventMessagesInQueue",
			"sendBlockUpdated",
			"updateNeighborsAt",
			"updateNeighborsAtExceptFromFacing",
			"neighborChanged*",
			"broadcastEntityEvent",
			"blockEvent",
			"runBlockEvents",
			"setMapData",
			"setDefaultSpawnPos",
			"onBlockStateChange",
			"onReputationEvent",
			"clearBlockEvents",
			"blockUpdated",
			"addLegacyChunkEntities",
			"addWorldGenChunkEntities",
			"startTickingChunk",
			"onStructureStartsAvailable"
	})
	private void bonsais$simulateServerVoidReturns(CallbackInfo ci) {
		if (bonsais$inTreeMode()) {
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), cancellable = true, method = {
			"addEntity",
			"doBlockEvent",
			"sendParticles(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/particles/ParticleOptions;ZDDDIDDDD)Z",
			"sendParticles(Lnet/minecraft/server/level/ServerPlayer;ZDDDLnet/minecraft/network/protocol/Packet;)Z",
			"setChunkForced"
	})
	private void bonsais$simulateBooleanReturns(CallbackInfoReturnable<Boolean> cir) {
		if (bonsais$inTreeMode()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(at = @At("HEAD"), cancellable = true, method = {
			"sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I",
			"doBlockEvent"
	})
	private void bonsais$simulateIntegerReturns(CallbackInfoReturnable<Integer> cir) {
		if (bonsais$inTreeMode()) {
			cir.setReturnValue(0);
		}
	}
}
