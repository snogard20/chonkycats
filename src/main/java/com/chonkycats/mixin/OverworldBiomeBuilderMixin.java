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
        // Uncommon biome: warm temp, dry, elevated terrain — wider window for discoverability
        consumer.accept(Pair.of(
                Climate.parameters(
                        Climate.Parameter.span(0.15f, 0.55f),      // warm temperature (wider)
                        Climate.Parameter.span(-0.45f, 0.0f),      // dry to neutral humidity (wider)
                        Climate.Parameter.span(-0.11f, 0.55f),     // coastal to mid-inland (wider)
                        Climate.Parameter.span(-1.0f, -0.5f),      // low erosion — hills to peaks (wider)
                        Climate.Parameter.point(0.0f),              // surface depth
                        Climate.Parameter.span(-1.0f, -0.4f),      // broad weirdness slice (much wider)
                        0.375f                                      // slight offset penalty so vanilla biomes win ties
                ),
                SKYLANDS
        ));
    }
}
