package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.GravityChandelierBlockEntity;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import static com.hyperlynx.reactive.be.GravityChandelierBlockEntity.RANGE;

public class GravityChandelierBlock extends Block implements EntityBlock {
    public static final BooleanProperty OFFSET = BooleanProperty.create("offset");

    public GravityChandelierBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(OFFSET, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        level.setBlock(pos, state.setValue(OFFSET, !state.getValue(OFFSET)), Block.UPDATE_CLIENTS);
        level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1.0F, 0.72F);
        return InteractionResult.SUCCESS;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OFFSET);
        super.createBlockStateDefinition(builder);
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
        BlockPos center = state.getValue(OFFSET) ? pos.above((int) RANGE) : pos;
        ParticleScribe.drawParticleSphere(level, ParticleTypes.END_ROD, center, 0,
                RANGE, 30);
        level.addParticle(ParticleTypes.END_ROD, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5,
                0, 0, 0);
    }
}
