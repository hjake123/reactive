package dev.hyperlynx.reactive.advancements;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// Represents an advancement criterion that checks if a particular reaction is run.
public class ReactionCriterion extends SimpleCriterionTrigger<ReactionCriterion.ReactionTriggerInstance> {
    private static final String REACTION_ALIAS = "reaction_alias";
    ResourceLocation crit_rl;

    public ReactionCriterion(ResourceLocation rl){
        crit_rl = rl;
    }

    @Override
    protected ReactionTriggerInstance createInstance(JsonObject json, ContextAwarePredicate pred, DeserializationContext context) {
        String alias = json.get(REACTION_ALIAS).getAsString();
        return new ReactionTriggerInstance(pred, alias);
    }

    public ReactionTriggerInstance createInstance(String alias) {
        return new ReactionTriggerInstance(ContextAwarePredicate.ANY, alias);
    }

    public static void triggerForNearbyPlayers(ServerLevel l, String alias, BlockPos center, int range){
        List<Player> nearby_players = l.getEntitiesOfClass(Player.class, AABB.ofSize(Vec3.atCenterOf(center), range, range, range));
        for(Player p : nearby_players) {
            CriteriaTriggers.REACTION_TRIGGER.trigger((ServerPlayer) p, alias);
        }
    }

    public static void triggerPerfectForNearbyPlayers(ServerLevel l, String alias, BlockPos center, int range){
        List<Player> nearby_players = l.getEntitiesOfClass(Player.class, AABB.ofSize(Vec3.atCenterOf(center), range, range, range));
        for(Player p : nearby_players) {
            CriteriaTriggers.PERFECT_REACTION_TRIGGER.trigger((ServerPlayer) p, alias);
        }
    }

    @Override
    public ResourceLocation getId() {
        return crit_rl;
    }

    public class ReactionTriggerInstance extends AbstractCriterionTriggerInstance {
        String reaction_alias;

        public ReactionTriggerInstance(ContextAwarePredicate pred, String reaction_alias) {
            super(crit_rl, pred);
            this.reaction_alias = reaction_alias;
        }

        @Override
        public @NotNull JsonObject serializeToJson(SerializationContext context) {
            Gson gson = new Gson();
            JsonObject object = super.serializeToJson(context);
            object.add(REACTION_ALIAS, gson.toJsonTree(reaction_alias, String.class));
            return object;
        }

        public boolean matches(String running_reaction) {
            return running_reaction.equals(reaction_alias);
        }
    }

    public void trigger(ServerPlayer player, String running_reaction) {
        this.trigger(player,
                (instance) -> instance.matches(running_reaction)
        );
    }
}
