package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

// Handles various circumstances that go beyond the normal logic of the mod.
public class SpecialCaseMan {

    public static void checkDissolveSpecialCases(CrucibleBlockEntity c, ItemEntity e){
        if(e.getItem().is(Tags.Items.ENDER_PEARLS)) enderPearlDissolve(c.getLevel(), c.getBlockPos(), e);
        if(e.getItem().is(Tags.Items.GUNPOWDER) && c.getPowerLevel(Powers.BLAZE_POWER.get()) > 10)
            explodeGunpowderDueToBlaze(Objects.requireNonNull(c.getLevel()), c.getBlockPos(), e);

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
