package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.AlchemyTags;
import com.hyperlynx.reactive.util.HarvestChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class MotionSaltBlock extends Block {
    public static final BooleanProperty POWERED = ObserverBlock.POWERED;
    public MotionSaltBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public void onPlace(BlockState p_60566_, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        causeNeighborToFall(level, pos.below());
        causeNeighborToFall(level, pos.offset(1, 0, 0));
        causeNeighborToFall(level, pos.offset(-1, 0, 0));
        causeNeighborToFall(level, pos.offset(0, 0, 1));
        causeNeighborToFall(level, pos.offset(0, 0, -1));
        checkBecomeElectrified(level, pos, pos.below());
    }

    @Override
    public void neighborChanged(BlockState our_state, Level level, BlockPos salt_pos, Block block, BlockPos neighbor_pos, boolean unknown) {
        if(level.getDirectSignalTo(salt_pos) > 10){
            level.setBlock(salt_pos, our_state.setValue(POWERED, true), Block.UPDATE_CLIENTS);
        }else if(our_state.getValue(POWERED)){
            level.setBlock(salt_pos, our_state.setValue(POWERED, false), Block.UPDATE_CLIENTS);
        }
        causeNeighborToFall(level, neighbor_pos);
        checkBecomeElectrified(level, salt_pos, neighbor_pos);
    }

    // Displace the block shortly after it is powered.
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
        DisplacedBlock.displace(state, pos, level, 200);
        if(state.getValue(POWERED)){
            // Also displace the block above this one.
            DisplacedBlock.displaceWithChain(level.getBlockState(pos.above()), pos.above(), level, 210, 0, pos);
        }
    }

    private static void causeNeighborToFall(Level level, BlockPos neighbor_pos) {
        if(!level.getBlockState(neighbor_pos.below()).isAir())
            return;

        if(HarvestChecker.canMineBlock(level, neighbor_pos, level.getBlockState(neighbor_pos), 35F)
        && !level.getBlockState(neighbor_pos).is(AlchemyTags.notRelocatable)){
            FallingBlockEntity.fall(level, neighbor_pos, level.getBlockState(neighbor_pos));
        }
    }

    private static void checkBecomeElectrified(Level level, BlockPos salt_pos, BlockPos neighbor_pos){
        if(level.getBlockState(neighbor_pos).is(Registration.VOLT_CELL.get()) && salt_pos.below().equals(neighbor_pos)){
            level.scheduleTick(salt_pos, Registration.MOTION_SALT_BLOCK.get(), 10);
        }
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 100;
    }

}
