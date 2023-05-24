package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.GravityChandelierBlockEntity;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GravityChandelierBlock extends Block implements EntityBlock {
    public GravityChandelierBlock(Properties props) {
        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GravityChandelierBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <GravityChandelierBlockEntity extends BlockEntity> BlockEntityTicker<GravityChandelierBlockEntity> getTicker(Level level, BlockState state, BlockEntityType<GravityChandelierBlockEntity> t) {
        if(t == Registration.GRAVITY_CHANDELIER_BE_TYPE.get()){
            return (l, p, s, a) -> com.hyperlynx.reactive.be.GravityChandelierBlockEntity.tick(l, p);
        }
        return null;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        ParticleScribe.drawParticleSphere(level, ParticleTypes.END_ROD, pos, 0,
                GravityChandelierBlockEntity.RANGE, 30);
        level.addParticle(ParticleTypes.END_ROD, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 0, 0, 0);
    }
}
