package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.MnemonicBlockEntity;
import com.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MnemonicBlock extends Block implements EntityBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public MnemonicBlock(Properties props) {
        super(props);
        registerDefaultState(this.defaultBlockState().setValue(POWER, 0).setValue(CHARGED, false).setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
        builder.add(CHARGED);
        builder.add(ACTIVE);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if(context.getLevel().getBlockState(context.getClickedPos().below()).is(Registration.VOLT_CELL.get())){
            return defaultBlockState().setValue(CHARGED, true);
        }
        return defaultBlockState();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MnemonicBlockEntity m) {
            if (!level.isClientSide && !(player.isCreative() && !m.hasMemory())) {
                m.stopRecording();
                ItemStack stack = Registration.MNEMONIC_BULB_ITEM.get().getDefaultInstance();
                m.saveToItem(stack, level.registryAccess());
                ItemEntity drop = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, stack);
                drop.setDefaultPickUpDelay();
                level.addFreshEntity(drop);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

        @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MnemonicBlockEntity(pos, state);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction direction) {
        return state.getValue(POWER);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor_block, BlockPos neighbor_pos, boolean moved_by_piston) {
        super.neighborChanged(state, level, pos, neighbor_block, neighbor_pos, moved_by_piston);
        if(level.getBlockState(pos.below()).is(Registration.VOLT_CELL.get()) && !state.getValue(CHARGED)){
            level.setBlock(pos, state.setValue(CHARGED, true), Block.UPDATE_CLIENTS);
        } else if(!(level.getBlockState(pos.below()).is(Registration.VOLT_CELL.get())) && state.getValue(CHARGED)){
            level.setBlock(pos, state.setValue(CHARGED, false), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource source) {
        super.animateTick(state, level, pos, source);
        if(state.getValue(CHARGED)){
            ParticleScribe.drawParticleBox(level, ParticleTypes.ELECTRIC_SPARK, AABB.ofSize(Vec3.atBottomCenterOf(pos).add(0, 1, 0), 1.2, 0.4, 1.2), 1);
        }
    }

    @Nullable
    @Override
    public <MnemonicBlockEntity extends BlockEntity> BlockEntityTicker<MnemonicBlockEntity> getTicker(Level level, BlockState state, BlockEntityType<MnemonicBlockEntity> type) {
        if(type == Registration.MNEMONIC_BULB_BE_TYPE.get() && !level.isClientSide){
            return (l, p, s, b) -> com.hyperlynx.reactive.be.MnemonicBlockEntity.tick(l, p, s, (com.hyperlynx.reactive.be.MnemonicBlockEntity) b);
        }
        return null;
    }
}
