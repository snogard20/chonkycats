package com.chonkycats.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BiomeLocationPayload(int x, int z, boolean found) implements CustomPacketPayload {

    public static final Type<BiomeLocationPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("chonkycats", "biome_location"));

    public static final StreamCodec<FriendlyByteBuf, BiomeLocationPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeInt(payload.x);
                        buf.writeInt(payload.z);
                        buf.writeBoolean(payload.found);
                    },
                    buf -> new BiomeLocationPayload(buf.readInt(), buf.readInt(), buf.readBoolean())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
