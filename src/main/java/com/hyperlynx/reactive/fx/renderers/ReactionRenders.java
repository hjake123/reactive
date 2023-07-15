package com.hyperlynx.reactive.fx.renderers;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.util.ConfigMan;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;

// Just a holder class for the various reaction render methods. Please only call these on the client thank you.
public class ReactionRenders {
    // Zap the gold symbol!
//    public static CrucibleBlockEntity foaming(CrucibleBlockEntity c) {
//        BlockPos symbol_position = c.areaMemory.fetch(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get());
//        if(symbol_position == null)
//            return c;
//        ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.EFFECT,
//                c.getBlockPos().getX() + 0.5F, c.getBlockPos().getY() + 0.5625F, c.getBlockPos().getZ() + 0.5F,
//                symbol_position.getX()+0.5, symbol_position.getY()+0.5, symbol_position.getZ()+0.5, 12, 7,0.4);
//        return c;
//    }

    public static CrucibleBlockEntity smoke(CrucibleBlockEntity c) {
        ParticleScribe.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.LARGE_SMOKE, c.getBlockPos(), 0.3F);
        return c;
    }

    // Causes nearby bonemeal-ables to be fertilized occasionally.
    public static CrucibleBlockEntity growth(CrucibleBlockEntity c) {
        ParticleScribe.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.HAPPY_VILLAGER, c.getBlockPos(), 0.1F);
        return c;
    }

    // Shoot flames from the crucible!
    public static CrucibleBlockEntity flamethrower(CrucibleBlockEntity c) {
        if(c.getLevel() == null) return c;

        if(c.getPowerLevel(Powers.SOUL_POWER.get()) > 20){
            ParticleScribe.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.SOUL_FIRE_FLAME, c.getBlockPos(), 0.1F, 0, 0.1, 0);
        }else{
            ParticleScribe.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.FLAME, c.getBlockPos(), 0.1F, 0, 0.1, 0);
        }

        return c;
    }
}
