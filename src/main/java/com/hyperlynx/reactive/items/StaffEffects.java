package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.fx.ParticleScribe;
import com.hyperlynx.reactive.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;

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
            ParticleScribe.drawParticleLine(user.level, ParticleTypes.ELECTRIC_SPARK,
                    user.getEyePosition().x, user.getEyePosition().y - 0.4, user.getEyePosition().z,
                    beam_end.x, beam_end.y, beam_end.z, 10, 0.1);
        }
        return user;
    }

    public static Player blazing(Player user){
        var blockHit = Helper.playerRayTrace(user.level, user, ClipContext.Fluid.NONE, ClipContext.Block.COLLIDER, 16);
        var blockHitPos = blockHit.getLocation();
        var start = user.getEyePosition();
        start = start.add(0, -0.3, 0);

        if(user instanceof ServerPlayer) {
            AABB aoe = new AABB(start, blockHitPos);
            for(LivingEntity victim : user.level.getEntitiesOfClass(LivingEntity.class, aoe)){
                victim.setRemainingFireTicks(1000);
                victim.hurt(DamageSource.IN_FIRE, 2);
            }
        }else{
            ParticleScribe.drawParticleStream(user.level, ParticleTypes.FLAME, start, user.getLookAngle(), 5);
        }
        return user;
    }

    public static Player warping(Player user) {
        var blockHit = Helper.playerRayTrace(user.level, user, ClipContext.Fluid.NONE, ClipContext.Block.OUTLINE, 16);
        var blockHitPos = blockHit.getLocation();
        var start = user.getEyePosition();
        var end = start.add(user.getLookAngle().scale(16));
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
            BlockState hit_state = user.level.getBlockState(blockHit.getBlockPos());
            if(entityHit != null){
                if(entityHit.getEntity() instanceof LivingEntity victim){
                    victim.hurt(DamageSource.MAGIC, 5);
                }
            }
            if(canMineBlock(user.level, user, blockHit.getBlockPos(), user.level.getBlockState(blockHit.getBlockPos()))){
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

    public static boolean canMineBlock(Level level, Player player, BlockPos pos, BlockState state) {
        if (!player.mayBuild() || !level.mayInteract(player, pos))
            return false;

        if (MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level, pos, state, player)))
            return false;

        Block candidate_to_break = state.getBlock();
        return !(candidate_to_break.defaultDestroyTime() < 0) && !(candidate_to_break.defaultDestroyTime() > 30F);
    }
}
