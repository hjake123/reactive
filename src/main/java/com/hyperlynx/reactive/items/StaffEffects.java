package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.util.Helper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

// A container class for the various effects that the staff items can have when right-clicked.
// Similar in concept to ReactionEffects
public class StaffEffects {
    /*
    - Radiant: Fires beams of light that damage entities and severely damage the Undead.
    - Blazing: Fires a jet of flame that also light the ground on fire.
    - Warped: Fires a short-range zap that breaks blocks and does damage.
    - Soulful: Fires a targeted medium range zap.
    - Arcane: Fires a multiple zaps that home in on surrounding enemies
    - Living: Applies regen to things around it

    Beam casting code is taken from Eclectic, as contributed by petrak@
     */
    public static Player radiance(Player user){
        var blockHit = Helper.playerRayTrace(user.level, user, ClipContext.Fluid.NONE, ClipContext.Block.VISUAL, 32);
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
                        victim.hurt(DamageSource.IN_FIRE, 7);
                    }
                    victim.hurt(DamageSource.MAGIC, 3);
                }
            }
        }else{
            Helper.drawParticleLine(user.level, ParticleTypes.ELECTRIC_SPARK,
                    user.getEyePosition().x, user.getEyePosition().y - 0.4, user.getEyePosition().z,
                    beam_end.x, beam_end.y, beam_end.z, 10, 0.1);
        }
        return user;
    }
}
