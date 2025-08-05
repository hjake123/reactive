package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.components.LitmusMeasurement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record LitmusScreenPayload(LitmusMeasurement measurement, List<Component> components) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LitmusScreenPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("litmus_screen_payload"));
    public static final StreamCodec<? super FriendlyByteBuf, LitmusScreenPayload> STREAM_CODEC = StreamCodec.composite(
            LitmusMeasurement.STREAM_CODEC, LitmusScreenPayload::measurement,
            ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.apply(ByteBufCodecs.list()), LitmusScreenPayload::components,
            LitmusScreenPayload::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
