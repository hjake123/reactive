package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.alchemy.AlchemyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class UnformedMatterBlock extends Block {
    public UnformedMatterBlock(Properties props) {
        super(props);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return 1;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        Block result = BuiltInRegistries.BLOCK.get(AlchemyTags.canBeGenerated).flatMap(tag -> tag.getRandomElement(random)).orElse(Holder.direct(Blocks.AIR)).value(); // THANK YOU FORGE !
        level.setBlock(pos, result.defaultBlockState(), Block.UPDATE_CLIENTS);
        level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
    }

    // Taken from MagmaBlock.java.
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity victim) {
        if (!victim.isSteppingCarefully() && victim instanceof LivingEntity) {
            victim.hurt(level.damageSources().hotFloor(), 1.0F);
        }

        super.stepOn(level, pos, state, victim);
    }
}
