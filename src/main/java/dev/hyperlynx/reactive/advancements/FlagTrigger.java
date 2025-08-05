package dev.hyperlynx.reactive.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

// Represents an advancement criterion that always occurs when triggered.
public class FlagTrigger extends SimpleCriterionTrigger<FlagTrigger.FlagTriggerInstance> {
    public static void triggerForNearbyPlayers(ServerLevel l, FlagTrigger crit, BlockPos center, int range){
        List<Player> nearby_players = l.getEntitiesOfClass(Player.class, AABB.ofSize(Vec3.atCenterOf(center), range, range, range));
        for(Player p : nearby_players) {
            crit.trigger((ServerPlayer) p);
        }
    }

    @Override
    public Codec<FlagTriggerInstance> codec() {
        return FlagTriggerInstance.CODEC;
    }

    public static class FlagTriggerInstance implements SimpleInstance {
        public static final Codec<FlagTriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(FlagTriggerInstance::player)
        ).apply(instance, FlagTriggerInstance::new));

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public FlagTriggerInstance(Optional<ContextAwarePredicate> ignored){

        }

        @SuppressWarnings("SameReturnValue")
        public boolean matches() {
            return true;
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

    public void trigger(ServerPlayer player) {
        this.trigger(player,
            FlagTriggerInstance::matches
        );
    }
}
