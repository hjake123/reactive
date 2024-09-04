package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.items.WarpBottleItem;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class GatewayPlinthBlock extends Block {
    private final VoxelShape SHAPE = Shapes.or(
            Block.box(1, 0, 1, 15, 2, 15),
            Block.box(0, 7, 0, 16, 9, 16),
            Block.box(4, 2, 4, 12, 7, 12));
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public GatewayPlinthBlock(Properties props) {
        super(props);
        registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if(state.getValue(ACTIVE) && !level.getBlockState(pos.above()).is(Blocks.END_GATEWAY)){
            level.setBlock(pos, state.setValue(ACTIVE, false), Block.UPDATE_CLIENTS);
        }
        super.neighborChanged(state, level, pos, pNeighborBlock, pNeighborPos, pMovedByPiston);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (Powers.WARP_POWER.get().matchesBottle(stack)) {
            if (WarpBottleItem.isRiftBottle(stack)) {
                GlobalPos warp_target = WarpBottleItem.getTeleportPosition(stack);
                if(warp_target == null){
                    player.displayClientMessage(Component.translatable("message.reactive.activate_plinth_failed"), true);
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                if(level.dimension().equals(warp_target.dimension())){
                    setGateway(level, pos.above(), warp_target.pos(), state);
                    player.setItemInHand(hand, Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
                    return ItemInteractionResult.SUCCESS;
                }
                player.displayClientMessage(Component.translatable("message.reactive.donate_warp_failed"), true);
                return ItemInteractionResult.FAIL;
            }

            if (player instanceof ServerPlayer splayer) {
                ResourceLocation warp_research = ReactiveMod.location("be_teleported");
                if (splayer.getAdvancements().getOrStartProgress(Advancement.Builder.advancement().build(warp_research)).isDone()) {
                    player.displayClientMessage(Component.translatable("message.reactive.reject_warp_knowledgeable"), true);
                } else {
                    player.displayClientMessage(Component.translatable("message.reactive.reject_warp_naive"), true);
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static void setGateway(Level level, BlockPos source, BlockPos destination, BlockState self_state){
        level.setBlock(source.below(), self_state.setValue(ACTIVE, true), Block.UPDATE_CLIENTS);
        level.setBlock(source, Blocks.END_GATEWAY.defaultBlockState(), Block.UPDATE_CLIENTS);
        var be = level.getBlockEntity(source);
        if(!(be instanceof TheEndGatewayBlockEntity gateway)){
            return;
        }
        gateway.setExitPosition(destination, true);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState new_state, boolean moved_by_piston) {
        if(state.getValue(ACTIVE)){
            if(level.getBlockState(pos.above()).is(Blocks.END_GATEWAY)){
                level.removeBlock(pos.above(), false);
            }
        }
        super.onRemove(state, level, pos, new_state, moved_by_piston);
    }
}
