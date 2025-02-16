package dev.hyperlynx.reactive.integration.kubejs.net;

import dev.hyperlynx.reactive.ReactiveMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ReactionRequestPayload implements CustomPacketPayload {
    private static final int VERSION = 1;

    public static final CustomPacketPayload.Type<ReactionRequestPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("request_kubejs_reactions"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReactionRequestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ReactionRequestPayload::version,
            ReactionRequestPayload::verify
    );

    public int version(){
        return VERSION;
    }

    public ReactionRequestPayload(){}
    public static ReactionRequestPayload verify(int version){
        if(version != VERSION){
            throw new RuntimeException("Incompatible Reactive versions detected on the client and server!");
        }
        return new ReactionRequestPayload();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
