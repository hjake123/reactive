package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class FramedMotionSaltBlock extends Block implements ChainDisplacingBlock{
    public static final BooleanProperty POWERED = ObserverBlock.POWERED;
    public FramedMotionSaltBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState prior, boolean p_60570_) {
        if(level.getBlockState(pos.below()).is(Registration.VOLT_CELL.get())){
            ((ChainDisplacingBlock)Registration.FRAMED_MOTION_SALT_BLOCK.get()).breadthFirstDisplace(level, pos, level.getBestNeighborSignal(pos) > 10);
        }
    }

    @Override
    public void neighborChanged(BlockState our_state, Level level, BlockPos salt_pos, Block block, BlockPos neighbor_pos, boolean unknown) {
        if(level.getBestNeighborSignal(salt_pos) > 10){
            level.setBlock(salt_pos, our_state.setValue(POWERED, true), Block.UPDATE_CLIENTS);
        }else if(our_state.getValue(POWERED)){
            level.setBlock(salt_pos, our_state.setValue(POWERED, false), Block.UPDATE_CLIENTS);
        }
        checkBecomeElectrified(level, salt_pos, our_state, neighbor_pos);
    }


    // These displace themselves instantly.
    private static void checkBecomeElectrified(Level level, BlockPos salt_pos, BlockState our_state, BlockPos neighbor_pos){
        if(level.getBlockState(neighbor_pos).is(Registration.VOLT_CELL.get()) && salt_pos.below().equals(neighbor_pos)){
            ((ChainDisplacingBlock)Registration.FRAMED_MOTION_SALT_BLOCK.get()).breadthFirstDisplace(level, salt_pos, our_state.getValue(POWERED));
        }
    }

    @Override
    public boolean stateMatchesSelf(BlockState state) {
        return state.is(Registration.FRAMED_MOTION_SALT_BLOCK.get());
    }
}
