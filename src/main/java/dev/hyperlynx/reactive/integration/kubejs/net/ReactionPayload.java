package dev.hyperlynx.reactive.integration.kubejs.net;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.integration.kubejs.CustomReaction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ReactionPayload(CustomReaction reaction) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ReactionPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("kubejs_reaction_add_payload"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ReactionPayload> STREAM_CODEC = StreamCodec.composite(
            CustomReaction.STREAM_CODEC, ReactionPayload::reaction,
            ReactionPayload::new
    );
}
