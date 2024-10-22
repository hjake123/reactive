package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.be.SymbolBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SymbolBlock extends WaterloggableBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private Item symbol_item = Items.BARRIER;

    protected static final VoxelShape UP_SHAPE = box(2, 0, 2, 14, 1, 14);
    protected static final VoxelShape DOWN_SHAPE = box(2, 15, 2, 14, 16, 14);
    protected static final VoxelShape EAST_SHAPE = box(0, 2, 2, 1, 14, 14);
    protected static final VoxelShape WEST_SHAPE = box(15, 2, 2, 16, 14, 14);
    protected static final VoxelShape NORTH_SHAPE = box(2, 2, 15, 14, 14, 16);
    protected static final VoxelShape SOUTH_SHAPE = box(2, 2, 0, 14, 14, 1);

    public SymbolBlock(BlockBehaviour.Properties props) {
        super(props.pushReaction(PushReaction.DESTROY));
        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (state.getValue(FACING).equals(Direction.UP))
            return UP_SHAPE;
        else if(state.getValue(FACING).equals(Direction.DOWN))
            return DOWN_SHAPE;
        else if(state.getValue(FACING).equals(Direction.EAST))
            return EAST_SHAPE;
        else if(state.getValue(FACING).equals(Direction.WEST))
            return WEST_SHAPE;
        else if(state.getValue(FACING).equals(Direction.NORTH))
            return NORTH_SHAPE;
        else if(state.getValue(FACING).equals(Direction.SOUTH))
            return SOUTH_SHAPE;

        System.err.println("Missing block state information for a symbol block at " + pos + "?!!");
        return UP_SHAPE;
    }

    public void setSymbolItem(Item item){
        symbol_item = item;
    }

    @Override
    public @NotNull Item asItem() {
        return symbol_item;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @javax.annotation.Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        SymbolBlockEntity symbol = new SymbolBlockEntity(pos, state, symbol_item);
        symbol.setFacing(FACING.getValue("facing").orElse(Direction.UP));
        return symbol;
    }

//    @Nullable
//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
//        return null;
//    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
