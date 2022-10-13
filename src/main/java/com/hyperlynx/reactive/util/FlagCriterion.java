package com.hyperlynx.reactive.util;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

// Represents an advancement criterion that always occurs when triggered.
public class FlagCriterion extends SimpleCriterionTrigger<FlagCriterion.FlagTriggerInstance> {

    private final ResourceLocation crit_rl;

    public FlagCriterion(ResourceLocation crit_rl){
        this.crit_rl = crit_rl;
    }

    class FlagTriggerInstance extends AbstractCriterionTriggerInstance {
        public FlagTriggerInstance(EntityPredicate.Composite pred) {
            super(crit_rl, pred);
        }

        @Override
        public @NotNull JsonObject serializeToJson(SerializationContext context) {
            return super.serializeToJson(context);
        }

        public boolean matches() {
            return true;
        }
    }

    @Override
    protected @NotNull FlagCriterion.FlagTriggerInstance createInstance(JsonObject p_66248_, EntityPredicate.Composite pred, DeserializationContext p_66250_) {
        return new FlagTriggerInstance(pred);
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return crit_rl;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player,
            FlagTriggerInstance::matches
        );
    }
}
