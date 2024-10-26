package dev.hyperlynx.reactive.util;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

public class OccultSymbolAttractGoal extends MoveToBlockGoal {
    public OccultSymbolAttractGoal(PathfinderMob mob, double d, int i) {
        super(mob, d, 24, i);
    }

    public double acceptedDistance() {
        return 1.14D;
    }

    @Override
    protected boolean isValidTarget(LevelReader reader, BlockPos pos) {
        return reader.getBlockState(pos).is(Registration.OCCULT_SYMBOL.get());
    }


}
