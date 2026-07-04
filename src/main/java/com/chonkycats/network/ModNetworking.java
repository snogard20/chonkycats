package com.chonkycats.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModNetworking {

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(BiomeLocationPayload.TYPE, BiomeLocationPayload.STREAM_CODEC);
    }
}
