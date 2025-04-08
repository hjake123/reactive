package dev.hyperlynx.reactive.net;

import com.mojang.datafixers.util.Either;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ReactionStatusPayload(List<ReactionStatusEntry> statuses, Target target) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ReactionStatusPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("reaction_sync_payload"));

    public static final StreamCodec<FriendlyByteBuf, ReactionStatusPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ReactionStatusEntry.STREAM_CODEC), ReactionStatusPayload::statuses,
            Target.STREAM_CODEC, ReactionStatusPayload::target,
            ReactionStatusPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ReactionStatusPayload payload, IPayloadContext context) {
        Level level = context.player().level();
        Reactor reactor = payload.target().getReactor(level);
        if(reactor == null){
            return;
        }

        reactor.clearRenderReactions();
        for(ReactionStatusEntry entry : payload.statuses()){
            if(entry.status() == Reaction.Status.REACTING){
                reactor.addRenderReaction(entry.reaction_alias());
            }
        }
    }


    public record Target(Either<BlockPos, Integer> target) {
        public static final StreamCodec<FriendlyByteBuf, Target> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.either(BlockPos.STREAM_CODEC, ByteBufCodecs.INT), Target::target,
                Target::new
        );

        public @Nullable Reactor getReactor(Level level) {
            if(target().left().isPresent()){
                BlockEntity be = level.getBlockEntity(target.left().get());
                if(!(be instanceof Reactor reactor)){
                    ReactiveMod.LOGGER.error("Sent reaction status to invalid reactor block entity. Ignoring.");
                    return null;
                }
                return reactor;
            }else if(target().right().isPresent()){
                if(!(level.getEntity(target.right().get()) instanceof Reactor reactor)){
                    ReactiveMod.LOGGER.error("Sent reaction status to invalid entity. Ignoring.");
                    return null;
                }
                return reactor;
            }
            ReactiveMod.LOGGER.error("Sent malformed reaction status. Ignoring.");
            return null;
        }
    }
}
