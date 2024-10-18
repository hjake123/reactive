package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlazeRodBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape X_SHAPE = Block.box(0, 6, 6, 16, 10, 10);
    protected static final VoxelShape Y_SHAPE = Block.box(6, 0, 6, 10, 16, 10);
    protected static final VoxelShape Z_SHAPE = Block.box(6, 6, 0, 10, 10, 16);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlazeRodBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if(state.getValue(AXIS).isVertical()){
            return Y_SHAPE;
        }
        if(state.getValue(AXIS).equals(Direction.Axis.X)){
            return X_SHAPE;
        }
        return Z_SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if(state.getValue(WATERLOGGED)){
            AABB bubble_box = this.getShape(state, level, pos, CollisionContext.empty()).bounds().move(pos);
            if(!level.getFluidState(pos.above()).is(Fluids.WATER)){
                bubble_box.setMaxY(0.8);
            }
            ParticleScribe.drawParticleBox(level, ParticleTypes.BUBBLE_COLUMN_UP, bubble_box, 2);
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
        return super.getStateForPlacement(context).setValue(WATERLOGGED, fluidstate.is(Fluids.WATER));
    }
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level_access, BlockPos pos, BlockPos pos2) {
        if (state.getValue(WATERLOGGED)) {
            level_access.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level_access));
        }
        return super.updateShape(state, direction, state2, level_access, pos, pos2);
    }
}
