package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

// Just a holder class for the various reaction effect methods.
public class ReactionEffects {
    public static CrucibleBlockEntity discharge(CrucibleBlockEntity c){
        if(c.getLevel() == null) return c;

        AABB aoe = new AABB(c.getBlockPos());
        aoe.inflate(5); // Inflate the AOE to be 5x the size of the crucible.

        if(c.getLevel().getBlockStates(aoe).anyMatch(blockState -> blockState.is(Blocks.LIGHTNING_ROD))){
            //TODO: Strike the lightning rod instead.
            return c;
        }

        if (!c.getLevel().isClientSide()) {
            List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            nearby_ents.get(0).hurt(DamageSource.MAGIC, 12);
        }else{
            //TODO: Draw strike particles.
        }
        return c;
    }
}
