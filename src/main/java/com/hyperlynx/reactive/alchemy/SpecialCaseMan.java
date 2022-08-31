package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.Helper;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

// Handles various circumstances that go beyond the normal logic of the mod.
public class SpecialCaseMan {

    public static void checkDissolveSpecialCases(CrucibleBlockEntity c, ItemEntity e){
        if(e.getItem().is(Tags.Items.ENDER_PEARLS))
            enderPearlDissolve(c.getLevel(), c.getBlockPos(), e);
        if(e.getItem().is(Tags.Items.GUNPOWDER) && c.getPowerLevel(Powers.BLAZE_POWER.get()) > 10)
            explodeGunpowderDueToBlaze(Objects.requireNonNull(c.getLevel()), c.getBlockPos(), e);
        if(e.getItem().is(Items.CARVED_PUMPKIN))
            pumpkinMagic(Objects.requireNonNull(c.getLevel()), e, c);
    }

    public static void checkEmptySpecialCases(CrucibleBlockEntity c){
        if(c.getLevel() == null) return;
        if(c.getPowerLevel(Powers.SOUL_POWER.get()) > WorldSpecificValue.get(c.getLevel(), "soul_escape_threshold", 300, 600))
            soulEscape(c);
        if(c.getPowerLevel(Powers.CURSE_POWER.get()) > 666)
            curseEscape(c);
        if(c.getPowerLevel(Powers.BLAZE_POWER.get()) > WorldSpecificValue.get(c.getLevel(), "blaze_escape_threshold", 20, 100))
            blazeEscape(c);
    }

    // Dissolving a carved pumpkin might have many effects.
    private static void pumpkinMagic(Level level, ItemEntity e, CrucibleBlockEntity c) {
        if (level.isClientSide)
            return;

        int cause = WorldSpecificValues.GOLEM_CAUSE.get(level);
        BlockPos candlePos = c.areaMemory.fetch(level, ConfigMan.COMMON.crucibleRange.get(), Blocks.CANDLE);
        boolean ironSymbol = c.areaMemory.exists(level, ConfigMan.COMMON.crucibleRange.get(), Registration.IRON_SYMBOL.get());

        if (candlePos != null && !ironSymbol && level.getBlockState(candlePos).getValue(CandleBlock.LIT)) {
            if (cause == 1) { // It's most likely that an Allay will spawn.
                if (level.random.nextFloat() > 0.07 && !(c.getPowerLevel(Powers.CURSE_POWER.get()) > 20))
                    EntityType.ALLAY.spawn((ServerLevel) level, null, null, candlePos, MobSpawnType.MOB_SUMMONED, true, true);
                else
                    EntityType.VEX.spawn((ServerLevel) level, null, null, candlePos, MobSpawnType.MOB_SUMMONED, true, true);
            } else if (cause == 2) { // It's most likely that a Vex will spawn.
                if (level.random.nextFloat() > 0.07 && !(c.getPowerLevel(Powers.MIND_POWER.get()) > 20))
                    EntityType.VEX.spawn((ServerLevel) level, null, null, candlePos, MobSpawnType.MOB_SUMMONED, true, true);
                else
                    EntityType.ALLAY.spawn((ServerLevel) level, null, null, candlePos, MobSpawnType.MOB_SUMMONED, true, true);
            }
            e.kill();
            Helper.drawParticleLine(level, ParticleTypes.ENCHANTED_HIT,
                    c.getBlockPos().getX() + 0.5, c.getBlockPos().getY() + 0.5125, c.getBlockPos().getZ() + 0.5,
                    candlePos.getX() + 0.5, candlePos.getY() + 0.38, candlePos.getZ() + 0.5, 20);

            for(int i = 0; i < 10; i++) {
                ((ServerLevel) level).sendParticles(ParticleTypes.POOF,
                        candlePos.getX() + 0.5, candlePos.getY() + 0.38, candlePos.getZ() + 0.5,
                        1, 0, 0, 0, 0.0);
            }

            level.playSound(null, candlePos, SoundEvents.SOUL_ESCAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    // Dissolving an Ender Pearl teleports you onto the crucible.
    private static void enderPearlDissolve(Level l, BlockPos p, ItemEntity e){
        for(int i = 0; i < 32; ++i) {
            ((ServerLevel) l).sendParticles(ParticleTypes.PORTAL, e.getX(), e.getY() + l.random.nextDouble() * 2.0, e.getZ(), 1, l.random.nextGaussian(), 0.0, l.random.nextGaussian(), 0.0);
        }

        UUID thrower = e.getThrower();
        if(thrower != null) {
            Player player = l.getPlayerByUUID(thrower);
            if(player != null){
                player.teleportTo(p.getX()+0.5, p.getY() + 0.85, p.getZ() + 0.5);
            }
        }
    }

    // Explode gunpowder due to blaze.
    private static void explodeGunpowderDueToBlaze(Level l, BlockPos p, ItemEntity e){
        l.explode(e, p.getX(), p.getY(), p.getZ(), 1.0F, Explosion.BlockInteraction.NONE);
        e.kill();
    }

    private static void soulEscape(CrucibleBlockEntity c){
        if(c.getLevel() == null) return;
        if(c.getLevel().isClientSide()){
            c.getLevel().addParticle(ParticleTypes.SOUL, c.getBlockPos().getX() + 0.5, c.getBlockPos().getY() + 0.65,
                    c.getBlockPos().getZ() + 0.5, 0, 0, 0);
        }else{
            ((ServerLevel) c.getLevel()).sendParticles(ParticleTypes.SOUL, c.getBlockPos().getX() + 0.5, c.getBlockPos().getY() + 0.65,
                    c.getBlockPos().getZ() + 0.5, 1,0, 0, 0, 0.0);
        }
    }

    private static void curseEscape(CrucibleBlockEntity c){
        if(c.getLevel() == null) return;
        AABB aoe = new AABB(c.getBlockPos());
        aoe.inflate(5); // Inflate the AOE to be 5x the size of the crucible.
        if(!c.getLevel().isClientSide()){
            List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for(LivingEntity e : nearby_ents){
                e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 1));
                e.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
                e.hurt(DamageSource.MAGIC, 10);
            }
            c.getLevel().playSound(null, c.getBlockPos(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1, 1);
        }
    }

    private static void blazeEscape(CrucibleBlockEntity c){
        if(c.getLevel() == null) return;
        AABB blast_zone = new AABB(c.getBlockPos());
        blast_zone.inflate(0, 3, 0); // Inflate the AOE to be 5x the size of the crucible.
        if(!c.getLevel().isClientSide()){
            List<LivingEntity> nearby_ents = c.getLevel().getEntitiesOfClass(LivingEntity.class, blast_zone);
            for(LivingEntity e : nearby_ents){
                e.hurt(DamageSource.IN_FIRE, 12);
                e.setSecondsOnFire(3);
            }
            c.getLevel().playSound(null, c.getBlockPos(), SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0F, 1.0F);
            for(int i = 0; i < 10; i++)
                Helper.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.FLAME, c.getBlockPos(), 1, 0, 1, 0);
        }
    }
}
