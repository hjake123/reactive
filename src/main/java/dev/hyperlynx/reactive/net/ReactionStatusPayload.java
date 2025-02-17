package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ReactionStatusPayload(List<ReactionStatusEntry> statuses, BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ReactionStatusPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("reaction_sync_payload"));

    public static final StreamCodec<FriendlyByteBuf, ReactionStatusPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ReactionStatusEntry.STREAM_CODEC), ReactionStatusPayload::statuses,
            BlockPos.STREAM_CODEC, ReactionStatusPayload::pos,
            ReactionStatusPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
