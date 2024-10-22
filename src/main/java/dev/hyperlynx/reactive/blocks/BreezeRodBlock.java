package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BreezeRodBlock extends Block implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE = Shapes.or(
            Block.box(7, 1, 7, 9, 15, 9),
            Shapes.join(
                    Block.box(5, 7, 5, 11, 8, 11),
                    Block.box(6, 7, 6, 10, 8, 10),
                    BooleanOp.ONLY_FIRST));
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BreezeRodBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if(state.getValue(WATERLOGGED)){
            AABB bubble_box = this.getShape(state, level, pos, CollisionContext.empty()).bounds().move(pos);
            if(!level.getFluidState(pos.above()).is(Fluids.WATER)){
                bubble_box.setMaxY(0.8);
            }
            ParticleScribe.drawParticleBox(level, ParticleTypes.BUBBLE_COLUMN_UP, bubble_box, 1);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.is(Fluids.WATER));
    }
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader reader, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos pos2, BlockState state2, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(reader));
        }
        return super.updateShape(state, reader, ticks, pos, direction, pos2, state2, random);
    }
}
