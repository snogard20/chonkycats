package com.chonkycats.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * Injects the Chonky Cat Skylands biome into overworld generation.
 * Generates in warm, dry, high-elevation terrain with a narrow parameter window (rare).
 */
@Mixin(OverworldBiomeBuilder.class)
public class OverworldBiomeBuilderMixin {

    private static final ResourceKey<Biome> SKYLANDS = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath("chonkycats", "chonky_cat_skylands"));

    @Inject(method = "addBiomes", at = @At("RETURN"))
    private void chonkycats$injectSkylands(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer,
                                           CallbackInfo ci) {
        // Rare biome: warm temp, low humidity, inland peaks, narrow weirdness slice
        consumer.accept(Pair.of(
                Climate.parameters(
                        Climate.Parameter.span(0.2f, 0.55f),       // warm temperature
                        Climate.Parameter.span(-0.35f, -0.1f),     // dry humidity
                        Climate.Parameter.span(0.03f, 0.3f),       // moderate continentalness
                        Climate.Parameter.span(-1.0f, -0.78f),     // very low erosion (peaks)
                        Climate.Parameter.point(0.0f),              // surface depth
                        Climate.Parameter.span(-1.0f, -0.93f),     // narrow weirdness (rare)
                        0.0f                                        // offset
                ),
                SKYLANDS
        ));
    }
}
