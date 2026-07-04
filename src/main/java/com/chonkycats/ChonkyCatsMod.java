package com.chonkycats;

import com.chonkycats.entity.ChonkyCatEntity;
import com.chonkycats.item.BiomeCompassItem;
import com.chonkycats.item.ChonkyCatArmorItem;
import com.chonkycats.item.ChonkyWandItem;
import com.chonkycats.network.ModNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChonkyCatsMod implements ModInitializer {
    public static final String MOD_ID = "chonkycats";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Entity Type
    public static final EntityType<ChonkyCatEntity> CHONKY_CAT = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "chonky_cat"),
            EntityType.Builder.of(ChonkyCatEntity::new, MobCategory.CREATURE)
                    .sized(0.9f, 0.7f)
                    .clientTrackingRange(10)
                    .build("chonky_cat")
    );

    // Armor items
    public static final Item LEATHER_CAT_ARMOR = registerItem("chonky_cat_leather_armor",
            new ChonkyCatArmorItem(3, 1, new Item.Properties().stacksTo(1)));
    public static final Item IRON_CAT_ARMOR = registerItem("chonky_cat_iron_armor",
            new ChonkyCatArmorItem(6, 2, new Item.Properties().stacksTo(1)));
    public static final Item GOLD_CAT_ARMOR = registerItem("chonky_cat_gold_armor",
            new ChonkyCatArmorItem(5, 3, new Item.Properties().stacksTo(1)));
    public static final Item DIAMOND_CAT_ARMOR = registerItem("chonky_cat_diamond_armor",
            new ChonkyCatArmorItem(8, 4, new Item.Properties().stacksTo(1)));
    public static final Item NETHERITE_CAT_ARMOR = registerItem("chonky_cat_netherite_armor",
            new ChonkyCatArmorItem(10, 5, new Item.Properties().stacksTo(1)));

    // Spawn egg
    public static final Item CHONKY_CAT_SPAWN_EGG = registerItem("chonky_cat_spawn_egg",
            new SpawnEggItem(CHONKY_CAT, 0xE88A36, 0x3D2B1F, new Item.Properties()));

    // Chonky Wand — summons a tamed cat, enchant glint, epic rarity
    public static final Item CHONKY_WAND = registerItem("chonky_wand",
            new ChonkyWandItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    // Biome Compass — locates Chonky Cat Skylands biome
    public static final Item BIOME_COMPASS = registerItem("biome_compass",
            new BiomeCompassItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, name), item);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Chonky Cats mod loading! Prepare for thicc protectors!");

        // Register network payloads (must be before client init)
        ModNetworking.registerPayloads();

        // Register entity attributes
        FabricDefaultAttributeRegistry.register(CHONKY_CAT, ChonkyCatEntity.createAttributes());

        // Add items to creative tabs
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.accept(LEATHER_CAT_ARMOR);
            entries.accept(IRON_CAT_ARMOR);
            entries.accept(GOLD_CAT_ARMOR);
            entries.accept(DIAMOND_CAT_ARMOR);
            entries.accept(NETHERITE_CAT_ARMOR);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            entries.accept(CHONKY_CAT_SPAWN_EGG);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(BIOME_COMPASS);
        });

        // Register commands
        ModCommands.register();

        LOGGER.info("Chonky Cats mod loaded!");
    }
}
