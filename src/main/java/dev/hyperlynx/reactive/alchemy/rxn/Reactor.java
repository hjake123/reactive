package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.PowerBearer;
import dev.hyperlynx.reactive.util.AreaMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface Reactor extends PowerBearer {
    List<ReactionStatusEntry> getReactionStatus();

    void resetReactionStatus();

    // The method that performs reactions.
    default void react(Level level) {
        this.setUsedCrystalThisCycle(false);
        this.resetReactionStatus();
        for (Reaction r : ReactiveMod.REACTION_MAN.getReactions()) {
            Reaction.Status reaction_status = r.conditionsMet(this);
            // If the reaction should occur, conditionsMet will return REACTING.
            if (reaction_status == Reaction.Status.REACTING) {
                r.run(this);
                this.setDirty();
            }
            if (!(reaction_status == Reaction.Status.STABLE))
                this.getReactionStatus().add(new ReactionStatusEntry(reaction_status, r.getAlias()));
        }
        if (this.getReactionStatus().isEmpty()) {
            this.getReactionStatus().add(ReactionStatusEntry.stable());
        }

        if (!this.hasUsedCrystalThisCycle() && this.getLinkedCrystal() != null)
            this.unlinkCrystal(level, this.getBlockPos(), this.getBlockState());
    }

    boolean hasUsedCrystalThisCycle();

    void setUsedCrystalThisCycle(boolean used);

    BlockState getBlockState();

    BlockPos getBlockPos();

    // Only call this method when linked_crystal isn't null please and thank you.
    void unlinkCrystal(Level level, BlockPos pos, BlockState state);

    void setDirty();

    int getPowerLevel(Power p);

    AreaMemory getAreaMemory();

    Level getLevel();

    int getElectricCharge();

    int getSacrificeCount();

    EndCrystal getLinkedCrystal();

    void setLinkedCrystal(EndCrystal end_crystal);

    void resetRenderReactions();

    void addRenderReaction(Reaction r);

    Iterable<Reaction> getRenderReactions();

    void setElectricCharge(int i);

    default void incrementElectricCharge(int i){
        setElectricCharge(getElectricCharge() + i);
    }
}
