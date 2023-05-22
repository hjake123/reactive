package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.alchemy.SpecialCaseMan;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.blocks.ShulkerCrucibleBlock;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.items.CrystalIronItem;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.advancements.FlagCriterion;
import com.hyperlynx.reactive.util.HarvestChecker;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;
import java.util.Random;

// Just a holder class for the various reaction effect methods. Only for use on the server side.
public class ReactionEffects {
    // Destroys the contents of the Crucible and some connected Symbols unless there is an Iron Symbol.
    public static CrucibleBlockEntity explosion(CrucibleBlockEntity c) {
        BlockPos pos = c.getBlockPos();

        ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.SMOKE, pos, c.areaMemory.fetch(c.getLevel(),
                ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get()), 20, 7, 0.8F);

        if(c.areaMemory.exists(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.IRON_SYMBOL.get())){
            ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.SMOKE, c.areaMemory.fetch(c.getLevel(),
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
        return c;
    }

    // Changes the Gold Symbol into Active Gold Foam, which spreads outwards for a limited distance and leaves Gold Foam behind.
    public static CrucibleBlockEntity foaming(CrucibleBlockEntity c) {
        BlockPos symbol_position = c.areaMemory.fetch(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get());
        c.getLevel().setBlock(symbol_position, Registration.ACTIVE_GOLD_FOAM.get().defaultBlockState(), Block.UPDATE_CLIENTS);
        return c;
    }

    public static CrucibleBlockEntity smoke(CrucibleBlockEntity c) {
        if (c.getLevel().random.nextFloat() < 0.4) {
            AABB aoe = new AABB(c.getBlockPos());
            aoe = aoe.inflate(3); // Inflate the AOE to be 3x the size of the crucible.
            List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity e : nearby_ents) {
                if (CrystalIronItem.effectNotBlocked(e, 1)) {
                    e.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
                    e.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1));
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
                }
            }
        }
        return c;
    }

    public static CrucibleBlockEntity salt(CrucibleBlockEntity c) {
        if(c.getTotalPowerLevel() < WorldSpecificValue.get("salt_overflow_threshold", 1000, 1300)){
            ItemEntity salt_drop = new ItemEntity(c.getLevel(), c.getBlockPos().getX() + 0.5,
                    c.getBlockPos().getY() + 0.5,
                    c.getBlockPos().getZ() + 0.6, Registration.SALT.get().getDefaultInstance());
            c.getLevel().addFreshEntity(salt_drop);
        }else{
            CrucibleBlockEntity.empty(c.getLevel(), c.getBlockPos(), c.getBlockState(), c);
            if(c.getBlockState().getBlock() instanceof ShulkerCrucibleBlock) {
                ItemEntity shell_drop = new ItemEntity(c.getLevel(), c.getBlockPos().getX() + 0.5,
                        c.getBlockPos().getY() + 0.5,
                        c.getBlockPos().getZ() + 0.6, Items.SHULKER_SHELL.getDefaultInstance());
                c.getLevel().addFreshEntity(shell_drop);
            }
            c.getLevel().setBlock(c.getBlockPos(), Registration.SALTY_CRUCIBLE.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            c.getLevel().playSound(null, c.getBlockPos(), SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
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
                    ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.ELECTRIC_SPARK,
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
                    if(CrystalIronItem.effectNotBlocked(victim, 2))
                        victim.hurt(DamageSource.MAGIC, 12);
                    ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.ELECTRIC_SPARK,
                            c.getBlockPos().getX() + 0.5F, c.getBlockPos().getY() + 0.5625F, c.getBlockPos().getZ() + 0.5F,
                            victim.getX(), victim.getEyeHeight() / 2 + victim.getY(), victim.getZ(), 8, 10, 0.3);
                }
            }
            c.electricCharge = 0;
        }
        c.setChanged();
        return c;
    }

    // Either apply levitation to nearby entities.
    public static CrucibleBlockEntity levitation(CrucibleBlockEntity c) {
        AABB aoe = new AABB(c.getBlockPos());
        aoe = aoe.inflate(12); // Inflate the AOE to be 6x the size of the crucible.
        List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);

        BlockPos origin_pos = c.areaMemory.fetch(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get());
        if(origin_pos == null){
            origin_pos = c.getBlockPos();
        }

        for(LivingEntity e : nearby_ents){
            if(CrystalIronItem.effectNotBlocked(e, 4)) {
                e.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 1));
            }
            ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.END_ROD,
                    origin_pos.getX() + 0.5, origin_pos.getY() + 0.5625, origin_pos.getZ() + 0.5,
                    e.getX(),e.getEyeY()-0.1, e.getZ(), 8, 7, 0.74);
        }
        return c;
    }

    // Causes nearby bonemeal-ables to be fertilized occasionally.
    public static CrucibleBlockEntity growth(CrucibleBlockEntity c) {
        Random random = new Random();
        BlockPos target = c.getBlockPos().offset(random.nextInt(-32, 32), random.nextInt(-1, 0), random.nextInt(-32, 32));
        if (Objects.requireNonNull(c.getLevel()).getBlockState(target).getBlock() instanceof BonemealableBlock) {
            ((BonemealableBlock) c.getLevel().getBlockState(target).getBlock()).performBonemeal((ServerLevel) c.getLevel(), c.getLevel().random, target, c.getLevel().getBlockState(target));
        }
        return c;
    }

    // Shoot flames from the crucible!
    public static CrucibleBlockEntity flamethrower(CrucibleBlockEntity c) {
        if(c.getLevel() == null) return c;

        AABB blast_zone = new AABB(c.getBlockPos());
        blast_zone = blast_zone.inflate(2, 5, 2);

        List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, blast_zone);
        for(LivingEntity e : nearby_ents){
            e.hurt(DamageSource.IN_FIRE, 12);
            e.setSecondsOnFire(7);
        }
        return c;
    }

    // Cause blocks to fall down near the Symbol.
    public static CrucibleBlockEntity blockfall(CrucibleBlockEntity c) {
        Level level = c.getLevel();
        RandomSource random = level.random;
        BlockPos symbol_pos = c.areaMemory.fetch(level, ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get());
        if(symbol_pos == null)
            return c;

        for(int i = 0; i < 10; i++) {
            BlockPos target = symbol_pos.offset(random.nextInt(-4, 4), random.nextInt(0, 4), random.nextInt(-4, 4));
            if (target == c.getBlockPos() || target == symbol_pos) continue;
            BlockState target_state = level.getBlockState(target);
            if (!target_state.isAir() && HarvestChecker.canMineBlock(c.getLevel(), target, target_state, 35F)) {
                FallingBlockEntity.fall(level, target, target_state);
                ParticleScribe.drawParticleZigZag(level, ParticleTypes.END_ROD, c.getBlockPos(), target, 8, 32, 0.7F);
                ItemEntity drop = new ItemEntity(level, c.getBlockPos().getX()+0.5, c.getBlockPos().getY()+0.6, c.getBlockPos().getZ()+0.5,
                        Registration.MOTION_SALT.get().getDefaultInstance());
                level.addFreshEntity(drop);
                FlagCriterion.triggerForNearbyPlayers((ServerLevel) level, CriteriaTriggers.BLOCK_FALL_TRIGGER, c.getBlockPos(), 16);
            }
        }
        return c;
    }
}
