package com.chonkycats;

import com.chonkycats.client.ChonkyCatModel;
import com.chonkycats.client.ChonkyCatRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ChonkyCatsClient implements ClientModInitializer {
    public static final ModelLayerLocation CHONKY_CAT_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(ChonkyCatsMod.MOD_ID, "chonky_cat"), "main");
    public static final ModelLayerLocation CHONKY_CAT_ARMOR_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(ChonkyCatsMod.MOD_ID, "chonky_cat"), "armor");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ChonkyCatsMod.CHONKY_CAT, ChonkyCatRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CHONKY_CAT_LAYER, ChonkyCatModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CHONKY_CAT_ARMOR_LAYER, ChonkyCatModel::createArmorLayer);
    }
}
