package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.blocks.AirLightBlock;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.util.BeamHelper;
import dev.hyperlynx.reactive.ConfigMan;
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
import java.util.List;

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
        int range = 64;
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
                        StaffItem.hurtVictim(serveruser, stack, victim, user.damageSources().inFire(), 7);
                    }
                    victim.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0));
                }
            }
            if(!block_hit.getType().equals(BlockHitResult.Type.MISS)) {
                BlockPos light_target = block_hit_pos.relative(block_hit.getDirection(), 1);
                if (user.level().getBlockState(light_target).isAir() && !user.level().getBlockState(light_target).is(Registration.GLOWING_AIR.get())) {
                    user.level().setBlock(light_target,
                            Registration.GLOWING_AIR.get().defaultBlockState().setValue(AirLightBlock.DECAYING, !ConfigMan.COMMON.lightStaffLightsPermanent.get()),
                            Block.UPDATE_ALL_IMMEDIATE);
                } else if (user.level().getBlockState(light_target).is(Blocks.WATER)) {
                    user.level().setBlock(light_target,
                            Registration.GLOWING_AIR.get().defaultBlockState()
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
                    .add(user.level().random.nextDouble()*2-1, user.level().random.nextDouble()*2-1, user.level().random.nextDouble()*2-1);
            var aim = target.subtract(fireball_position).normalize().scale(0.1);
            SmallFireball fireball = new SmallFireball(user.level(), fireball_position.x, fireball_position.y, fireball_position.z, aim);
            user.level().addFreshEntity(fireball);
            user.level().playSound(null, fireball_position.x, fireball_position.y, fireball_position.z, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.25F, 1.0F);
        }
    }

    public static void spectral(Player user, ItemStack stack){
        var blockHit = BeamHelper.playerRayTrace(user.level(), user, ClipContext.Fluid.NONE, ClipContext.Block.COLLIDER, 16);
        var blockHitPos = blockHit.getLocation();

        AABB aoe = new AABB(blockHitPos.subtract(1, 1, 1), blockHitPos.add(1, 1, 1));
        boolean wide = EnchantmentHelper.has(stack, Registration.WIDE_RANGE.value());
        aoe = aoe.inflate(wide ? 2.5 : 1.5);

        if(user instanceof ServerPlayer serveruser) {
            for(LivingEntity victim : user.level().getEntitiesOfClass(LivingEntity.class, aoe)){
                if(victim instanceof ServerPlayer && !(victim.equals(user)) && !CrystalIronItem.effectNotBlocked(victim, 1))
                    continue; // This staff cannot hurt players other than the user.
                StaffItem.hurtVictim(serveruser, stack, victim, user.damageSources().magic(), 3);
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
            boolean super_missile = EnchantmentHelper.has(stack, Registration.WIDE_RANGE.value());
            aoe = aoe.inflate(super_missile ? 10 : 6);
            List<LivingEntity> nearby_ents = user.level().getEntitiesOfClass(LivingEntity.class, aoe);
            nearby_ents.remove(user);
            for(int i = 0; i < (super_missile ? 7 : 3); i++) {
                if(nearby_ents.isEmpty())
                    break;
                LivingEntity victim = nearby_ents.get(user.level().random.nextInt(0, nearby_ents.size()));
                if(victim instanceof ArmorStand)
                    continue;
                if(victim instanceof TamableAnimal tamable_victim){
                    if(tamable_victim.getOwner() != null && tamable_victim.getOwner().equals(user)){
                        continue;
                    }
                }
                StaffItem.hurtVictim(serveruser, stack, victim, user.damageSources().magic(), 2);
                ParticleScribe.drawParticleZigZag(user.level(), Registration.SMALL_RUNE_PARTICLE, user.getX(), user.getEyeY() - 0.4, user.getZ(),
                        victim.getX(), victim.getEyeY(), victim.getZ(), 2, 5, 0.7);
                user.level().playSound(null,  victim.getX(), victim.getEyeY(), victim.getZ(), SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.PLAYERS, 0.30F,
                        user.level().random.nextFloat()*0.1f + 0.8f);
            }
        }
    }

    public static void living(Player user, ItemStack stack){
        if (user.level().random.nextFloat() < 0.4) {
            AABB aoe = new AABB(user.position().subtract(1, 1, 1), user.position().add(1, 1, 1));
            aoe = aoe.inflate(5);
            List<LivingEntity> nearby_ents = user.level().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity victim : nearby_ents) {
                boolean has_regen = false, has_hp_up = false;
                for(MobEffectInstance mei : victim.getActiveEffects()){
                    if(mei.getEffect().equals(MobEffects.HEALTH_BOOST)){
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
            user.level().addParticle(ParticleTypes.CRIMSON_SPORE, user.getRandomX(5.0), user.getY(),
                    user.getRandomZ(5.0), 0, 0, 0);
        }
        user.level().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.PLAYERS, 1F, 1f);

    }

}
