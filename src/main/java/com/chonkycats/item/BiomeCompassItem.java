package com.chonkycats.item;

import com.chonkycats.network.BiomeLocationPayload;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public class BiomeCompassItem extends Item {

    private static final ResourceKey<Biome> SKYLANDS_KEY = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath("chonkycats", "chonky_cat_skylands"));

    public BiomeCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            player.sendSystemMessage(Component.literal("\u00a7e\u00a7lScanning for Chonky Cat Skylands..."));

            Pair<BlockPos, Holder<Biome>> result = serverLevel.findClosestBiome3d(
                    biome -> biome.is(SKYLANDS_KEY),
                    player.blockPosition(),
                    6400, 32, 64
            );

            if (result != null) {
                BlockPos pos = result.getFirst();
                int dist = (int) Math.sqrt(player.blockPosition().distSqr(pos));
                ServerPlayNetworking.send(serverPlayer, new BiomeLocationPayload(pos.getX(), pos.getZ(), true));
                player.sendSystemMessage(Component.literal(
                        "\u00a7a\u00a7l\u2714 Skylands detected! \u00a7r\u00a77" + dist + " blocks away"));
            } else {
                ServerPlayNetworking.send(serverPlayer, new BiomeLocationPayload(0, 0, false));
                player.sendSystemMessage(Component.literal(
                        "\u00a7c\u00a7l\u2718 No Skylands found nearby. \u00a7r\u00a77Try exploring new territory!"));
            }
        }

        player.playSound(SoundEvents.SPYGLASS_USE, 1.0f, 1.5f);
        player.getCooldowns().addCooldown(this, 60); // 3 second cooldown

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("\u00a77Right-click to locate the nearest"));
        tooltip.add(Component.literal("\u00a7dChonky Cat Skylands \u00a77biome"));
        tooltip.add(Component.literal("\u00a78Hold to see compass HUD"));
    }
}
