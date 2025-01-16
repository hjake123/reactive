package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.be.GatewayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
                entity.teleportTo(target_level, destination.pos().getX(), destination.pos().getY(), destination.pos().getZ(),
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
}
