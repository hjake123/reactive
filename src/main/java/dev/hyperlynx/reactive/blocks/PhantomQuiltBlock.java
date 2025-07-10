package dev.hyperlynx.reactive.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PhantomQuiltBlock extends Block {
    public static final BooleanProperty WEIGHED = BooleanProperty.create("weighed");

    protected static final VoxelShape SHAPE = Block.box(1, 3, 1, 15, 4, 15);
    protected static final VoxelShape SHAPE_LOW = Block.box(1, 2, 1, 15, 3, 15);
    protected static final VoxelShape TOUCH_SHAPE = Block.box(1, 3, 1, 15, 5, 15);

    public PhantomQuiltBlock(Properties prop) {
        super(prop);
        registerDefaultState(stateDefinition.any().setValue(WEIGHED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WEIGHED);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(WEIGHED) ? SHAPE_LOW : SHAPE;
    }

    @Override
    public void fallOn(@NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull Entity p_153365_, float p_153366_) {
        // Don't cause fall damage! ;)
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if(!level.isClientSide() && !state.getValue(WEIGHED)) {
            level.setBlockAndUpdate(pos, state.setValue(WEIGHED, shouldBeWeighed(state, level, pos)));
            level.scheduleTick(new BlockPos(pos), this, 10); // Schedule a tick to test if we should remain weighed down.
        }
    }

    @Override
    public void tick(BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        level.setBlockAndUpdate(pos, state.setValue(WEIGHED, shouldBeWeighed(state, level, pos)));
        if(state.getValue(WEIGHED)){
            level.scheduleTick(pos, this, 10);
        }
    }

    private boolean shouldBeWeighed(@NotNull BlockState state, Level level, @NotNull BlockPos pos){
        return !level.getEntities(null, TOUCH_SHAPE.bounds().move(pos)).isEmpty();
    }

}
