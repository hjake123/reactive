package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.entites.ReactorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.handling.IPayloadContext;
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

    public static void handle(ReactionStatusPayload payload, IPayloadContext context) {
        Level level = context.player().level();
        BlockEntity be = level.getBlockEntity(payload.pos());
        Reactor reactor;

        if(be instanceof Reactor){
            reactor = (Reactor) be;
        } else {
            var reactor_entities = level.getEntitiesOfClass(ReactorEntity.class, AABB.ofSize(payload.pos().getCenter(), 1, 1, 1));
            if(reactor_entities.isEmpty()){
                ReactiveMod.LOGGER.error("Reaction status packet had an invalid destination. Ignoring.");
                return;
            }
            reactor = reactor_entities.getFirst();
        }

        reactor.clearRenderReactions();
        for(ReactionStatusEntry entry : payload.statuses()){
            if(entry.status() == Reaction.Status.REACTING){
                reactor.addRenderReaction(entry.reaction_alias());
            }
        }
    }
}
