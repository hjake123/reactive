package com.hyperlynx.reactive.integration.kubejs;

import com.hyperlynx.reactive.advancements.FlagTrigger;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;

public class FlagTriggerBuilder extends BuilderBase<FlagTrigger> {
    public FlagTriggerBuilder(ResourceLocation id) {
        super(id);
    }

    @Override
    public FlagTrigger createObject() {
        return new FlagTrigger(id);
    }
}
