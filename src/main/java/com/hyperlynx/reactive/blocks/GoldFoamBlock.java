package com.hyperlynx.reactive.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

// A block of Gold Foam. Gold Foam breaks fall damage, lessens jump height and increases movement speed.
// Also, it can be "active", in which state it "grows" kinda like a Chorus Flower.
// It is not flammable, but it does disappear on contact with fluids.
public class GoldFoamBlock extends Block{
    public GoldFoamBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public void fallOn(Level level, BlockState state, BlockPos pos, Entity guy_who_falls_down, float f) {
        guy_who_falls_down.causeFallDamage(f, 0.1F, DamageSource.FALL);
    }
}
