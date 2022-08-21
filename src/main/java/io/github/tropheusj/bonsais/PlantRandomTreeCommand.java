package io.github.tropheusj.bonsais;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import io.github.tropheusj.bonsais.mixin.SaplingBlockAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;

public class PlantRandomTreeCommand {
	public static final SimpleCommandExceptionType NOT_POT = new SimpleCommandExceptionType(Component.translatable("bonsais.error.not_pot"));
	public static final SimpleCommandExceptionType FILLED = new SimpleCommandExceptionType(Component.translatable("bonsais.error.filled"));
	public static final SimpleCommandExceptionType NONE_VALID = new SimpleCommandExceptionType(Component.translatable("bonsais.error.none_valid"));
	public static final List<SaplingBlock> SAPLINGS = new ObjectArrayList<>();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection environment) {
		dispatcher.register(
				literal("bonsais")
						.requires(source -> source.hasPermission(2))
						.then(literal("plantRandom")
								.then(argument("pos", BlockPosArgument.blockPos())
										.executes(ctx -> {
											BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
											ServerLevel level = ctx.getSource().getLevel();
											BlockEntity be = level.getBlockEntity(pos);
											if (!(be instanceof BonsaiPotBlockEntity pot)) {
												throw NOT_POT.create();
											}
											if (pot.treeData != null) {
												throw FILLED.create();
											}
											SaplingBlock sapling = randomSapling(level.random);
											AbstractTreeGrower grower = ((SaplingBlockAccessor) sapling).bonsais$treeGrower();
											pot.acceptTree(sapling.asItem(), sapling, grower);
											return 1;
										})
								)
						)
		);
	}

	public static SaplingBlock randomSapling(RandomSource random) throws CommandSyntaxException {
		SaplingBlock sapling = null;
		int tries = 0;
		while (BonsaiPotBlockEntity.FAILED.contains(sapling)) {
			if (tries > 10) {
				throw NONE_VALID.create();
			}
			sapling = SAPLINGS.get(random.nextInt(SAPLINGS.size()));
			tries++;
		}
		return sapling;
	}
}
