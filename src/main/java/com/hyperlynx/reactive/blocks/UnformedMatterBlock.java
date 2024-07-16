package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.alchemy.AlchemyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.ForgeRegistries;

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
        Block result = ForgeRegistries.BLOCKS.tags().getTag(AlchemyTags.canBeGenerated).getRandomElement(random).orElse(Blocks.AIR); // THANK YOU FORGE !
        level.setBlock(pos, result.defaultBlockState(), Block.UPDATE_CLIENTS);
        level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
    }

    // Taken from MagmaBlock.java.
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity victim) {
        if (!victim.isSteppingCarefully() && victim instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)victim)) {
            victim.hurt(level.damageSources().hotFloor(), 1.0F);
        }

        super.stepOn(level, pos, state, victim);
    }
}
