package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.phys.AABB;

import java.util.List;

// Just a holder class for the various reaction effect methods.
public class ReactionEffects {

    // TODO: a lot of reaction effects
    public static CrucibleBlockEntity vortex(CrucibleBlockEntity c){
        if(!c.getLevel().isClientSide)
            System.out.println("*sounds of spiraling*");
        return c;
    }

    public static CrucibleBlockEntity formation(CrucibleBlockEntity c){
        if(!c.getLevel().isClientSide)
            System.out.println("Gogograhgrah!");
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
                2, 0);
        return c;
    }

    public static CrucibleBlockEntity sicklySmoke(CrucibleBlockEntity c){
        if(!c.getLevel().isClientSide) {
            if(c.getLevel().random.nextFloat() < 0.4) {
                AABB aoe = new AABB(c.getBlockPos());
                aoe.inflate(3); // Inflate the AOE to be 5x the size of the crucible.
                List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
                for(LivingEntity e : nearby_ents){
                    e.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
                }
            }
        }else {
            Helper.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.LARGE_SMOKE, c.getBlockPos(), 0.3F);
        }
        return c;
    }

    public static CrucibleBlockEntity discharge(CrucibleBlockEntity c){
        c.electricCharge += 5;
        if(c.electricCharge > 20){
            BlockPos potential_rod = c.areaMemory.fetch(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Blocks.LIGHTNING_ROD);
            if(potential_rod != null){
                if(!c.getLevel().isClientSide) {
                    ((LightningRodBlock) Blocks.LIGHTNING_ROD).onLightningStrike(c.getLevel().getBlockState(potential_rod), c.getLevel(), potential_rod);
                    Helper.drawParticleLine(c.getLevel(), ParticleTypes.ELECTRIC_SPARK,
                            c.getBlockPos().getX() + 0.5F, c.getBlockPos().getY() + 0.5625F, c.getBlockPos().getZ() + 0.5F,
                            potential_rod.getX(), potential_rod.getY() + 0.5F, potential_rod.getZ(), 16, 0.3);
                    c.getLevel().playSound(null, potential_rod, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 0.1F, 1.3F);
                }
            }else{
                AABB aoe = new AABB(c.getBlockPos());
                aoe = aoe.inflate(ConfigMan.COMMON.crucibleRange.get()); // Inflate the AOE to be 5x the size of the crucible?
                List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
                if(nearby_ents.isEmpty()){
                    return c;
                }
                LivingEntity victim = nearby_ents.get(0);

                if(!c.getLevel().isClientSide){
                    victim.hurt(DamageSource.MAGIC, 12);
                    Helper.drawParticleLine(c.getLevel(), ParticleTypes.ELECTRIC_SPARK,
                            c.getBlockPos().getX() + 0.5F, c.getBlockPos().getY() + 0.5625F, c.getBlockPos().getZ() + 0.5F,
                            victim.getX(), victim.getEyeHeight()/2 + victim.getY(), victim.getZ(), 16, 0.3);
                }
            }
            c.electricCharge = 0;
        }
        c.setChanged();
        return c;
    }

}
