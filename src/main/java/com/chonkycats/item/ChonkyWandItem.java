package com.chonkycats.item;

import com.chonkycats.ChonkyCatsMod;
import com.chonkycats.entity.ChonkyCatEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ChonkyWandItem extends Item {
    public ChonkyWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level instanceof ServerLevel serverLevel) {
            // Spawn a pre-tamed chonky cat at the player's look position
            ChonkyCatEntity cat = new ChonkyCatEntity(ChonkyCatsMod.CHONKY_CAT, level);
            double spawnX = player.getX() + player.getLookAngle().x * 3;
            double spawnY = player.getY();
            double spawnZ = player.getZ() + player.getLookAngle().z * 3;
            cat.setPos(spawnX, spawnY, spawnZ);
            cat.tame(player);
            cat.setOrderedToSit(false);

            // Random variant
            cat.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(cat.blockPosition()),
                    net.minecraft.world.entity.MobSpawnType.MOB_SUMMONED, null);

            level.addFreshEntity(cat);

            // Effects
            for (int i = 0; i < 10; i++) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                        spawnX + level.random.nextGaussian() * 0.5,
                        spawnY + 0.5 + level.random.nextFloat(),
                        spawnZ + level.random.nextGaussian() * 0.5,
                        1, 0, 0.1, 0, 0.1);
            }

            player.sendSystemMessage(Component.literal(
                    "\u00a7d\u2728 A Chonky Cat appears from the magical mist!"));
        }

        player.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0f, 1.0f);
        player.getCooldowns().addCooldown(this, 60); // 3 second cooldown

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Enchantment glint
    }
}
