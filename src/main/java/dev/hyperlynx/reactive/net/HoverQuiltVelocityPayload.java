package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record HoverQuiltVelocityPayload(double velocity) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<HoverQuiltVelocityPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("hover_quilt_input"));

    public static final StreamCodec<FriendlyByteBuf, HoverQuiltVelocityPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, HoverQuiltVelocityPayload::velocity,
            HoverQuiltVelocityPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
