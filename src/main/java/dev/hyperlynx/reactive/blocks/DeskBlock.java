package dev.hyperlynx.reactive.blocks;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.reactive.be.DeskBlockEntity;
import dev.hyperlynx.reactive.menu.DeskMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class DeskBlock extends HorizontalDirectionalBlock implements EntityBlock {
    // Copied from the model. Can't think of a better option...
    protected static final VoxelShape BASE_SHAPE = Shapes.or(
            Block.box(0, 6, 0, 16, 13, 16),
            Block.box(0, 0, 0, 4, 6,4),
            Block.box(0, 0, 12, 4, 6, 16),
            Block.box(12, 0, 0, 16, 6, 4),
            Block.box(12, 0, 12, 16, 6, 16),
            Block.box(4, 3, 2, 12, 5, 14),
            Block.box(12, 3, 4, 14, 5, 12),
            Block.box(2, 3, 4, 4, 5, 12)
    );
    protected static final VoxelShape X_BOOK = Shapes.or(
            Block.box(3, 13, 2, 13, 15, 7),
            Block.box(3, 13, 9, 13, 15, 14),
            Block.box(3, 13, 7, 13, 14, 9)
    );
    protected static final VoxelShape Z_BOOK = Shapes.or(
            Block.box(2, 13, 3, 7, 15, 13),
            Block.box(9, 13, 3, 14, 15, 13),
            Block.box(7, 13, 3, 9, 14, 13)

    );
    protected static final VoxelShape X_SHAPE = Shapes.or(
            BASE_SHAPE, X_BOOK
    );
    protected static final VoxelShape Z_SHAPE = Shapes.or(
            BASE_SHAPE, Z_BOOK
    );

    public DeskBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(state.getValue(FACING).getAxis().equals(Direction.Axis.X)) {
            return X_SHAPE;
        }
        return Z_SHAPE;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if(handler != null) {
            return new SimpleMenuProvider(
                    ((container_id, player_inventory, player) ->
                            new DeskMenu(container_id, player_inventory, handler, ContainerLevelAccess.create(level, pos))),
                    Component.translatable("menu.title.reactive.desk")
            );
        }
        return null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(player instanceof ServerPlayer splayer) {
            splayer.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DeskBlockEntity(pos, state);
    }
}
