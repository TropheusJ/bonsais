package io.github.tropheusj.bonsais;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.Blocks;

import net.minecraft.world.level.block.entity.BlockEntityType;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Bonsais implements ModInitializer {
	public static final String ID = "bonsais";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final Block BONSAI_POT = new BonsaiPotBlock(QuiltBlockSettings.copyOf(Blocks.TERRACOTTA).noOcclusion());
	public static final Item BONSAI_POT_ITEM = new BlockItem(BONSAI_POT, new QuiltItemSettings());
	public static final BlockEntityType<BonsaiPotBlockEntity> BONSAI_POT_TYPE = FabricBlockEntityTypeBuilder
			.create(BonsaiPotBlockEntity::new, BONSAI_POT).build();

	@Override
	public void onInitialize(ModContainer mod) {
		Registry.register(Registry.BLOCK, id("bonsai_pot"), BONSAI_POT);
		Registry.register(Registry.ITEM, id("bonsai_pot"), BONSAI_POT_ITEM);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, id("bonsai_pot"), BONSAI_POT_TYPE);
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}

	public static <K, V> Object2ObjectLinkedOpenHashMap<K, V> entriesToMap(List<Entry<K, V>> entries) {
		Object2ObjectLinkedOpenHashMap<K, V> map = new Object2ObjectLinkedOpenHashMap<>();
		entries.forEach(e -> map.put(e.getKey(), e.getValue()));
		return map;
	}
}
