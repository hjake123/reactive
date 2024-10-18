package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.be.GravityBeamBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class GravityBeamBlock extends DirectionalBlock implements EntityBlock {
    public static final MapCodec<GravityBeamBlock> CODEC = simpleCodec(GravityBeamBlock::new);
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public GravityBeamBlock(Properties props) {
        super(props);
        registerDefaultState(this.defaultBlockState().setValue(ENABLED, false));
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(ENABLED);
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighbor_position, boolean unknown) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(ENABLED, level.hasNeighborSignal(pos)), Block.UPDATE_CLIENTS);
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(ENABLED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GravityBeamBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <GravityBeamBlockEntity extends BlockEntity> BlockEntityTicker<GravityBeamBlockEntity> getTicker(Level level, BlockState state, BlockEntityType<GravityBeamBlockEntity> t) {
        if(t == Registration.GRAVITY_BEAM_BE_TYPE.get()){
            return (l, p, s, a) -> dev.hyperlynx.reactive.be.GravityBeamBlockEntity.tick(l, p, s);
        }
        return null;
    }

}
