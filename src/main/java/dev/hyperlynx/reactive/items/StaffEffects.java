package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.registration.*;
import dev.hyperlynx.reactive.blocks.AirLightBlock;
import dev.hyperlynx.reactive.client.particles.EnergyParticle;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.util.BeamHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static void radiance(Player user, ItemStack stack){
        int range = ConfigMan.COMMON.lightStaffRange.get();
        var block_hit = BeamHelper.playerRayTrace(user.level(), user, ClipContext.Fluid.NONE, ClipContext.Block.VISUAL, range);
        var block_hit_pos = block_hit.getBlockPos();
        var start = user.getEyePosition();
        var end = start.add(user.getLookAngle().scale(range));
        var entity_hit = ProjectileUtil.getEntityHitResult(
                user, start, end, new AABB(start, end), e -> e instanceof LivingEntity, Double.MAX_VALUE
        );

        if(user instanceof ServerPlayer serveruser){
            if(entity_hit != null){
                if(entity_hit.getEntity() instanceof LivingEntity victim){
                    if(victim.isInvertedHealAndHarm()){
                        victim.setRemainingFireTicks(300);
                        StaffItem.hurtVictim(serveruser, stack, victim, user.damageSources().inFire(), ConfigMan.COMMON.lightStaffPowerVsUndead.get().floatValue());
                    }
                    victim.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0));
                }
            }
            if(!block_hit.getType().equals(BlockHitResult.Type.MISS)) {
                BlockPos light_target = block_hit_pos.relative(block_hit.getDirection(), 1);
                if(!user.level().isLoaded(light_target)) {
                    return;
                }
                if (user.level().getBlockState(light_target).isAir() && !user.level().getBlockState(light_target).is(ReactiveBlocks.GLOWING_AIR.get())) {
                    user.level().setBlock(light_target,
                            ReactiveBlocks.GLOWING_AIR.get().defaultBlockState().setValue(AirLightBlock.DECAYING, !ConfigMan.COMMON.lightStaffLightsPermanent.get()),
                            Block.UPDATE_ALL_IMMEDIATE);
                } else if (user.level().getBlockState(light_target).is(Blocks.WATER)) {
                    user.level().setBlock(light_target,
                            ReactiveBlocks.GLOWING_AIR.get().defaultBlockState()
                                    .setValue(AirLightBlock.DECAYING, !ConfigMan.COMMON.lightStaffLightsPermanent.get())
                                    .setValue(AirLightBlock.WATERLOGGED, true),
                            Block.UPDATE_ALL_IMMEDIATE);
                }
                user.level().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.4F, 1.2F);
            }
        } else {
            ParticleScribe.drawParticleLine(user.level(), ParticleTypes.END_ROD,
                    user.getEyePosition().x, user.getEyePosition().y - 0.4, user.getEyePosition().z,
                    block_hit.getLocation().x, block_hit.getLocation().y, block_hit.getLocation().z, 2, 0.1);
        }
    }

    public static void blazing(Player user, ItemStack stack){
        int range = ConfigMan.COMMON.blazeStaffRange.get();
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
                    .add(user.level().random.nextDouble()*2-1, user.level().random.nextDouble()*2-1, user.level().random.nextDouble()*2-1);
            var aim = target.subtract(fireball_position).normalize().scale(0.1);

            Fireball fireball;
            if(ConfigMan.COMMON.blazeStaffExplosionSize.get() > 0) {
                fireball = new LargeFireball(user.level(), user, aim, ConfigMan.COMMON.blazeStaffExplosionSize.get());
            } else {
                fireball = new SmallFireball(user.level(), user, aim);
            }
            fireball.setPos(fireball_position);
            user.level().addFreshEntity(fireball);
            user.level().playSound(null, fireball_position.x, fireball_position.y, fireball_position.z, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.25F, 1.0F);
        }
    }

    public static void spectral(Player user, ItemStack stack){
        var blockHit = BeamHelper.playerRayTrace(user.level(), user, ClipContext.Fluid.NONE, ClipContext.Block.COLLIDER, ConfigMan.COMMON.soulStaffRange.get());
        var blockHitPos = blockHit.getLocation();

        AABB aoe = new AABB(blockHitPos.subtract(1, 1, 1), blockHitPos.add(1, 1, 1));
        boolean wide = EnchantmentHelper.has(stack, ReactiveComponentTypes.WIDE_RANGE.value());
        aoe = aoe.inflate(wide ? 2.5 : 1.5);

        if(user instanceof ServerPlayer serveruser) {
            for(LivingEntity victim : user.level().getEntitiesOfClass(LivingEntity.class, aoe)){
                if(victim instanceof ServerPlayer && !(victim.equals(user)) && !CrystalIronItem.effectNotBlocked(victim, 1))
                    continue; // This staff cannot hurt players other than the user.
                StaffItem.hurtVictim(serveruser, stack, victim, user.damageSources().magic(), ConfigMan.COMMON.soulStaffPower.get().floatValue());
                victim.knockback(0.3, user.level().random.nextDouble()*0.2 - 0.1, user.level().random.nextDouble()*0.2 - 0.1);
            }
            user.level().playSound(null, blockHitPos.x, blockHitPos.y, blockHitPos.z, SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.5F,
                    user.level().random.nextFloat()*0.1f + 0.95f);
        }else{
            ParticleScribe.drawParticleBox(user.level(), ParticleTypes.SOUL, aoe, wide ? 20 : 10);
            user.level().addParticle(ParticleTypes.SOUL, blockHitPos.x, blockHitPos.y, blockHitPos.z, 0, 0, 0);
        }
    }

    public static void missile(Player user, ItemStack stack){
        if (user instanceof ServerPlayer serveruser) {
            AABB aoe = new AABB(user.position().subtract(1, 1, 1), user.position().add(1, 1, 1));
            boolean super_missile = EnchantmentHelper.has(stack, ReactiveComponentTypes.WIDE_RANGE.value());
            int base_range = ConfigMan.COMMON.mindStaffRange.get();
            aoe = aoe.inflate(super_missile ? base_range * 1.67 : base_range);
            List<LivingEntity> nearby_ents = user.level().getEntitiesOfClass(LivingEntity.class, aoe);
            nearby_ents.remove(user);
            Map<LivingEntity, Integer> hit_counts = new HashMap<>();

            for(int i = 0; i < (super_missile ? ConfigMan.COMMON.mindStaffEnchantedMissiles.get() : ConfigMan.COMMON.mindStaffBaseMissiles.get()); i++) {
                if(nearby_ents.isEmpty())
                    break;
                LivingEntity victim = nearby_ents.get(user.level().random.nextInt(0, nearby_ents.size()));
                if(victim.isDeadOrDying())
                    continue;
                if(victim instanceof ArmorStand)
                    continue;
                if(victim instanceof TamableAnimal tamable_victim){
                    if(tamable_victim.getOwner() != null && tamable_victim.getOwner().equals(user)){
                        continue;
                    }
                }
                hit_counts.put(victim, hit_counts.getOrDefault(victim, 0) + 1);
                if(hit_counts.get(victim) >= ConfigMan.COMMON.mindStaffMaxHits.get()) {
                    nearby_ents.remove(victim);
                }
            }

            for(LivingEntity victim : hit_counts.keySet()) {
                for(int i = 0; i < hit_counts.get(victim); i++) {
                    ParticleScribe.drawParticleZigZag(user.level(), ReactiveParticles.SMALL_RUNE, user.getX(), user.getEyeY() - 0.4, user.getZ(),
                            victim.getX(), victim.getEyeY(), victim.getZ(), 2, 5, 0.7);
                    user.level().playSound(null,  victim.getX(), victim.getEyeY(), victim.getZ(), SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.PLAYERS, 0.30F,
                            user.level().random.nextFloat()*0.1f + 0.8f);
                }
                StaffItem.hurtVictim(serveruser, stack, victim, user.damageSources().magic(),
                        hit_counts.get(victim) * ConfigMan.COMMON.mindStaffPower.get().floatValue());
            }
        }
    }

    public static void living(Player user, ItemStack stack){
        AABB aoe = new AABB(user.position().subtract(1, 1, 1), user.position().add(1, 1, 1));
        aoe = aoe.inflate(ConfigMan.COMMON.vitalStaffRange.get());
        if (user.level().random.nextFloat() < 0.4) {
            List<LivingEntity> nearby_ents = user.level().getEntitiesOfClass(LivingEntity.class, aoe);
                for (LivingEntity victim : nearby_ents) {
                boolean has_regen = false, has_hp_up = false;
                for(MobEffectInstance mei : victim.getActiveEffects()){
                    if(mei.getEffect().equals(MobEffects.HEALTH_BOOST)){
                        mei.update(new MobEffectInstance(MobEffects.HEALTH_BOOST, 500, ConfigMan.COMMON.vitalStaffHealthBoost.get() - 1));
                        has_hp_up = true;
                    }
                    else if(mei.getEffect().equals(MobEffects.REGENERATION)){
                        mei.update(new MobEffectInstance(MobEffects.REGENERATION, 50, ConfigMan.COMMON.vitalStaffRegeneration.get() - 1));
                        has_regen = true;
                    }
                }
                if(!has_hp_up)
                    victim.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 500, ConfigMan.COMMON.vitalStaffHealthBoost.get() - 1));
                if(!has_regen)
                    victim.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, ConfigMan.COMMON.vitalStaffRegeneration.get() - 1));
            }
        }

        if(user.level().isClientSide) {
            ParticleScribe.drawParticleBox(user.level(), new EnergyParticle.Options(0.1F, Powers.VITAL_POWER.get().getColor(), user.getEyePosition(), true), aoe.deflate(3.0), 5);
        }
        user.level().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.7F, 1.3f);
    }
}
