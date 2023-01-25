package com.hyperlynx.reactive.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class StagedFlagCriterion extends FlagCriterion{
    ResourceLocation prereq;

    public StagedFlagCriterion(ResourceLocation crit_rl, ResourceLocation prerequisite_rl) {
        super(crit_rl);
        prereq = prerequisite_rl;
    }

    @Override
    public void trigger(ServerPlayer player) {
        if(player.getAdvancements().getOrStartProgress(Advancement.Builder.advancement().build(prereq)).isDone())
            super.trigger(player);
    }
}
