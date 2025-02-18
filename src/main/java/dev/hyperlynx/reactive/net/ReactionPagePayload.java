package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ReactionPagePayload(String alias, String contents) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ReactionPagePayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("reaction_page_payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReactionPagePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ReactionPagePayload::alias,
            ByteBufCodecs.STRING_UTF8, ReactionPagePayload::contents,
            ReactionPagePayload::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
