package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.fx.ParticleScribe;
import com.hyperlynx.reactive.util.BeamHelper;
import com.hyperlynx.reactive.util.HarvestChecker;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

// A container class for the various effects that the staff items can have when right-clicked.
// Similar in concept to ReactionEffects
public class StaffEffects {
    /*
    - Radiant: Fires beams of light that damage entities and severely damage the Undead.
    - Blazing: Fires a jet of flame that also light the ground on fire.
    - Warped: Not here -- check WarpStaffItem
    - Spectral: Creates a shining point where it hits terrain, which damages entities and slowly breaks blocks.
    - Arcane: Fires a multiple zaps that home in on surrounding enemies
    - Living: Applies regen to things around it

    Beam casting code is taken from Eclectic, as contributed by petrak@
     */
    public static Player radiance(Player user){
        var blockHit = BeamHelper.playerRayTrace(user.level, user, ClipContext.Fluid.NONE, ClipContext.Block.VISUAL, 32);
        var blockHitPos = blockHit.getLocation();
        var start = user.getEyePosition();
        var end = start.add(user.getLookAngle().scale(32));
        var entityHit = ProjectileUtil.getEntityHitResult(
                user, start, end, new AABB(start, end), e -> e instanceof LivingEntity, Double.MAX_VALUE
        );

        // Check which is closer
        Vec3 beam_end;
        if (entityHit == null) {
            beam_end = blockHitPos;
        } else if (entityHit.getLocation().distanceToSqr(start) < blockHitPos.distanceToSqr(start)) {
            beam_end = entityHit.getLocation();
        } else {
            beam_end = blockHitPos;
        }

        if(user instanceof ServerPlayer){
            if(entityHit != null){
                if(entityHit.getEntity() instanceof LivingEntity victim){
                    if(victim.getMobType().equals(MobType.UNDEAD)){
                        victim.setRemainingFireTicks(300);
                        victim.hurt(DamageSource.playerAttack(user).setIsFall(), 7);
                    }
                    victim.hurt(DamageSource.playerAttack(user).setMagic(), 3);
                }
            }
        }else{
            ParticleScribe.drawParticleLine(user.level, ParticleTypes.END_ROD,
                    user.getEyePosition().x, user.getEyePosition().y - 0.4, user.getEyePosition().z,
                    beam_end.x, beam_end.y, beam_end.z, 2, 0.1);
        }
        return user;
    }

    public static Player blazing(Player user){
        var blockHit = BeamHelper.playerRayTrace(user.level, user, ClipContext.Fluid.NONE, ClipContext.Block.COLLIDER, 16);
        var blockHitPos = blockHit.getLocation();
        var start = user.getEyePosition();
        start = start.add(0, -0.3, 0);

        if(user instanceof ServerPlayer) {
            AABB aoe = new AABB(start, blockHitPos);
            for(LivingEntity victim : user.level.getEntitiesOfClass(LivingEntity.class, aoe)){
                if(victim.equals(user))
                    continue;
                victim.setRemainingFireTicks(1000);
                victim.hurt(DamageSource.playerAttack(user).setIsFire(), 1);
            }
            if(user.level.getBlockState(blockHit.getBlockPos().above()).isAir())
                user.level.setBlockAndUpdate(blockHit.getBlockPos().above(), Blocks.FIRE.defaultBlockState());
        }else{
            ParticleScribe.drawParticleStream(user.level, ParticleTypes.FLAME, start, user.getLookAngle(), 5);
        }
        return user;
    }

    public static Player spectral(Player user){
        var blockHit = BeamHelper.playerRayTrace(user.level, user, ClipContext.Fluid.NONE, ClipContext.Block.COLLIDER, 16);
        var blockHitPos = blockHit.getLocation();

        if(user instanceof ServerPlayer) {
            AABB aoe = new AABB(blockHitPos.subtract(1, 1, 1), blockHitPos.add(1, 1, 1));
            aoe = aoe.inflate(2);
            for(LivingEntity victim : user.level.getEntitiesOfClass(LivingEntity.class, aoe)){
                if(victim instanceof ServerPlayer)
                    continue; // This staff cannot hurt players.
                victim.hurt(DamageSource.GENERIC, 3);
                victim.knockback(0.3, user.level.random.nextDouble()*0.2 - 0.1, user.level.random.nextDouble()*0.2 - 0.1);
            }
        }else{
            user.level.addParticle(ParticleTypes.SOUL, blockHitPos.x, blockHitPos.y, blockHitPos.z, 0, 0, 0);
        }
        return user;
    }

