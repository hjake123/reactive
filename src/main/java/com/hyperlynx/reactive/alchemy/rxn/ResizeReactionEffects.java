package com.hyperlynx.reactive.integration.pehkui;

import com.hyperlynx.reactive.ConfigMan;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.items.CrystalIronItem;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;

public class ResizeReactionEffects {
    public static CrucibleBlockEntity shrink(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, ConfigMan.SERVER.shrinkSmallSize.get(), ConfigMan.SERVER.shrinkSmallStep.get(), Mode.REDUCE, Registration.ACID_BUBBLE_PARTICLE);
        return crucible;
    }

    public static CrucibleBlockEntity grow(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, ConfigMan.SERVER.growLargeSize.get(), ConfigMan.SERVER.growLargeStep.get(), Mode.ENLARGE, ParticleTypes.HAPPY_VILLAGER);
        return crucible;
    }

    public static CrucibleBlockEntity revert_from_large(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, 1.0, 0.6, Mode.REDUCE, ParticleTypes.ELECTRIC_SPARK);
        return crucible;
    }

    public static CrucibleBlockEntity revert_from_small(CrucibleBlockEntity crucible) {
        resizeNearby(crucible, 1.0, 0.6, Mode.ENLARGE, ParticleTypes.ELECTRIC_SPARK);
        return crucible;
    }

    private static void resizeNearby(CrucibleBlockEntity crucible, double new_scale, double new_step_height, Mode mode, ParticleOptions particle) {
        if (Objects.requireNonNull(crucible.getLevel()).random.nextFloat() < 0.4) {
            AABB aoe = new AABB(crucible.getBlockPos());
            aoe = aoe.inflate(3);
            List<LivingEntity> victims = crucible.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity victim : victims) {
                if (CrystalIronItem.effectNotBlocked(victim, 2)) {
                    double current_scale = victim.getAttributeValue(Attributes.SCALE);
                    if(mode == Mode.ENLARGE && current_scale < new_scale
                            || mode == Mode.REDUCE && current_scale > new_scale){
                        Objects.requireNonNull(victim.getAttribute(Attributes.SCALE)).setBaseValue(new_scale);
                        Objects.requireNonNull(victim.getAttribute(Attributes.STEP_HEIGHT)).setBaseValue(new_step_height);
                        ParticleScribe.drawParticleZigZag(crucible.getLevel(), particle,
                                crucible.getBlockPos().getX()+0.5, crucible.getBlockPos().getY()+0.6, crucible.getBlockPos().getZ()+0.5,
                                victim.getEyePosition().x, victim.getEyePosition().y, victim.getEyePosition().z, 20, 5, 0.9);
                        crucible.getLevel().playSound(null, crucible.getBlockPos(), Registration.ZAP_SOUND.get(), SoundSource.BLOCKS);
                        crucible.getLevel().playSound(null, crucible.getBlockPos(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 0.5F, 1.3F + crucible.getLevel().random.nextFloat()*0.2F);
                        victim.hurt(crucible.getLevel().damageSources().magic(), 1);
                        if(victim instanceof ServerPlayer splayer){
                            if (new_scale == 1.0) {
                                CriteriaTriggers.SIZE_REVERTED_TRIGGER.get().trigger(splayer);
                            } else {
                                CriteriaTriggers.SIZE_CHANGED_TRIGGER.get().trigger(splayer);
                            }
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
