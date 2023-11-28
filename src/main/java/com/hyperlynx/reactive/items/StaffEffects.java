package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.blocks.AirLightBlock;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.util.BeamHelper;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.HarvestChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

// A container class for the various effects that the staff items can have when right-clicked.
// Similar in concept to ReactionEffects
public class StaffEffects {
    /*
    - Radiant: Fires beams of light that damage entities and make invisible light sources where they hit.
    - Blazing: Creates and fires Blaze fireballs.
    - Warped: --not in this file--
    - Spectral: Creates a field of damaging souls.
    - Arcane: Fires a multiple zaps that home in on surrounding enemies
    - Living: Applies regen and health boost to things around it, and removes negative effects.

    Beam casting code is taken from Eclectic, as contributed by petrak@
     */
    public static Player radiance(Player user){
        int range = 64;
        var blockHit = BeamHelper.playerRayTrace(user.level, user, ClipContext.Fluid.NONE, ClipContext.Block.VISUAL, range);
        var blockHitPos = blockHit.getLocation();
        var start = user.getEyePosition();
        var end = start.add(user.getLookAngle().scale(range));
        var entityHit = ProjectileUtil.getEntityHitResult(
                user, start, end, new AABB(start, end), e -> e instanceof LivingEntity, Double.MAX_VALUE
        );

        if(user instanceof ServerPlayer){
            if(entityHit != null){
                if(entityHit.getEntity() instanceof LivingEntity victim){
                    if(victim.getMobType().equals(MobType.UNDEAD)){
                        victim.setRemainingFireTicks(300);
                        victim.hurt(DamageSource.playerAttack(user).setIsFire(), 7);
                    }
                    victim.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0));
                }
            }
            if(!blockHit.getType().equals(BlockHitResult.Type.MISS)){
                // Try to toggle light on the side of the hit block.
                BlockPos light_target = new BlockPos(blockHitPos.relative(blockHit.getDirection(), 1));
                if(user.level.getBlockState(light_target).isAir()){
                    user.level.setBlock(light_target,
                            Registration.GLOWING_AIR.get().defaultBlockState().setValue(AirLightBlock.DECAYING, !ConfigMan.COMMON.lightStaffLightsPermanent.get()),
                            Block.UPDATE_ALL);
                }
            }
            user.level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.4F, 1.2F);
        }else{
            ParticleScribe.drawParticleLine(user.level, ParticleTypes.END_ROD,
                    user.getEyePosition().x, user.getEyePosition().y - 0.4, user.getEyePosition().z,
                    blockHitPos.x, blockHitPos.y, blockHitPos.z, 2, 0.1);
        }
        return user;
    }

    public static Player blazing(Player user){
        int range = 24;
        var start = user.getEyePosition();
        var end = start.add(user.getLookAngle().scale(range));
        var entityHit = ProjectileUtil.getEntityHitResult(
                user, start, end, new AABB(start, end), e -> e instanceof LivingEntity, Double.MAX_VALUE
        );

        if(user instanceof ServerPlayer) {
            Vec3 target;
            if(entityHit == null){
                target = end;
            }else{
                target = entityHit.getLocation();
            }
            var fireball_position = start
                    .add(user.getLookAngle().scale(1.5))
                    .add(user.level.random.nextDouble()*2-1, user.level.random.nextDouble()*2-1, user.level.random.nextDouble()*2-1);
            var aim = target.subtract(fireball_position).normalize().scale(0.1);
            SmallFireball fireball = new SmallFireball(user.level, user, aim.x, aim.y, aim.z);
            fireball.setPos(fireball_position);
            user.level.addFreshEntity(fireball);
            user.level.playSound(null, fireball_position.x, fireball_position.y, fireball_position.z, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.25F, 1.0F);
        }
        return user;
    }

    public static Player spectral(Player user){
        var blockHit = BeamHelper.playerRayTrace(user.level, user, ClipContext.Fluid.NONE, ClipContext.Block.COLLIDER, 16);
        var blockHitPos = blockHit.getLocation();

        AABB aoe = new AABB(blockHitPos.subtract(1, 1, 1), blockHitPos.add(1, 1, 1));
        aoe = aoe.inflate(1.5);

        if(user instanceof ServerPlayer) {
            for(LivingEntity victim : user.level.getEntitiesOfClass(LivingEntity.class, aoe)){
                if(victim instanceof ServerPlayer && !(victim.equals(user)))
                    continue; // This staff cannot hurt players other than the user.
                victim.hurt(DamageSource.playerAttack(user).setMagic(), 3);
                victim.knockback(0.3, user.level.random.nextDouble()*0.2 - 0.1, user.level.random.nextDouble()*0.2 - 0.1);
            }
            user.level.playSound(null, blockHitPos.x, blockHitPos.y, blockHitPos.z, SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.5F,
                    user.level.random.nextFloat()*0.1f + 0.95f);
        }else{
            ParticleScribe.drawParticleBox(user.level, ParticleTypes.SOUL, aoe, 10);
            user.level.addParticle(ParticleTypes.SOUL, blockHitPos.x, blockHitPos.y, blockHitPos.z, 0, 0, 0);
        }
        return user;
    }

    public static Player missile(Player user){
        if (user instanceof ServerPlayer) {
            AABB aoe = new AABB(user.position().subtract(1, 1, 1), user.position().add(1, 1, 1));
            aoe = aoe.inflate(6);
            List<LivingEntity> nearby_ents = user.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            nearby_ents.remove(user);
            for(int i = 0; i < 3; i++) {
                if(nearby_ents.isEmpty())
                    break;
                LivingEntity victim = nearby_ents.get(user.level.random.nextInt(0, nearby_ents.size()));
                victim.hurt(DamageSource.playerAttack(user).setMagic(), 2);
                ParticleScribe.drawParticleZigZag(user.level, Registration.SMALL_RUNE_PARTICLE, user.getX(), user.getEyeY() - 0.4, user.getZ(),
                        victim.getX(), victim.getEyeY(), victim.getZ(), 2, 5, 0.7);
                user.level.playSound(null,  victim.getX(), victim.getEyeY(), victim.getZ(), SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.PLAYERS, 0.30F,
                        user.level.random.nextFloat()*0.1f + 0.8f);
            }
        }
        return user;
    }

    public static Player living(Player user){
        if (user.getLevel().random.nextFloat() < 0.4) {
            AABB aoe = new AABB(user.position().subtract(1, 1, 1), user.position().add(1, 1, 1));
            aoe = aoe.inflate(5);
            List<LivingEntity> nearby_ents = user.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity victim : nearby_ents) {
                boolean has_regen = false, has_hp_up = false;
                for(MobEffectInstance mei : victim.getActiveEffects()){
                    if(!mei.getEffect().isBeneficial())
                        victim.removeEffect(mei.getEffect());
                    else if(mei.getEffect().equals(MobEffects.HEALTH_BOOST)){
                        mei.update(new MobEffectInstance(MobEffects.HEALTH_BOOST, 500, 2));
                        has_hp_up = true;
                    }
                    else if(mei.getEffect().equals(MobEffects.REGENERATION)){
                        mei.update(new MobEffectInstance(MobEffects.REGENERATION, 50, 2));
                        has_regen = true;
                    }
                }
                if(!has_regen)
                    victim.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, 2));
                if(!has_hp_up)
                    victim.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 500, 2));
            }
        }

        for(int i = 0; i < 10; i++){
            user.level.addParticle(ParticleTypes.CRIMSON_SPORE, user.getRandomX(5.0), user.getY(),
                    user.getRandomZ(5.0), 0, 0, 0);
        }
        user.level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.PLAYERS, 1F, 1f);

        return user;
    }

}
