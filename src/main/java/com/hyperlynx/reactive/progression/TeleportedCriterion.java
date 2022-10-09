package com.hyperlynx.reactive.progression;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class TeleportedCriterion extends SimpleCriterionTrigger<TeleportedCriterion.TeleportedTriggerInstance> {

    private final ResourceLocation TP_CRIT_RL = new ResourceLocation("reactive:teleport_criterion");

    class TeleportedTriggerInstance extends AbstractCriterionTriggerInstance {
        public TeleportedTriggerInstance(EntityPredicate.Composite pred) {
            super(TP_CRIT_RL, pred);
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
    protected @NotNull TeleportedTriggerInstance createInstance(JsonObject p_66248_, EntityPredicate.Composite pred, DeserializationContext p_66250_) {
        return new TeleportedTriggerInstance(pred);
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return TP_CRIT_RL;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player,
            TeleportedTriggerInstance::matches
        );
    }
}
