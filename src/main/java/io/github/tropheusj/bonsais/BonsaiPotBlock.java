package io.github.tropheusj.bonsais;

import org.jetbrains.annotations.Nullable;

import io.github.tropheusj.bonsais.mixin.SaplingBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BonsaiPotBlock extends BaseEntityBlock {
	public static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 6, 15);
	public static final BooleanProperty FILLED = BooleanProperty.create("filled");

	public BonsaiPotBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FILLED, Boolean.FALSE));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FILLED);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack held = player.getItemInHand(hand);
		if (state.getValue(FILLED)) {
			return InteractionResult.PASS;
		}
		if (held.getItem() instanceof BlockItem blockItem
				&& blockItem.getBlock() instanceof SaplingBlock sapling
				&& sapling instanceof SaplingBlockAccessor access
				&& world.getBlockEntity(pos) instanceof BonsaiPotBlockEntity pot) {

			if (world.isClientSide()) {
				return InteractionResult.SUCCESS;
			}
			AbstractTreeGrower treeGrower = access.bonsais$treeGrower();
			boolean accepted = pot.acceptTree(blockItem, sapling, treeGrower);
			if (accepted) {
				world.setBlockAndUpdate(pos, state.setValue(FILLED, Boolean.TRUE));
				if (!player.isCreative()) {
					ItemStack newHeld = held.copy();
					newHeld.shrink(1);
					if (newHeld.isEmpty())
						newHeld = ItemStack.EMPTY;
					player.setItemInHand(hand, newHeld);
				}
				world.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1, 1);
				return InteractionResult.CONSUME;
			}
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	// TODO
//	@Override
//	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
//		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
//		if (blockEntity instanceof BonsaiPotBlockEntity pot && pot.item != null) {
//			builder = builder.withDynamicDrop(ITEM,
//					(context, consumer) -> consumer.accept(pot.item.getDefaultInstance()));
//		}
//		return super.getDrops(state, builder);
//	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return Bonsais.BONSAI_POT_TYPE.create(pos, state);
	}

//	@Nullable
//	@Override
//	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
//		return createTickerHelper(type, Bonsais.BONSAI_POT_TYPE,
//				(level, pos, state2, be) -> be.tick(level, pos, state2));
//	}
}
