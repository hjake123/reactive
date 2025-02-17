package dev.hyperlynx.reactive.integration.kubejs.net;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ReactionEffectResetPayload implements CustomPacketPayload {
    private static final int VERSION = 1;

    public static final Type<ReactionEffectResetPayload> TYPE = new Type<>(ReactiveMod.location("kubejs_reaction_effect_cache_reset"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReactionEffectResetPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ReactionEffectResetPayload::version,
            ReactionEffectResetPayload::verify
    );

    public int version(){
        return VERSION;
    }

    public ReactionEffectResetPayload(){}
    public static ReactionEffectResetPayload verify(int version){
        if(version != VERSION){
            throw new RuntimeException("Incompatible Reactive versions detected on the client and server!");
        }
        return new ReactionEffectResetPayload();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
