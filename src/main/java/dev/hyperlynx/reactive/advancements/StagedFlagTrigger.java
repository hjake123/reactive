package dev.hyperlynx.reactive.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class StagedFlagTrigger extends FlagTrigger {
    ResourceLocation prereq;

    public StagedFlagTrigger(ResourceLocation crit_rl, ResourceLocation prerequisite_advancement_rl) {
        super(crit_rl);
        prereq = prerequisite_advancement_rl;
    }

    @Override
    public void trigger(ServerPlayer player) {
        if(player.getAdvancements().getOrStartProgress(Advancement.Builder.advancement().build(prereq)).isDone())
            super.trigger(player);
    }
}
