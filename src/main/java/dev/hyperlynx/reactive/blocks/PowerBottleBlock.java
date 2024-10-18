package dev.hyperlynx.reactive.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PowerBottleBlock extends WaterloggableBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final IntegerProperty BOTTLES = IntegerProperty.create("bottles", 1, 3);
    protected static final VoxelShape ONE_BOTTLE_BOUNDS = box(6, 0, 6, 10, 8, 10);

    public PowerBottleBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.X).setValue(BOTTLES, 1));
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
        builder.add(BOTTLES);
        super.createBlockStateDefinition(builder);
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(AXIS, context.getHorizontalDirection().getAxis());
    }
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        int bottles = state.getValue(BOTTLES);
        if(bottles == 1){
            return box(6, 0, 6, 10, 8, 10);
        }

        if(state.getValue(AXIS).equals(Direction.Axis.X)){
            if(bottles == 2){
                return box(6, 0, 3, 10, 8, 13);
            }else{
                return box(6, 0, 1, 10, 8, 15);
            }
        }else{
            if(bottles == 2){
                return box(3, 0, 6, 13, 8, 10);
            }else{
                return box(1, 0, 6, 15, 8, 10);
            }
        }
    }
}
