package io.github.tropheusj.bonsais;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;

import net.minecraft.world.phys.AABB;

import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class BonsaiPotBlockEntity extends BlockEntity implements QuiltBlockEntity {
	public static final List<SaplingBlock> FAILED = new ArrayList<>();

	public Object2ObjectLinkedOpenHashMap<BlockPos, BlockState> treeData;
	public long ticksAtTreeAdd = -1;
	public final AABB bounds;

	protected BonsaiPotBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		Vec3 pos = new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
		this.bounds = AABB.ofSize(pos, 1, 3, 1);
	}

	public BonsaiPotBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(Bonsais.BONSAI_POT_TYPE, blockPos, blockState);
	}

	public void tick(Level level, BlockPos pos, BlockState state) {
	}

	public long getTreeAge() {
		return level.getGameTime() - ticksAtTreeAdd;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.ticksAtTreeAdd = tag.contains("TicksAtTreeAdd") ? tag.getLong("TicksAtTreeAdd") : -1;
		if (!tag.contains("TreeData"))
			return;
		ListTag treeData = tag.getList("TreeData", Tag.TAG_COMPOUND);
		this.treeData = new Object2ObjectLinkedOpenHashMap<>();
		treeData.forEach((element) -> {
			if (element instanceof CompoundTag compound) {
				int x = compound.getInt("X");
				int y = compound.getInt("Y");
				int z = compound.getInt("Z");
				int id = compound.getInt("ID");
				BlockPos pos = new BlockPos(x, y, z);
				BlockState state = Block.stateById(id);
				this.treeData.put(pos, state);
			}
		});
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		if (treeData != null) {
			tag.putLong("TicksAtTreeAdd", ticksAtTreeAdd);
			ListTag list = new ListTag();
			treeData.forEach((pos, state) -> {
				CompoundTag compound = new CompoundTag();
				compound.putInt("X", pos.getX());
				compound.putInt("Y", pos.getY());
				compound.putInt("Z", pos.getZ());
				compound.putInt("ID", Block.getId(state));
				list.add(compound);
			});
			tag.put("TreeData", list);
		}
		return tag;
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public boolean acceptTree(SaplingBlock sapling, AbstractTreeGrower grower) {
		if (!(level instanceof ServerLevel serverLevel))
			return false;
		ChunkGenerator generator = serverLevel.getChunkSource().getGenerator();
		BlockState state = sapling.defaultBlockState();
		ServerLevelExtensions ex = (ServerLevelExtensions) serverLevel;
		ex.bonsais$enterTreeMode();
		grower.growTree(serverLevel, generator, BlockPos.ZERO, state, serverLevel.random);
		treeData = ex.bonsais$exitTreeMode();
		if (treeData.isEmpty()) {
			treeData = null;
			if (!FAILED.contains(sapling)) {
				FAILED.add(sapling);
				Bonsais.LOGGER.error("Failed to generate sapling: [{}]", Registry.BLOCK.getKey(sapling));
			}
			return false;
		}
		sortTreeData();
		ticksAtTreeAdd = serverLevel.getGameTime();
		setChanged();
		sync();
		return true;
	}

	public void sortTreeData() {
		treeData = Bonsais.entriesToMap(treeData
				.entrySet()
				.stream()
				.sorted((e1, e2) -> {
					BlockPos pos1 = e1.getKey();
					BlockPos pos2 = e2.getKey();
					return Integer.compare(pos1.distManhattan(BlockPos.ZERO), pos2.distManhattan(BlockPos.ZERO));
				})
				.toList());
	}
}
