package com.hyperlynx.reactive.integration.pehkui;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.items.CrystalIronItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;

public class ResizeReactionRenders {
    public static CrucibleBlockEntity acid_based(CrucibleBlockEntity crucible) {
        Level level = crucible.getLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleCrucibleTop(crucible.getLevel(), Registration.ACID_BUBBLE_PARTICLE.getType(), crucible.getBlockPos());
        return crucible;
    }

    public static CrucibleBlockEntity verdant_based(CrucibleBlockEntity crucible) {
        Level level = crucible.getLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleCrucibleTop(level, ParticleTypes.HAPPY_VILLAGER, crucible.getBlockPos());
        return crucible;
    }
}
