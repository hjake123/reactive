package com.hyperlynx.reactive.fx;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.SpecialCaseMan;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.fx.ParticleScribe;
import com.hyperlynx.reactive.items.CrystalIronItem;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.FlagCriterion;
import com.hyperlynx.reactive.util.HarvestChecker;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;
import java.util.Random;

// Just a holder class for the various reaction render methods. Please only call these on the client thank you.
public class ReactionRenders {
    // Changes the Gold Symbol into Active Gold Foam, which spreads outwards for a limited distance and leaves Gold Foam behind.
    public static CrucibleBlockEntity foaming(CrucibleBlockEntity c) {
        BlockPos symbol_position = c.areaMemory.fetch(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get());
        ParticleScribe.drawParticleZigZag(c.getLevel(), ParticleTypes.EFFECT,
                c.getBlockPos().getX() + 0.5F, c.getBlockPos().getY() + 0.5625F, c.getBlockPos().getZ() + 0.5F,
                symbol_position.getX()+0.5, symbol_position.getY()+0.5, symbol_position.getZ()+0.5, 12, 7,0.4);
        return c;
    }

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