    public static Player warping(Player user) {
        var blockHit = BeamHelper.playerRayTrace(user.level, user, ClipContext.Fluid.NONE, ClipContext.Block.OUTLINE, 16);
        var blockHitPos = blockHit.getLocation();
        var start = user.getEyePosition();
        var end = start.add(user.getLookAngle().scale(16));
        var entityHit = ProjectileUtil.getEntityHitResult(
                user, start, end, new AABB(start, end), Objects::nonNull, Double.MAX_VALUE
        );

        // Check which is closer
        Vec3 beam_end;
        if (entityHit == null) {
            beam_end = blockHitPos;
        } else if (entityHit.getLocation().distanceToSqr(start) < blockHitPos.distanceToSqr(start)) {
            beam_end = entityHit.getLocation();
        } else {
            beam_end = blockHitPos;
        }

        if(user instanceof ServerPlayer){
            BlockState hit_state = user.level.getBlockState(blockHit.getBlockPos());
            if(entityHit != null){
                if(entityHit.getEntity() instanceof LivingEntity victim){
                    victim.hurt(DamageSource.playerAttack(user).setMagic(), 5);
                }
                if(entityHit.getEntity() instanceof ItemEntity drop_entity){
                    drop_entity.teleportTo(user.getX(), user.getY(), user.getZ());
                }
            }
            else if(HarvestChecker.canMineBlock(user.level, user, blockHit.getBlockPos(), user.level.getBlockState(blockHit.getBlockPos()), 30F)){
                hit_state.getBlock().playerWillDestroy(user.level, blockHit.getBlockPos(), hit_state, user);
                hit_state.getBlock().playerDestroy(user.level, user, blockHit.getBlockPos(), hit_state, null, Items.IRON_PICKAXE.getDefaultInstance());
                user.level.removeBlock(blockHit.getBlockPos(), false);
            }
        }else{
            ParticleScribe.drawParticleZigZag(user.level, ParticleTypes.ENCHANTED_HIT,
                    user.getEyePosition().x, user.getEyePosition().y - 0.4, user.getEyePosition().z,
                    beam_end.x, beam_end.y, beam_end.z, 4, 4,0.1);
        }
        return user;
    }


    public static Player missile(Player user){
        if (user instanceof ServerPlayer) {
            AABB aoe = new AABB(user.position().subtract(1, 1, 1), user.position().add(1, 1, 1));
            aoe = aoe.inflate(7);
            List<LivingEntity> nearby_ents = user.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            nearby_ents.remove(user);
            for(int i = 0; i < 3; i++) {
                if(nearby_ents.isEmpty())
                    break;
                LivingEntity victim = nearby_ents.get(user.level.random.nextInt(0, nearby_ents.size()));
                victim.hurt(DamageSource.playerAttack(user).setMagic(), 3);
                ParticleScribe.drawParticleZigZag(user.level, Registration.SMALL_RUNE_PARTICLE, user.getX(), user.getEyeY() - 0.4, user.getZ(),
                        victim.getX(), victim.getEyeY(), victim.getZ(), 2, 5, 0.7);
            }
        }
        return user;
    }

    public static Player living(Player user){
        if (user.getLevel().random.nextFloat() < 0.4) {
            AABB aoe = new AABB(user.position().subtract(1, 1, 1), user.position().add(1, 1, 1));
            aoe = aoe.inflate(5); // Inflate the AOE to be 3x the size of the crucible.
            List<LivingEntity> nearby_ents = user.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity victim : nearby_ents) {
                victim.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30, 1));
            }
        }

        for(int i = 0; i < 10; i++){
            user.level.addParticle(ParticleTypes.CRIMSON_SPORE, user.getRandomX(5.0), user.getY(),
                    user.getRandomZ(5.0), 0, 0, 0);
        }

        return user;
    }

}
