package io.github.tropheusj.bonsais.sim;

import io.github.tropheusj.bonsais.Bonsais;
import io.github.tropheusj.bonsais.ServerLevelExtensions;
import io.github.tropheusj.bonsais.mixin.BiomeManagerAccessor;
import io.github.tropheusj.bonsais.mixin.MinecraftServerAccessor;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.ServerLevelData;

import javax.annotation.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

// a good amount of this is yoinked from Create, licenced under MIT.
public class TreePlacementSimulationLevel extends ServerLevel {
	public final ServerLevel beLevel;
	public Map<BlockPos, BlockState> modifiedStates = new LinkedHashMap<>();

	public TreePlacementSimulationLevel(ServerLevel beLevel, MinecraftServer minecraftServer, Executor executor,
										LevelStorageAccess levelStorageAccess, ServerLevelData serverLevelData,
										ResourceKey<Level> resourceKey, LevelStem levelStem,
										ChunkProgressListener chunkProgressListener, boolean bl, long l,
										List<CustomSpawner> list, boolean bl2) {

		super(minecraftServer, executor, levelStorageAccess, serverLevelData,
				resourceKey, levelStem, chunkProgressListener, bl, l, list, bl2);
		this.beLevel = beLevel;
	}

	@Nullable
	public static TreePlacementSimulationLevel create(ServerLevel beLevel) {
		MinecraftServer server = beLevel.getServer();
		MinecraftServerAccessor serverAccess = (MinecraftServerAccessor) server;
		Executor executor = Util.backgroundExecutor(); // from server's constructor
		LevelStorageAccess access = serverAccess.bonsais$storageSource();
		LevelData data = beLevel.getLevelData();
		if (!(data instanceof ServerLevelData serverData)) {
			Bonsais.LOGGER.error("Could not create sim level, unexpected data type. found [{}]", data.getClass().getName());
			return null;
		}
		ResourceKey<Level> dimension = beLevel.dimension();
		ServerLevelExtensions serverLevelExtensions = ((ServerLevelExtensions) beLevel);
		LevelStem levelStem = serverLevelExtensions.bonsais$levelStem();
		ChunkProgressListener listener = EmptyProgressListener.INSTANCE;
		boolean debug = beLevel.isDebug();
		BiomeManagerAccessor managerAccess = (BiomeManagerAccessor) beLevel.getBiomeManager();
		long biomeZoomSeed = managerAccess.bonsais$biomeZoomSeed();
		List<CustomSpawner> customSpawners = Collections.emptyList();
		boolean tickTime = false;

		return new TreePlacementSimulationLevel(beLevel, server, executor, access, serverData,
				dimension, levelStem, listener, debug, biomeZoomSeed, customSpawners, tickTime);
	}

	public Map<BlockPos, BlockState> finishSimulation() {
		Map<BlockPos, BlockState> states = modifiedStates;
		modifiedStates = new LinkedHashMap<>();
		return states;
	}

	@Override
	public boolean setBlock(BlockPos pos, BlockState newState, int flags) {
		modifiedStates.put(pos.immutable(), newState);
		return true;
	}

	@Override
	public boolean setBlockAndUpdate(BlockPos pos, BlockState state) {
		return setBlock(pos, state, 0);
	}

	@Override
	public boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> condition) {
		return condition.test(getBlockState(pos));
	}

	@Override
	public boolean isLoaded(BlockPos pos) {
		return true;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (modifiedStates.containsKey(pos))
			return modifiedStates.get(pos);
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public float getSunAngle(float p_72826_1_) {
		return 0;
	}

	@Override
	public int getMaxLocalRawBrightness(BlockPos pos) {
		return 15;
	}

	@Override
	public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
	}

	@Override
	public void levelEvent(Player player, int type, BlockPos pos, int data) {
	}

	@Override
	public List<ServerPlayer> players() {
		return Collections.emptyList();
	}

	@Override
	public void playSound(Player player, double x, double y, double z, SoundEvent soundIn, SoundSource category,
						  float volume, float pitch) {
	}

	@Override
	public void playSound(Player p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundSource p_217384_4_,
						  float p_217384_5_, float p_217384_6_) {
	}

	@Override
	public Entity getEntity(int id) {
		return null;
	}

	@Override
	public MapItemSavedData getMapData(String mapName) {
		return null;
	}

	@Override
	public boolean addFreshEntity(Entity entityIn) {
		return false;
	}

	@Override
	public void setMapData(String mapId, MapItemSavedData mapDataIn) {
	}

	@Override
	public int getFreeMapId() {
		return 0;
	}

	@Override
	public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
	}

	@Override
	public RecipeManager getRecipeManager() {
		return beLevel.getRecipeManager();
	}

	@Override
	public Holder<Biome> getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
		return beLevel.getUncachedNoiseBiome(p_225604_1_, p_225604_2_, p_225604_3_);
	}
}
