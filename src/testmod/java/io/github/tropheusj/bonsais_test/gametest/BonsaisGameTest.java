package io.github.tropheusj.bonsais_test.gametest;

import io.github.tropheusj.bonsais_test.BonsaisTest;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;

public class BonsaisGameTest implements FabricGameTest {
	@GameTest(template = BonsaisTest.ID + ":minecart_test")
	public void minecartTest(GameTestHelper helper) {
		helper.setBlock(2, 2, 0, Blocks.REDSTONE_BLOCK);
		helper.succeedWhenEntityPresent(EntityType.MINECART, 2, 2, 3);
	}
}
