package dev.hyperlynx.reactive.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NoduleBlock extends DirectionalBlock {
    private final boolean small;
    private static final VoxelShape SMALL_FLOOR_SHAPE = Block.box(6, 0, 6, 10, 3, 10);
    private static final VoxelShape SMALL_CEILING_SHAPE = Block.box(6, 13, 6, 10, 16, 10);
    private static final VoxelShape SMALL_WEST_SHAPE = Block.box(0, 6, 6, 3, 10, 10);
    private static final VoxelShape SMALL_EAST_SHAPE = Block.box(13, 6, 6, 16, 10, 10);
    private static final VoxelShape SMALL_NORTH_SHAPE = Block.box(6, 6, 0, 10, 10, 3);
    private static final VoxelShape SMALL_SOUTH_SHAPE = Block.box(6, 6, 13, 10, 10, 16);

    private static final VoxelShape FLOOR_SHAPE = Block.box(4, 0, 4, 12, 6, 12);
    private static final VoxelShape CEILING_SHAPE = Block.box(4, 10, 4, 12, 16, 12);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 4, 4, 6, 12, 12);
    private static final VoxelShape EAST_SHAPE = Block.box(10, 4, 4, 16, 12, 12);
    private static final VoxelShape NORTH_SHAPE = Block.box(4, 4, 0,12, 12, 6);
    private static final VoxelShape SOUTH_SHAPE = Block.box(4, 4, 10,12, 12, 16);

    public NoduleBlock(Properties properties, boolean small_hitbox) {
        super(properties);
        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.DOWN));
        small = small_hitbox;
    }

    private NoduleBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.DOWN));
        small = false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    private boolean isSmall() {
        return small;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch(state.getValue(FACING)) {
            case UP -> {
                return isSmall() ? SMALL_CEILING_SHAPE : CEILING_SHAPE;
            }
            case WEST -> {
                return isSmall() ? SMALL_WEST_SHAPE : WEST_SHAPE;
            }
            case EAST -> {
                return isSmall() ? SMALL_EAST_SHAPE : EAST_SHAPE;
            }
            case NORTH -> {
                return isSmall() ? SMALL_NORTH_SHAPE : NORTH_SHAPE;
            }
            case SOUTH -> {
                return isSmall() ? SMALL_SOUTH_SHAPE : SOUTH_SHAPE;
            }
            default -> {
                return isSmall() ? SMALL_FLOOR_SHAPE : FLOOR_SHAPE;
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }
}
