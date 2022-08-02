package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.SymbolBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SymbolBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private Item symbol_item = Items.BARRIER;

    protected static final VoxelShape UP_SHAPE = Block.box(2, 0, 2, 14, 1, 14);
    protected static final VoxelShape DOWN_SHAPE = Block.box(2, 15, 2, 14, 16, 14);
    protected static final VoxelShape EAST_SHAPE = Block.box(0, 2, 2, 1, 14, 14);
    protected static final VoxelShape WEST_SHAPE = Block.box(15, 2, 2, 16, 14, 14);
    protected static final VoxelShape NORTH_SHAPE = Block.box(2, 2, 15, 14, 14, 16);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(2, 2, 0, 14, 14, 1);

    public static BlockPos getAttachedBlock(BlockState state, BlockPos pos){
        return pos.relative(state.getValue(FACING).getOpposite());
    }

    public SymbolBlock(BlockBehaviour.Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
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
    }

    @javax.annotation.Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor accessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        SymbolBlockEntity symbol = new SymbolBlockEntity(pos, state, symbol_item);
        symbol.setFacing(FACING.getValue("facing").orElse(Direction.UP));
        return symbol;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(type == Registration.SYMBOL_BE_TYPE.get()){
            return (l, p, s, x) -> com.hyperlynx.reactive.be.SymbolBlockEntity.tick(l, p, s, (com.hyperlynx.reactive.be.SymbolBlockEntity) x);
        }
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
