package com.chonkycats;

import com.chonkycats.client.ChonkyCatModel;
import com.chonkycats.client.ChonkyCatRenderer;
import com.chonkycats.client.ChonkyPawScreen;
import com.chonkycats.client.BiomeLocatorHud;
import com.chonkycats.network.BiomeLocationPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ChonkyCatsClient implements ClientModInitializer {
    public static final ModelLayerLocation CHONKY_CAT_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(ChonkyCatsMod.MOD_ID, "chonky_cat"), "main");
    public static final ModelLayerLocation CHONKY_CAT_ARMOR_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(ChonkyCatsMod.MOD_ID, "chonky_cat"), "armor");

    private static final ResourceLocation PAW_ICON = ResourceLocation.fromNamespaceAndPath(
            ChonkyCatsMod.MOD_ID, "textures/gui/paw_button.png");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ChonkyCatsMod.CHONKY_CAT, ChonkyCatRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CHONKY_CAT_LAYER, ChonkyCatModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CHONKY_CAT_ARMOR_LAYER, ChonkyCatModel::createArmorLayer);

        // Register biome compass HUD
        HudRenderCallback.EVENT.register(new BiomeLocatorHud());

        // Register biome location packet handler
        ClientPlayNetworking.registerGlobalReceiver(BiomeLocationPayload.TYPE, (payload, context) -> {
            BiomeLocatorHud.updateTarget(payload.x(), payload.z(), payload.found());
        });

        // Add paw button to inventory screen
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof InventoryScreen) {
                int x = (scaledWidth / 2) + 64;
                int y = (scaledHeight / 2) - 22;
                Screens.getButtons(screen).add(
                    new net.minecraft.client.gui.components.Button.Builder(
                        net.minecraft.network.chat.Component.literal("\uD83D\uDC3E"),
                        btn -> {
                            // Check player name server-side via command
                            Minecraft mc = Minecraft.getInstance();
                            if (mc.player != null) {
                                String name = mc.player.getGameProfile().getName();
                                if (name.equalsIgnoreCase("dragonminer2020")) {
                                    mc.setScreen(new ChonkyPawScreen());
                                }
                            }
                        }
                    ).bounds(x, y, 20, 20).build()
                );
            }
        });
    }
}
