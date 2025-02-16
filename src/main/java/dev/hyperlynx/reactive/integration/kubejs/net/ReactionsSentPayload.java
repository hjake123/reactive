package dev.hyperlynx.reactive.integration.kubejs.net;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ReactionsSentPayload implements CustomPacketPayload {
    private static final int VERSION = 1;

    public static final Type<ReactionsSentPayload> TYPE = new Type<>(ReactiveMod.location("kubejs_reactions_done"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReactionsSentPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ReactionsSentPayload::version,
            ReactionsSentPayload::verify
    );

    public int version(){
        return VERSION;
    }

    public ReactionsSentPayload(){}
    public static ReactionsSentPayload verify(int version){
        if(version != VERSION){
            throw new RuntimeException("Incompatible Reactive versions detected on the client and server!");
        }
        return new ReactionsSentPayload();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
