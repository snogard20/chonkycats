package com.chonkycats.client;

import com.chonkycats.ChonkyCatsMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Axis;

public class BiomeLocatorHud implements HudRenderCallback {

    // Updated by network packet
    private static int targetX = 0;
    private static int targetZ = 0;
    private static boolean biomeFound = false;
    private static long lastUpdateTick = -1;

    public static void updateTarget(int x, int z, boolean found) {
        targetX = x;
        targetZ = z;
        biomeFound = found;
        lastUpdateTick = System.currentTimeMillis();
    }

    @Override
    public void onHudRender(GuiGraphics graphics, DeltaTracker tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Only show HUD when holding the biome compass
        boolean holdingCompass = isHoldingBiomeCompass(mc.player);
        if (!holdingCompass) return;

        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();

        // HUD position: top-center, below hotbar area
        int centerX = screenWidth / 2;
        int hudY = 4;

        if (lastUpdateTick < 0) {
            // Never searched yet
            drawHudBackground(graphics, centerX - 60, hudY - 2, 120, 22);
            graphics.drawCenteredString(font, "\u00a7e\u00a7l\u2726 \u00a7r\u00a77Right-click to scan", centerX, hudY + 4, 0xFFFFFF);
            return;
        }

        long age = System.currentTimeMillis() - lastUpdateTick;
        if (age > 120_000) {
            // Data is stale (>2 min) — show faded prompt
            drawHudBackground(graphics, centerX - 55, hudY - 2, 110, 22);
            graphics.drawCenteredString(font, "\u00a78\u2726 Scan expired - right-click", centerX, hudY + 4, 0x888888);
            return;
        }

        if (!biomeFound) {
            drawHudBackground(graphics, centerX - 55, hudY - 2, 110, 22);
            graphics.drawCenteredString(font, "\u00a7c\u00a7l\u2718 \u00a7r\u00a77No Skylands nearby", centerX, hudY + 4, 0xFFFFFF);
            return;
        }

        // Calculate direction and distance
        double playerX = mc.player.getX();
        double playerZ = mc.player.getZ();
        double dx = targetX - playerX;
        double dz = targetZ - playerZ;
        int distance = (int) Math.sqrt(dx * dx + dz * dz);

        // Angle from player to target (in degrees, 0=south, clockwise)
        double targetAngle = Math.toDegrees(Math.atan2(-dx, dz));
        // Player yaw (0=south, clockwise)
        double playerYaw = mc.player.getYRot() % 360;
        // Relative angle for the arrow
        float relativeAngle = (float) (targetAngle - playerYaw);

        // Cardinal direction
        String cardinal = getCardinal(targetAngle);

        // Distance color: green <500, yellow 500-2000, red >2000
        String distColor;
        if (distance < 500) distColor = "\u00a7a";
        else if (distance < 2000) distColor = "\u00a7e";
        else distColor = "\u00a7c";

        // Draw HUD
        int hudWidth = 130;
        int hudHeight = 34;
        drawHudBackground(graphics, centerX - hudWidth / 2, hudY - 2, hudWidth, hudHeight);

        // Title
        graphics.drawCenteredString(font, "\u00a7d\u00a7l\u2726 \u00a7r\u00a7fSkylands", centerX, hudY + 1, 0xFFFFFF);

        // Arrow — draw a rotated triangle
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(centerX - 40, hudY + 21, 0);
        pose.mulPose(Axis.ZP.rotationDegrees(relativeAngle));

        // Draw arrow as plain triangle (no formatting codes for clean centering)
        graphics.drawCenteredString(font, "\u25B2", 0, -4, 0xFFFFFF);

        pose.popPose();

        // Distance + direction text
        String distText = distColor + distance + "m \u00a77" + cardinal;
        graphics.drawCenteredString(font, distText, centerX + 10, hudY + 18, 0xFFFFFF);
    }

    private void drawHudBackground(GuiGraphics graphics, int x, int y, int width, int height) {
        // Semi-transparent dark background
        graphics.fill(RenderType.guiOverlay(), x, y, x + width, y + height, 0xAA000000);
        // Subtle border
        graphics.fill(RenderType.guiOverlay(), x, y, x + width, y + 1, 0x66D4A0FF);           // top (purple tint)
        graphics.fill(RenderType.guiOverlay(), x, y + height - 1, x + width, y + height, 0x66D4A0FF); // bottom
        graphics.fill(RenderType.guiOverlay(), x, y, x + 1, y + height, 0x44D4A0FF);           // left
        graphics.fill(RenderType.guiOverlay(), x + width - 1, y, x + width, y + height, 0x44D4A0FF); // right
    }

    private boolean isHoldingBiomeCompass(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return main.is(ChonkyCatsMod.BIOME_COMPASS) || off.is(ChonkyCatsMod.BIOME_COMPASS);
    }

    private String getCardinal(double angle) {
        // Normalize to 0-360
        double a = ((angle % 360) + 360) % 360;
        if (a >= 337.5 || a < 22.5) return "S";
        if (a < 67.5) return "SW";
        if (a < 112.5) return "W";
        if (a < 157.5) return "NW";
        if (a < 202.5) return "N";
        if (a < 247.5) return "NE";
        if (a < 292.5) return "E";
        return "SE";
    }
}

