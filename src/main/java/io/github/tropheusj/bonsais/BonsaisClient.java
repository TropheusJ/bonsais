package io.github.tropheusj.bonsais;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class BonsaisClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		BlockEntityRendererRegistry.register(Bonsais.BONSAI_POT_TYPE, BonsaiPotBlockEntityRenderer::new);
	}
}
