package dev.hyperlynx.reactive.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.registration.ReactiveCriterionTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

// Represents an advancement criterion that checks if a particular reaction is run.
public class ReactionTrigger extends SimpleCriterionTrigger<ReactionTrigger.ReactionTriggerInstance> {
    public ReactionTrigger(){
    }

    public static void triggerForNearbyPlayers(ServerLevel l, String alias, BlockPos center, int range){
        List<Player> nearby_players = l.getEntitiesOfClass(Player.class, AABB.ofSize(Vec3.atCenterOf(center), range, range, range));
        for(Player p : nearby_players) {
            ReactiveCriterionTriggers.REACTION.get().trigger((ServerPlayer) p, alias);
        }
    }

    public static void triggerPerfectForNearbyPlayers(ServerLevel l, String alias, BlockPos center, int range){
        List<Player> nearby_players = l.getEntitiesOfClass(Player.class, AABB.ofSize(Vec3.atCenterOf(center), range, range, range));
        for(Player p : nearby_players) {
            ReactiveCriterionTriggers.PERFECT_REACTION.get().trigger((ServerPlayer) p, alias);
        }
    }

    @Override
    public Codec<ReactionTriggerInstance> codec() {
        return ReactionTriggerInstance.CODEC;
    }

    public Criterion<ReactionTriggerInstance> instance(String reaction_alias) {
        return this.createCriterion(new ReactionTriggerInstance(reaction_alias));
    }

    public record ReactionTriggerInstance(String reaction_alias) implements SimpleInstance {
        public static final Codec<ReactionTriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.STRING.fieldOf("reaction_alias").forGetter(ReactionTriggerInstance::reaction_alias)
        ).apply(instance, ReactionTriggerInstance::new));


        public boolean matches(String running_reaction) {
            return running_reaction.equals(reaction_alias);
        }

        @Override
        public void validate(CriterionValidator validator) {
            SimpleInstance.super.validate(validator);
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }
    }

    public void trigger(ServerPlayer player, String running_reaction) {
        this.trigger(player,
                (instance) -> instance.matches(running_reaction)
        );
    }
}
