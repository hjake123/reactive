package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.PowerBearer;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.client.renderers.rxn.ReactionRenderer;
import dev.hyperlynx.reactive.net.ReactionStatusPayload;
import dev.hyperlynx.reactive.util.AreaMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public interface Reactor extends PowerBearer {
    List<ReactionStatusEntry> getReactionStatus();

    void resetReactionStatus();

    // The method that performs reactions.
    default void react(ServerLevel level) {
        this.setUsedCrystalThisCycle(false);
        this.resetReactionStatus();
        for (Reaction r : ReactiveMod.REACTION_MAN.getReactions(level)) {
            Reaction.Status reaction_status = r.conditionsMet(this);
            // If the reaction should occur, conditionsMet will return REACTING.
            if (reaction_status == Reaction.Status.REACTING) {
                r.run(this);
                this.setDirty();
            }
            if (!(reaction_status == Reaction.Status.STABLE))
                this.getReactionStatus().add(new ReactionStatusEntry(reaction_status, r.getAlias()));
        }

        // Update clients each reaction tick about what to display.
        BlockPos pos = this.getBlockPos();
        PacketDistributor.sendToPlayersNear(level, null, pos.getX(), pos.getY(), pos.getZ(), 32,
                ReactionStatusPayload.forReactor(getReactionStatus(), this));

        if (this.getReactionStatus().isEmpty()) {
            this.getReactionStatus().add(ReactionStatusEntry.stable());
        }

        if (!this.hasUsedCrystalThisCycle() && this.getLinkedCrystal() != null)
            this.unlinkCrystal(level, this.getBlockPos(), this.getBlockState());
    }

    default boolean checkGoldSymbol(){
        return this.getAreaMemory().exists(this.getLevel(), Registration.GOLD_SYMBOL.get());
    }

    boolean hasUsedCrystalThisCycle();

    void setUsedCrystalThisCycle(boolean used);

    BlockState getBlockState();

    BlockPos getBlockPos();

    Vec3 getPos();

    // Only call this method when linked_crystal isn't null please and thank you.
    void unlinkCrystal(Level level, BlockPos pos, BlockState state);

    void setDirty();

    AreaMemory getAreaMemory();

    Level getLevel();

    int getElectricCharge();

    EndCrystal getLinkedCrystal();

    void setLinkedCrystal(EndCrystal end_crystal);

    Iterable<String> getRenderReactions();

    void setElectricCharge(int i);

    default void addElectricCharge(int i){
        setElectricCharge(getElectricCharge() + i);
    }

    void clearRenderReactions();

    void addRenderReaction(String s);
}
