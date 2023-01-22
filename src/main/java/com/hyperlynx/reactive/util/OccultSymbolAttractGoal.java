package com.hyperlynx.reactive.util;

import com.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;

public class OccultSymbolAttractGoal extends MoveToBlockGoal {
    public OccultSymbolAttractGoal(PathfinderMob mob, double d, int i) {
        super(mob, d, 24, i);
    }

    public void playDestroyProgressSound(LevelAccessor accessor, BlockPos pos) {
        accessor.playSound((Player)null, pos, SoundEvents.STONE_HIT, SoundSource.HOSTILE, 0.5F, 0.9F + accessor.getRandom().nextFloat() * 0.2F);
    }

    public void playBreakSound(Level level, BlockPos pos) {
        level.playSound((Player)null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
    }

    public double acceptedDistance() {
        return 1.14D;
    }

    @Override
    protected boolean isValidTarget(LevelReader reader, BlockPos pos) {
        return reader.getBlockState(pos).is(Registration.OCCULT_SYMBOL.get());
    }


}
