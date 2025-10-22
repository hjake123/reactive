package dev.hyperlynx.reactive.net.rxn;

import com.mojang.datafixers.util.Either;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.client.renderers.rxn.ReactionRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public record ReactionStatusMessage(Target target, List<ReactionStatusEntry> statuses){
    public ReactionStatusMessage(BlockPos pos, List<ReactionStatusEntry> statuses) {
        this(new Target(Either.left(pos)), statuses);
    }

    public ReactionStatusMessage(int entity_index, List<ReactionStatusEntry> statuses) {
        this(new Target(Either.right(entity_index)), statuses);
    }

    public void encoder(FriendlyByteBuf buffer) {
        buffer.writeBoolean(target.isBlockEntity());
        if(target.isBlockEntity()) {
            buffer.writeBlockPos(Objects.requireNonNull(target.blockEntityReactorPos()));
        } else {
            buffer.writeInt(Objects.requireNonNull(target.entityReactorIndex()));
        }
        buffer.writeCollection(statuses, (buf, status) -> {
            buf.writeUtf(status.reaction_alias());
            buf.writeEnum(status.status());
        });
    }

    public static ReactionStatusMessage decoder(FriendlyByteBuf buffer) {
        boolean target_is_block = buffer.readBoolean();
        Target target;
        if(target_is_block) {
            target = new Target(Either.left(buffer.readBlockPos()));
        } else {
            target = new Target(Either.right(buffer.readInt()));
        }
        var statuses = buffer.readCollection(ArrayList::new, (buf) -> {
            var alias = buf.readUtf();
            var status = buf.readEnum(Reaction.Status.class);
            return new ReactionStatusEntry(status, alias);
        });
        return new ReactionStatusMessage(target, statuses);
    }

    public void handler(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ReactionRenderers.handleReactionStatusMessage(this);
            context.get().setPacketHandled(true);
        });
    }

    public static class Target {
        private final Either<BlockPos, Integer> target;

        public Target(Either<BlockPos, Integer> target) {
            this.target = target;
        }

        public boolean isBlockEntity() {
            return target.left().isPresent();
        }

        public @Nullable BlockPos blockEntityReactorPos() {
            return target.left().orElse(null);
        }

        public @Nullable Integer entityReactorIndex() {
            return target.right().orElse(null);
        }

        public @Nullable Reactor getReactor(Level level) {
            if(target.left().isPresent()){
                BlockEntity be = level.getBlockEntity(target.left().get());
                if(!(be instanceof Reactor reactor)){
                    ReactiveMod.LOGGER.error("Sent reaction status to invalid reactor block entity. Ignoring.");
                    return null;
                }
                return reactor;
            }else if(target.right().isPresent()){
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
