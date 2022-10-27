package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.items.CrystalIronItem;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.Helper;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

// Just a holder class for the various reaction effect methods.
public class ReactionEffects {

    // TODO: a lot of reaction effects

    // Will attract entities towards a memorized gold symbol.
    public static CrucibleBlockEntity vortex(CrucibleBlockEntity c) {
        if (!c.getLevel().isClientSide)
            System.out.println("*sounds of spiraling*");
        return c;
    }

    // Creates various items or blocks depending on the surroundings.
    public static CrucibleBlockEntity formation(CrucibleBlockEntity c) {
        if (!c.getLevel().isClientSide)
            System.out.println("Gogograhgrah!");
        return c;
    }

    public static CrucibleBlockEntity sicklySmoke(CrucibleBlockEntity c) {
        if (!c.getLevel().isClientSide) {
            if (c.getLevel().random.nextFloat() < 0.4) {
                AABB aoe = new AABB(c.getBlockPos());
                aoe.inflate(3); // Inflate the AOE to be 3x the size of the crucible.
                List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
                for (LivingEntity e : nearby_ents) {
                    if (CrystalIronItem.effectNotBlocked(c.getLevel(), e, 1)) {
                        e.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
                        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
                    }
                }
            }
        } else {
            Helper.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.LARGE_SMOKE, c.getBlockPos(), 0.3F);
        }
        return c;
    }

    public static CrucibleBlockEntity weakeningSmoke(CrucibleBlockEntity c) {
        if (!c.getLevel().isClientSide) {
            if (c.getLevel().random.nextFloat() < 0.4) {
                AABB aoe = new AABB(c.getBlockPos());
                aoe.inflate(3); // Inflate the AOE to be 3x the size of the crucible.
                List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
                for (LivingEntity e : nearby_ents) {
                    if (CrystalIronItem.effectNotBlocked(c.getLevel(), e, 2)) {
                        e.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1));
                        e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
                    }
                }
            }
        } else {
            Helper.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.ASH, c.getBlockPos(), 0.3F);
        }
        return c;
    }

    public static CrucibleBlockEntity discharge(CrucibleBlockEntity c) {
        c.electricCharge += 5;
        if (c.electricCharge > 21) {
            BlockPos potential_rod = c.areaMemory.fetch(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Blocks.LIGHTNING_ROD);
            if (potential_rod != null) {
                if (!c.getLevel().isClientSide) {
                    ((LightningRodBlock) Blocks.LIGHTNING_ROD).onLightningStrike(c.getLevel().getBlockState(potential_rod), c.getLevel(), potential_rod);
                    Helper.drawParticleZigZag(c.getLevel(), ParticleTypes.ELECTRIC_SPARK,
                            c.getBlockPos().getX() + 0.5F, c.getBlockPos().getY() + 0.5625F, c.getBlockPos().getZ() + 0.5F,
                            potential_rod.getX()+0.5, potential_rod.getY()+0.5, potential_rod.getZ()+0.5, 8, 10,0.6);
                    c.getLevel().playSound(null, potential_rod, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 0.1F, 1.3F);
                }
            } else {
                AABB aoe = new AABB(c.getBlockPos());
                aoe = aoe.inflate(ConfigMan.COMMON.crucibleRange.get()); // Inflate the AOE to be 5x the size of the crucible?
                List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
                if (nearby_ents.isEmpty()) {
                    return c;
                }
                LivingEntity victim = nearby_ents.get(0);

                if (!c.getLevel().isClientSide) {
                    if(CrystalIronItem.effectNotBlocked(c.getLevel(), victim, 2))
                        victim.hurt(DamageSource.MAGIC, 12);
                    Helper.drawParticleZigZag(c.getLevel(), ParticleTypes.ELECTRIC_SPARK,
                            c.getBlockPos().getX() + 0.5F, c.getBlockPos().getY() + 0.5625F, c.getBlockPos().getZ() + 0.5F,
                            victim.getX(), victim.getEyeHeight() / 2 + victim.getY(), victim.getZ(), 8, 10, 0.3);
                }
            }
            c.electricCharge = 0;
        }
        c.setChanged();
        return c;
    }

    // Either apply levitation to nearby entities, apply slow falling, or shoot a shulker bullet.
    public static CrucibleBlockEntity levitation(CrucibleBlockEntity c) {
        AABB aoe = new AABB(c.getBlockPos());
        aoe.inflate(6); // Inflate the AOE to be 6x the size of the crucible.
        List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
        switch(WorldSpecificValue.get(Objects.requireNonNull(c.getLevel()), "levitation_reaction_effect", 1, 3)){
            case 1:
                for(LivingEntity e : nearby_ents){
                    e.addEffect(new MobEffectInstance(MobEffects.LEVITATION));
                    Helper.drawParticleZigZag(c.getLevel(), ParticleTypes.END_ROD,
                            c.getBlockPos().getX() + 0.5, c.getBlockPos().getY() + 0.5625, c.getBlockPos().getZ() + 0.5,
                            e.getX(), e.getEyeY(), e.getZ(), 10, 7, 0.3);
                }
                break;
            case 2:
                for(LivingEntity e : nearby_ents){
                    e.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING));
                    Helper.drawParticleZigZag(c.getLevel(), ParticleTypes.ENCHANTED_HIT,
                            c.getBlockPos().getX() + 0.5, c.getBlockPos().getY() + 0.5625, c.getBlockPos().getZ() + 0.5,
                            e.getX(), e.getEyeY(), e.getZ(), 5, 12, 0.7);
                }
                break;
            case 3:
                ShulkerBullet bullet = new ShulkerBullet(EntityType.SHULKER_BULLET, c.getLevel());
                bullet.setPos(Vec3.atCenterOf(c.getBlockPos()).add(0, 0.3, 0));
                c.getLevel().addFreshEntity(bullet);
        }
        return c;
    }

    // Causes nearby bonemeal-ables to be fertilized occasionally.
    public static CrucibleBlockEntity growth(CrucibleBlockEntity c) {
        return c;
    }

}
