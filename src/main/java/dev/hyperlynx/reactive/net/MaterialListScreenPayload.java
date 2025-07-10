package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record MaterialListScreenPayload() implements CustomPacketPayload {
    public static final Type<MaterialListScreenPayload> TYPE = new Type<>(ReactiveMod.location("material_list_screen_payload"));
    public static final StreamCodec<? super FriendlyByteBuf, MaterialListScreenPayload> STREAM_CODEC = StreamCodec.unit(new MaterialListScreenPayload());


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public @NotNull ClientboundCustomPayloadPacket toVanillaClientbound() {
        return CustomPacketPayload.super.toVanillaClientbound();
    }
}
