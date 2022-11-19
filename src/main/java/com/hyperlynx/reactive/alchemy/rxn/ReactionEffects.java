package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.SpecialCaseMan;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.items.CrystalIronItem;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.Helper;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.Random;

// Just a holder class for the various reaction effect methods.
public class ReactionEffects {

    // TODO: testing

    // Destroys the contents of the Crucible and some connected Symbols unless there is an Iron Symbol.
    public static CrucibleBlockEntity explosion(CrucibleBlockEntity c) {
        if (!c.getLevel().isClientSide){
            BlockPos pos = c.getBlockPos();

            Helper.drawParticleZigZag(c.getLevel(), ParticleTypes.SMOKE, pos, c.areaMemory.fetch(c.getLevel(),
                    ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get()), 20, 7, 0.8F);

            if(c.areaMemory.exists(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.IRON_SYMBOL.get())){
                Helper.drawParticleZigZag(c.getLevel(), ParticleTypes.SMOKE, c.areaMemory.fetch(c.getLevel(),
                        ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get()), c.areaMemory.fetch(c.getLevel(),
                        ConfigMan.COMMON.crucibleRange.get(), Registration.IRON_SYMBOL.get()), 20, 7, 0.8F);
            }else{
                SpecialCaseMan.checkEmptySpecialCases(c);
                c.expendPower();
                c.getLevel().setBlock(pos, c.getLevel().getBlockState(pos).setValue(CrucibleBlock.FULL, false), Block.UPDATE_CLIENTS);
                c.getLevel().explode(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 1.0F, Explosion.BlockInteraction.NONE);

                if(c.areaMemory.exists(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get()))
                    c.getLevel().removeBlock(c.areaMemory.fetch(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get()), true);
            }

        }
        return c;
    }

    // Changes the Gold Symbol into Active Gold Foam, which spreads outwards for a limited distance and leaves Gold Foam behind.
    public static CrucibleBlockEntity formation(CrucibleBlockEntity c) {
        BlockPos symbol_position = c.areaMemory.fetch(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get());
        if (!c.getLevel().isClientSide){
            c.getLevel().setBlock(symbol_position, Registration.ACTIVE_GOLD_FOAM.get().defaultBlockState(), Block.UPDATE_CLIENTS);
        }else{
            Helper.drawParticleZigZag(c.getLevel(), ParticleTypes.EFFECT,
                    c.getBlockPos().getX() + 0.5F, c.getBlockPos().getY() + 0.5625F, c.getBlockPos().getZ() + 0.5F,
                    symbol_position.getX()+0.5, symbol_position.getY()+0.5, symbol_position.getZ()+0.5, 12, 7,0.4);
        }
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

        BlockPos origin_pos = c.areaMemory.fetch(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get());
        if(origin_pos == null){
            origin_pos = c.getBlockPos();
        }

        switch(WorldSpecificValue.get(Objects.requireNonNull(c.getLevel()), "levitation_reaction_effect", 1, 3)){
            case 1:
                for(LivingEntity e : nearby_ents){
                    if(CrystalIronItem.effectNotBlocked(c.getLevel(), e, 4)) {
                        e.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 1));
                    }
                    Helper.drawParticleZigZag(c.getLevel(), ParticleTypes.ENCHANTED_HIT,
                            origin_pos.getX() + 0.5, origin_pos.getY() + 0.5625, origin_pos.getZ() + 0.5,
                            e.getX(),e.getEyeY()-0.1, e.getZ(), 10, 7, 0.3);
                }
                break;
            case 2:
                for(LivingEntity e : nearby_ents){
                    if(CrystalIronItem.effectNotBlocked(c.getLevel(), e, 4)) {
                        e.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 1));
                    }
                    Helper.drawParticleZigZag(c.getLevel(), ParticleTypes.ENCHANTED_HIT,
                            origin_pos.getX() + 0.5, origin_pos.getY() + 0.5625, origin_pos.getZ() + 0.5,
                            e.getX(), e.getEyeY()-0.1, e.getZ(), 5, 12, 0.7);
                }
                break;
            case 3:
                ShulkerBullet bullet = new ShulkerBullet(EntityType.SHULKER_BULLET, c.getLevel());
                bullet.setPos(Vec3.atCenterOf(origin_pos).add(0, 0.3, 0));
                c.getLevel().addFreshEntity(bullet);
        }
        return c;
    }

    // Causes nearby bonemeal-ables to be fertilized occasionally.
    public static CrucibleBlockEntity growth(CrucibleBlockEntity c) {
        if(c.getLevel().isClientSide){
            Helper.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.HAPPY_VILLAGER, c.getBlockPos(), 0.1F);
        }else {
            Random random = new Random();
            BlockPos target = c.getBlockPos().offset(random.nextInt(-32, 32), random.nextInt(-1, 0), random.nextInt(-32, 32));
            if (Objects.requireNonNull(c.getLevel()).getBlockState(target).getBlock() instanceof BonemealableBlock) {
                ((BonemealableBlock) c.getLevel().getBlockState(target).getBlock()).performBonemeal((ServerLevel) c.getLevel(), c.getLevel().random, target, c.getLevel().getBlockState(target));
            }
        }
        return c;
    }

    // Shoot flames from the crucible!
    public static CrucibleBlockEntity flamethrower(CrucibleBlockEntity c) {
        if(c.getLevel() == null) return c;

        AABB blast_zone = new AABB(c.getBlockPos());
        blast_zone.inflate(3, 5, 3);

        if(c.getLevel().isClientSide){
            if(c.getPowerLevel(Powers.SOUL_POWER.get()) > 20){
                Helper.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.SOUL_FIRE_FLAME, c.getBlockPos(), 0.1F, 0, 0.1, 0);
            }else{
                Helper.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.FLAME, c.getBlockPos(), 0.1F, 0, 0.1, 0);
            }
        }else {
            List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, blast_zone);
            for(LivingEntity e : nearby_ents){
                e.hurt(DamageSource.IN_FIRE, 12);
                e.setSecondsOnFire(7);
            }
        }
        return c;
    }

    // Cause blocks to fall down near the Crucible.
    public static CrucibleBlockEntity blockfall(CrucibleBlockEntity c) {
        Level level = c.getLevel();
        RandomSource random = level.random;
        BlockPos target = c.getBlockPos().offset(random.nextInt(-4, 4), random.nextInt(-4, 4), random.nextInt(-4, 4));
        if(target == c.getBlockPos()) return c;

        if(!level.isClientSide){
            FallingBlockEntity.fall(level, target, level.getBlockState(target));
        }else{
            Helper.drawParticleZigZag(level, ParticleTypes.END_ROD, c.getBlockPos(), target, 8, 32, 0.7F);
        }
        return c;
    }
}
