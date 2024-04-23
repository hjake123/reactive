package com.hyperlynx.reactive.integration.pehkui;

import com.hyperlynx.reactive.ConfigMan;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.items.CrystalIronItem;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;
import java.util.Objects;

public class ResizeReactionEffects {
    public static CrucibleBlockEntity shrink(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, (float) ConfigMan.SERVER.pehkuiSmallSize.get().doubleValue(), Mode.REDUCE, Registration.ACID_BUBBLE_PARTICLE);
        return crucible;
    }

    public static CrucibleBlockEntity grow(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, (float) ConfigMan.SERVER.pehkuiLargeSize.get().doubleValue(), Mode.ENLARGE, ParticleTypes.HAPPY_VILLAGER);
        return crucible;
    }

    public static CrucibleBlockEntity revert_from_large(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, 1.0F, Mode.REDUCE, ParticleTypes.ELECTRIC_SPARK);
        return crucible;
    }

    public static CrucibleBlockEntity revert_from_small(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, 1.0F, Mode.ENLARGE, ParticleTypes.ELECTRIC_SPARK);
        return crucible;
    }

    private static void resizeNearby(CrucibleBlockEntity crucible, float new_scale, Mode mode, ParticleOptions particle) {
        if (Objects.requireNonNull(crucible.getLevel()).random.nextFloat() < 0.4) {
            AABB aoe = new AABB(crucible.getBlockPos());
            aoe = aoe.inflate(3);
            List<LivingEntity> victims = crucible.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity victim : victims) {
                if (CrystalIronItem.effectNotBlocked(victim, 2)) {
                    ScaleData victim_scale_data = ScaleTypes.BASE.getScaleData(victim);
                    if(mode == Mode.ENLARGE && victim_scale_data.getScale() < new_scale
                            || mode == Mode.REDUCE && victim_scale_data.getScale() > new_scale){
                        victim_scale_data.setTargetScale(new_scale);
                        ParticleScribe.drawParticleZigZag(crucible.getLevel(), particle,
                                crucible.getBlockPos().getX()+0.5, crucible.getBlockPos().getY()+0.6, crucible.getBlockPos().getZ()+0.5,
                                victim.getEyePosition().x, victim.getEyePosition().y, victim.getEyePosition().z, 20, 5, 0.9);
                        crucible.getLevel().playSound(null, crucible.getBlockPos(), Registration.ZAP_SOUND.get(), SoundSource.BLOCKS);
                        crucible.getLevel().playSound(null, crucible.getBlockPos(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 0.5F, 1.3F + crucible.getLevel().random.nextFloat()*0.2F);
                        victim.hurt(crucible.getLevel().damageSources().magic(), 1);
                        if(victim instanceof ServerPlayer splayer){
                            ReactivePehkuiPlugin.SIZE_CHANGED.trigger(splayer);
                        }
                    }
                }
            }
        }
    }

    enum Mode{
        ENLARGE,
        REDUCE
    }
}
