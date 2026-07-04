package com.chonkycats.client;

import com.chonkycats.ChonkyCatsMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ChonkyPawScreen extends Screen {
    private static final int PANEL_WIDTH = 180;
    private static final int PANEL_HEIGHT = 186;

    public ChonkyPawScreen() {
        super(Component.literal("Chonky Cats"));
    }

    @Override
    protected void init() {
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;
        int btnWidth = PANEL_WIDTH - 20;
        int btnY = top + 30;

        // Debug Stick button
        this.addRenderableWidget(Button.builder(
                Component.literal("\u2B50 Debug Stick"), btn -> {
                    giveViaCommand("debug_stick");
                }).bounds(left + 10, btnY, btnWidth, 20).build());

        // Chonky Cat Egg button
        this.addRenderableWidget(Button.builder(
                Component.literal("\uD83D\uDC31 Chonky Cat Egg"), btn -> {
                    giveViaCommand("spawn_egg");
                }).bounds(left + 10, btnY + 26, btnWidth, 20).build());

        // Chonky Wand button
        this.addRenderableWidget(Button.builder(
                Component.literal("\u2728 Chonky Wand"), btn -> {
                    giveViaCommand("chonky_wand");
                }).bounds(left + 10, btnY + 52, btnWidth, 20).build());

        // All armor buttons
        this.addRenderableWidget(Button.builder(
                Component.literal("\uD83D\uDEE1 Cat Armor (All)"), btn -> {
                    giveViaCommand("all_armor");
                }).bounds(left + 10, btnY + 78, btnWidth, 20).build());

        // Skylands Compass button
        this.addRenderableWidget(Button.builder(
                Component.literal("\uD83E\uDDED Skylands Compass"), btn -> {
                    giveViaCommand("biome_compass");
                }).bounds(left + 10, btnY + 104, btnWidth, 20).build());

        // Close button
        this.addRenderableWidget(Button.builder(
                Component.literal("\u2716 Close"), btn -> {
                    this.onClose();
                }).bounds(left + 10, btnY + 130, btnWidth, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;

        // Dark panel background
        graphics.fill(left - 2, top - 2, left + PANEL_WIDTH + 2, top + PANEL_HEIGHT + 2, 0xFF333333);
        graphics.fill(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xCC000000);

        // Title
        graphics.drawCenteredString(this.font,
                Component.literal("\u00a76\u00a7lChonky Cats \uD83D\uDC3E"),
                this.width / 2, top + 10, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void giveViaCommand(String item) {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.connection.sendCommand("chonky_give " + item);
        }
        this.onClose();
    }
}
