package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.be.GatewayBlockEntity;
import dev.hyperlynx.reactive.items.WarpBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GatewayBlock extends Block implements Portal, EntityBlock {
    private final VoxelShape SHAPE = Block.box(5, 5, 5, 11, 11, 11);

    public GatewayBlock(Properties properties) {
        super(properties);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == Registration.GATEWAY_BE.get() ? GatewayBlockEntity::tick : null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new GatewayBlockEntity(pos, state);
    }

    @Override
    public int getPortalTransitionTime(@NotNull ServerLevel level, @NotNull Entity entity) {
        return Portal.super.getPortalTransitionTime(level, entity);
    }

    @Override
    public @Nullable DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if(be instanceof GatewayBlockEntity gateway && gateway.target != null) {
            ServerLevel target_level = level.getServer().getLevel(gateway.target.dimension());
            if(target_level == null){
                ReactiveMod.LOGGER.error("Invalid destination dimension for gateway!");
                return null;
            }
            return new DimensionTransition(target_level, Vec3.atCenterOf(gateway.target.pos()), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), DimensionTransition.DO_NOTHING);
        }
        ReactiveMod.LOGGER.error("No destination set for gateway!");
        return null;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (!ConfigMan.COMMON.doNotTeleport.get().contains(entity.getEncodeId())) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public Transition getLocalTransition() {
        return Portal.super.getLocalTransition();
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        // NO-OP
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!(level.getBlockEntity(pos) instanceof GatewayBlockEntity gateway)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if(stack.is(Registration.WARP_BOTTLE.get()) && !WarpBottleItem.isRiftBottle(stack)) {
            stack.shrink(1);
            ItemStack rift_bottle = Registration.WARP_BOTTLE.get().getDefaultInstance();
            WarpBottleItem.setTeleportTarget(rift_bottle, gateway.target);
            player.addItem(rift_bottle);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
