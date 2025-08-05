package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record HoverQuiltHeightPayload(int id, double height) implements CustomPacketPayload {
    public static final Type<HoverQuiltHeightPayload> TYPE = new Type<>(ReactiveMod.location("hover_quilt_height_sync"));

    public static final StreamCodec<FriendlyByteBuf, HoverQuiltHeightPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, HoverQuiltHeightPayload::id,
            ByteBufCodecs.DOUBLE, HoverQuiltHeightPayload::height,
            HoverQuiltHeightPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
