package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionEffects;
import dev.hyperlynx.reactive.be.GatewayBlockEntity;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.items.WarpBottleItem;
import dev.hyperlynx.reactive.mixin.EndGatewayExitViewerMixin;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
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
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if(level instanceof ServerLevel slevel && level.getBlockState(pos.above()).is(Blocks.END_GATEWAY)){
            convertEndGateway(slevel, pos.above());
            level.setBlock(pos, state.setValue(ACTIVE, true), Block.UPDATE_CLIENTS);
        }
        if(state.getValue(ACTIVE) && !level.getBlockState(pos.above()).is(Registration.GATEWAY_BLOCK.get())){
            level.setBlock(pos, state.setValue(ACTIVE, false), Block.UPDATE_CLIENTS);
        }
        super.neighborChanged(state, level, pos, pNeighborBlock, pNeighborPos, pMovedByPiston);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if(level instanceof ServerLevel slevel && level.getBlockState(pos.above()).is(Blocks.END_GATEWAY)){
            convertEndGateway(slevel, pos.above());
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit_result) {
        ItemStack stack = player.getItemInHand(hand);
        if (Powers.WARP_POWER.get().matchesBottle(stack)) {
            if (WarpBottleItem.isRiftBottle(stack)) {
                assert stack.getTag() != null;
                GlobalPos warp_target = WarpBottleItem.getTeleportPosition(stack.getTag());
                if(warp_target == null){
                    player.displayClientMessage(Component.translatable("message.reactive.activate_plinth_failed"), true);
                    return InteractionResult.PASS;
                }
                setGateway(level, pos.above(), warp_target, state);
                level.playSound((Player) null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
                level.playSound((Player) null, pos, SoundEvents.EVOKER_CAST_SPELL, SoundSource.BLOCKS, 0.9F, 0.75F);
                level.playSound((Player) null, pos, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 0.3F, 1F);
                player.setItemInHand(hand, Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
                return InteractionResult.SUCCESS;
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
        return InteractionResult.PASS;
    }

    private static void setGateway(Level level, BlockPos source, GlobalPos destination, BlockState self_state){
        level.setBlock(source.below(), self_state.setValue(ACTIVE, true), Block.UPDATE_CLIENTS);
        level.setBlock(source, Registration.GATEWAY_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS);
        var be = level.getBlockEntity(source);
        if(!(be instanceof GatewayBlockEntity gateway)){
            return;
        }
        gateway.target = destination;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState new_state, boolean moved_by_piston) {
        if(state.getValue(ACTIVE)){
            if(level.getBlockState(pos.above()).is(Registration.GATEWAY_BLOCK.get())){
                level.removeBlock(pos.above(), false);
                for(BlockPos point : ReactionEffects.getCreationPoints(pos)){
                    ParticleScribe.drawParticleZigZag(level, Registration.STARDUST_PARTICLE, pos.above(), point, 10, 7, 0.8);
                }
            }
        }
        super.onRemove(state, level, pos, new_state, moved_by_piston);
    }

    // For old worlds, we need to be able to change existing End Gateways into Reactive Gateway blocks.
    // This method does that. Pass it the position of the gateway.
    private static void convertEndGateway(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if(!(be instanceof TheEndGatewayBlockEntity end_gateway)) {
            ReactiveMod.LOGGER.error("Tried to convert an inconvertible block at {}.", pos);
            return;
        }
        BlockPos target_pos = ((EndGatewayExitViewerMixin) end_gateway).getExitPortal();
        if(target_pos == null){
            ReactiveMod.LOGGER.error("No valid destination for the end gateway at {}.", pos);
            return;
        }
        level.setBlock(pos, Registration.GATEWAY_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS);
        BlockEntity be2 = level.getBlockEntity(pos);
        if(!(be2 instanceof GatewayBlockEntity gateway)) {
            ReactiveMod.LOGGER.error("Something went wrong while converting the gateway at {}.", pos);
            return;
        }
        gateway.target = GlobalPos.of(level.dimension(), target_pos);
    }
}
