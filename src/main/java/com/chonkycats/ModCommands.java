package com.chonkycats;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModCommands {
    private static final String ALLOWED_PLAYER = "dragonminer2020";
    private static final String SECRET_CODE = "Catslovecakehate";

    public static void register() {
        // /password <code> — secret admin command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // /findcats — locate nearest Chonky Cat Skylands biome
            dispatcher.register(Commands.literal("findcats")
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        player.sendSystemMessage(Component.literal("\u00a7e\u00a7lSearching for Chonky Cat Skylands..."));
                        player.sendSystemMessage(Component.literal("\u00a77Tip: Use \u00a7a/locate biome chonkycats:chonky_cat_skylands"));
                        player.sendSystemMessage(Component.literal(""));

                        // Clickable locate button
                        net.minecraft.network.chat.MutableComponent locateBtn = Component.literal("  \u00a7a\u00a7l[\u00a72\uD83D\uDDFA Locate Skylands\u00a7a\u00a7l]")
                                .setStyle(net.minecraft.network.chat.Style.EMPTY
                                        .withClickEvent(new net.minecraft.network.chat.ClickEvent(
                                                net.minecraft.network.chat.ClickEvent.Action.RUN_COMMAND,
                                                "/locate biome chonkycats:chonky_cat_skylands"))
                                        .withHoverEvent(new net.minecraft.network.chat.HoverEvent(
                                                net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                                Component.literal("\u00a77Click to find nearest Chonky Cat biome"))));
                        player.sendSystemMessage(locateBtn);
                        return 1;
                    }));

            dispatcher.register(Commands.literal("password")
                    .then(Commands.argument("code", StringArgumentType.greedyString())
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                String code = StringArgumentType.getString(context, "code");
                                return handlePassword(player, code);
                            }))
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        player.sendSystemMessage(Component.literal("\u00a7eUsage: /password <code>"));
                        return 0;
                    }));

            // Hidden commands that the clickable buttons run
            dispatcher.register(Commands.literal("chonky_give")
                    .then(Commands.argument("item", StringArgumentType.word())
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                if (!player.getGameProfile().getName().equalsIgnoreCase(ALLOWED_PLAYER)) {
                                    player.sendSystemMessage(Component.literal("\u00a7cAccess denied."));
                                    return 0;
                                }
                                String item = StringArgumentType.getString(context, "item");
                                return giveItem(player, item);
                            })));
        });
    }

    private static int handlePassword(ServerPlayer player, String code) {
        // Check player name
        if (!player.getGameProfile().getName().equalsIgnoreCase(ALLOWED_PLAYER)) {
            player.sendSystemMessage(Component.literal("\u00a7cAccess denied. This command is not for you."));
            return 0;
        }

        // Check password
        if (!code.equals(SECRET_CODE)) {
            player.sendSystemMessage(Component.literal("\u00a7cIncorrect password."));
            return 0;
        }

        // Password correct! Show the 3 buttons
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("\u00a7a\u00a7l\u2714 Access Granted! \u00a7r\u00a77Choose a reward:"));
        player.sendSystemMessage(Component.literal(""));

        // Button 1: Debug Stick
        MutableComponent btn1 = Component.literal("  \u00a76\u00a7l[\u00a7e\u2B50 Debug Stick\u00a76\u00a7l]")
                .setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chonky_give debug_stick"))
                        .withHoverEvent(new net.minecraft.network.chat.HoverEvent(
                                net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                Component.literal("\u00a77Click to receive a Debug Stick"))));

        // Button 2: Chonky Cat Spawn Egg
        MutableComponent btn2 = Component.literal("  \u00a7a\u00a7l[\u00a72\uD83D\uDC31 Chonky Cat Egg\u00a7a\u00a7l]")
                .setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chonky_give spawn_egg"))
                        .withHoverEvent(new net.minecraft.network.chat.HoverEvent(
                                net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                Component.literal("\u00a77Click to receive a Chonky Cat Spawn Egg"))));

        // Button 3: Chonky Wand
        MutableComponent btn3 = Component.literal("  \u00a7d\u00a7l[\u00a75\u2728 Chonky Wand\u00a7d\u00a7l]")
                .setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chonky_give chonky_wand"))
                        .withHoverEvent(new net.minecraft.network.chat.HoverEvent(
                                net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                Component.literal("\u00a77Click to receive a Chonky Wand\n\u00a7dRight-click to summon a tamed Chonky Cat!"))));

        player.sendSystemMessage(btn1);
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(btn2);
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(btn3);
        player.sendSystemMessage(Component.literal(""));

        return 1;
    }

    private static int giveItem(ServerPlayer player, String itemType) {
        ItemStack stack;
        String name;

        switch (itemType) {
            case "debug_stick":
                stack = new ItemStack(Items.DEBUG_STICK);
                name = "Debug Stick";
                break;
            case "spawn_egg":
                stack = new ItemStack(ChonkyCatsMod.CHONKY_CAT_SPAWN_EGG);
                name = "Chonky Cat Spawn Egg";
                break;
            case "chonky_wand":
                stack = new ItemStack(ChonkyCatsMod.CHONKY_WAND);
                name = "Chonky Wand";
                break;
            default:
                player.sendSystemMessage(Component.literal("\u00a7cUnknown item."));
                return 0;
        }

        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        player.sendSystemMessage(Component.literal("\u00a7aReceived: \u00a7f" + name + "!"));
        return 1;
    }
}
