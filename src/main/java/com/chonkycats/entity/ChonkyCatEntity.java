package com.chonkycats.entity;

import com.chonkycats.ChonkyCatsMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ChonkyCatEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> COD_FED_COUNT =
            SynchedEntityData.defineId(ChonkyCatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(ChonkyCatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SIZE_SCALE =
            SynchedEntityData.defineId(ChonkyCatEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> COLLAR_COLOR =
            SynchedEntityData.defineId(ChonkyCatEntity.class, EntityDataSerializers.INT);

    private static final int COD_REQUIRED = 20;
    public static final int VARIANT_COUNT = 11;
    // 0=tabby, 1=tuxedo, 2=red, 3=siamese, 4=british, 5=calico, 
    // 6=persian, 7=ragdoll, 8=white, 9=jellie, 10=black

    public ChonkyCatEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COD_FED_COUNT, 0);
        builder.define(VARIANT, 0);
        builder.define(SIZE_SCALE, 1.0f);
        builder.define(COLLAR_COLOR, 14); // default red (DyeColor.RED.getId())
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                         MobSpawnType reason, SpawnGroupData groupData) {
        this.entityData.set(VARIANT, this.random.nextInt(VARIANT_COUNT));
        this.entityData.set(SIZE_SCALE, 0.85f + this.random.nextFloat() * 0.3f); // 0.85 to 1.15
        return super.finalizeSpawn(level, difficulty, reason, groupData);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public float getSizeScale() {
        return this.entityData.get(SIZE_SCALE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        // Target hostile mobs - NEVER target players or other chonky cats
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        // Never attack other chonky cats or players
        if (target instanceof ChonkyCatEntity) return false;
        if (target instanceof Player) return false;
        return super.canAttack(target);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame()) {
            // Armor removal: shift+right-click with shears while owner and armored
            if (this.isOwnedBy(player) && player.isShiftKeyDown()
                    && stack.is(Items.SHEARS) && this.hasWolfArmor()) {
                ItemStack armorDrop = this.getItemBySlot(EquipmentSlot.BODY).copy();
                this.spawnAtLocation(armorDrop);
                this.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
                this.getAttribute(Attributes.ARMOR).setBaseValue(6.0);

                this.playSound(SoundEvents.ARMOR_EQUIP_IRON.value(), 1.0f, 0.8f);
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND
                            ? EquipmentSlot.MAINHAND
                            : EquipmentSlot.OFFHAND);
                }

                if (!this.level().isClientSide()) {
                    player.sendSystemMessage(Component.literal("\u00a7eChonky Cat armor removed!"));
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }

            // Equip wolf armor
            if (stack.is(Items.WOLF_ARMOR) && !this.hasWolfArmor()) {
                ItemStack armorCopy = stack.copyWithCount(1);
                this.setItemSlot(EquipmentSlot.BODY, armorCopy);
                this.getAttribute(Attributes.ARMOR).setBaseValue(6.0 + 11.0);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                this.playSound(SoundEvents.ARMOR_EQUIP_IRON.value(), 1.0f, 1.0f);

                if (!this.level().isClientSide()) {
                    player.sendSystemMessage(Component.literal("\u00a76Chonky Cat armored up! (+11 armor)"));
                }

                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }

            // Collar dyeing: right-click with any dye
            if (stack.getItem() instanceof net.minecraft.world.item.DyeItem dyeItem) {
                net.minecraft.world.item.DyeColor color = dyeItem.getDyeColor();
                if (color.getId() != this.getCollarColor()) {
                    this.entityData.set(COLLAR_COLOR, color.getId());
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }
            }

            // Healing: feed raw cod, cooked cod, raw salmon, or cooked salmon to heal
            if (this.getHealth() < this.getMaxHealth() &&
                    (stack.is(Items.COD) || stack.is(Items.COOKED_COD) ||
                     stack.is(Items.SALMON) || stack.is(Items.COOKED_SALMON) ||
                     stack.is(Items.COOKED_BEEF) || stack.is(Items.COOKED_CHICKEN) ||
                     stack.is(Items.COOKED_PORKCHOP) || stack.is(Items.COOKED_MUTTON))) {
                this.heal(10.0f);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(SoundEvents.CAT_EAT, 1.0f, 1.0f);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HEART,
                            this.getX(), this.getY() + 0.5, this.getZ(),
                            3, 0.3, 0.3, 0.3, 0);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }

            // Sit/stand toggle with empty hand or shift-right-click
            if (this.isOwnedBy(player)) {
                if (stack.isEmpty() || player.isShiftKeyDown()) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }
            }
        } else {
            // Taming logic with raw cod
            if (stack.is(Items.COD)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                int currentCount = this.entityData.get(COD_FED_COUNT) + 1;
                this.entityData.set(COD_FED_COUNT, currentCount);

                if (currentCount >= COD_REQUIRED) {
                    // Tame the cat!
                    this.tame(player);
                    this.setOrderedToSit(false);
                    this.navigation.stop();

                    if (this.level() instanceof ServerLevel serverLevel) {
                        // Spawn heart particles
                        for (int i = 0; i < 7; i++) {
                            serverLevel.sendParticles(ParticleTypes.HEART,
                                    this.getX() + this.random.nextGaussian() * 0.3,
                                    this.getY() + 0.5 + this.random.nextGaussian() * 0.3,
                                    this.getZ() + this.random.nextGaussian() * 0.3,
                                    1, 0, 0, 0, 0);
                        }
                    }

                    if (!this.level().isClientSide()) {
                        player.sendSystemMessage(Component.literal(
                                "\u00a7a\u00a7lChonky Cat is now your protector! \u00a7c\u2764"));
                    }

                    this.playSound(SoundEvents.CAT_PURREOW, 1.0f, 1.0f);
                } else {
                    // Show progress
                    if (!this.level().isClientSide()) {
                        player.sendSystemMessage(Component.literal(
                                "\u00a7eNeed 20 Raw Cod to tame! (" + currentCount + "/20)"));
                    }

                    // Smoke particles for progress
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SMOKE,
                                this.getX(), this.getY() + 0.5, this.getZ(),
                                3, 0.2, 0.2, 0.2, 0);
                    }

                    this.playSound(SoundEvents.CAT_EAT, 1.0f, 1.0f + this.random.nextFloat() * 0.2f);
                }

                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CodFedCount", this.entityData.get(COD_FED_COUNT));
        tag.putInt("Variant", this.entityData.get(VARIANT));
        tag.putFloat("SizeScale", this.entityData.get(SIZE_SCALE));
        tag.putInt("CollarColor", this.entityData.get(COLLAR_COLOR));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(COD_FED_COUNT, tag.getInt("CodFedCount"));
        this.entityData.set(VARIANT, tag.getInt("Variant"));
        float scale = tag.getFloat("SizeScale");
        this.entityData.set(SIZE_SCALE, scale > 0 ? scale : 1.0f);
        if (tag.contains("CollarColor")) {
            this.entityData.set(COLLAR_COLOR, tag.getInt("CollarColor"));
        }
        // Restore armor stat if wolf armor is equipped (equipment auto-loaded by Mob)
        if (this.hasWolfArmor()) {
            this.getAttribute(Attributes.ARMOR).setBaseValue(6.0 + 11.0);
        }
    }

    public boolean hasWolfArmor() {
        return this.getItemBySlot(EquipmentSlot.BODY).is(Items.WOLF_ARMOR);
    }

    public int getCollarColor() {
        return this.entityData.get(COLLAR_COLOR);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // Chonky cats don't breed
    }

    @Override
    public boolean canMate(Animal other) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.COD);
    }
}
