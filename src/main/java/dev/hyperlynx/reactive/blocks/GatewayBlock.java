package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.be.GatewayBlockEntity;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.items.WarpBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class GatewayBlock extends Block implements EntityBlock {
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

    public @Nullable static GlobalPos getDestination(GatewayBlockEntity gateway, ServerLevel level) {
        if(gateway.target != null) {
            ServerLevel target_level = level.getServer().getLevel(gateway.target.dimension());
            if(target_level == null){
                ReactiveMod.LOGGER.error("Invalid destination dimension for gateway!");
                return null;
            }
            return gateway.target;
        }
        ReactiveMod.LOGGER.error("No destination set for gateway!");
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if(!(level instanceof ServerLevel slevel)){
            return;
        }
        if(!(level.getBlockEntity(pos) instanceof GatewayBlockEntity gateway)) {
            ReactiveMod.LOGGER.error("Invalid gateway block entity at {}!", pos);
            return;
        }
        if (!ConfigMan.COMMON.doNotTeleport.get().contains(entity.getEncodeId())) {
            if(!gateway.isOnCooldown()) {
                GlobalPos destination = getDestination(gateway, slevel);
                if(destination == null){
                    ReactiveMod.LOGGER.error("No valid destination for the gateway at {}!", pos);
                    return;
                }
                ServerLevel target_level = level.getServer().getLevel(destination.dimension());
                if(target_level == null){
                    ReactiveMod.LOGGER.error("No valid dimension for the gateway at {}!", pos);
                    return;
                }
                Vec3 target_vector = Vec3.atCenterOf(destination.pos());
                entity.teleportTo(target_level, target_vector.x(), target_vector.y(), target_vector.z(),
                        Set.of(), entity.getYRot(), entity.getXRot());
                gateway.setCooldown(20);
            }
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        // NO-OP
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if(stack.is(Registration.WARP_BOTTLE.get()) && !WarpBottleItem.isRiftBottle(stack)) {
            stack.shrink(1);
            ItemStack rift_bottle = Registration.WARP_BOTTLE.get().getDefaultInstance();
            WarpBottleItem.makeRiftBottle(rift_bottle, level.dimension(), player.blockPosition());
            player.addItem(rift_bottle);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 0.5F);
            ParticleScribe.drawExactParticleRing(level, ParticleTypes.PORTAL, player.position(), 0.0F, 0.5F, 10);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

}
