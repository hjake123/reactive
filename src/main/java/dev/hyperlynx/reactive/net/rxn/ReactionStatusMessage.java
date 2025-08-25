package dev.hyperlynx.reactive.net.rxn;

import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import dev.hyperlynx.reactive.client.renderers.CrucibleRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record ReactionStatusMessage(BlockPos pos, List<ReactionStatusEntry> statuses){
    public void encoder(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeCollection(statuses, (buf, status) -> {
            buf.writeUtf(status.reaction_alias());
            buf.writeEnum(status.status());
        });
    }

    public static ReactionStatusMessage decoder(FriendlyByteBuf buffer) {
        var pos = buffer.readBlockPos();
        var statuses = buffer.readCollection(ArrayList::new, (buf) -> {
            var alias = buf.readUtf();
            var status = buf.readEnum(Reaction.Status.class);
            return new ReactionStatusEntry(status, alias);
        });
        return new ReactionStatusMessage(pos, statuses);
    }

    public void handler(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            CrucibleRenderer.handleReactionStatusMessage(this);
            context.get().setPacketHandled(true);
        });
    }
}
