package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
    // TODO
    public static CrucibleBlockEntity vortex(CrucibleBlockEntity c){
        return c;
    }

    public static CrucibleBlockEntity formation(CrucibleBlockEntity c){
        return c;
    }

    public static CrucibleBlockEntity luminescence(CrucibleBlockEntity c){
        if(c.getLevel() == null) return c;
        AABB aoe = new AABB(c.getBlockPos());
        aoe.inflate(3); // Inflate the AOE to be 5x the size of the crucible.
        if(!c.getLevel().isClientSide()){
            List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for(LivingEntity e : nearby_ents){
                e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 500, 1));
                e.hurt(DamageSource.MAGIC, 1);
            }
        }
        Helper.drawParticleLine(c.getLevel(), ParticleTypes.END_ROD,
                c.getBlockPos().getX() + 0.5, c.getBlockPos().getY() + 0.5, c.getBlockPos().getZ() + 0.5,
                c.getBlockPos().getX() + 0.5, c.getBlockPos().getY() + 30.5, c.getBlockPos().getZ() + 0.5,
                2);
        return c;
    }

    public static CrucibleBlockEntity sicklySmoke(CrucibleBlockEntity c){
        return c;
    }

    public static CrucibleBlockEntity discharge(CrucibleBlockEntity c){
        return c;
    }

}
