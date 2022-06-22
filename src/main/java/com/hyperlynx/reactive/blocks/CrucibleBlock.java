package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.tile.CrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CrucibleBlock extends Block implements EntityBlock {

    public static final BooleanProperty FULL = BooleanProperty.create("full");

    public CrucibleBlock(Properties p) {
        super(p);
        registerDefaultState(stateDefinition.any().setValue(FULL, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FULL);
        super.createBlockStateDefinition(builder);
    }

    // Copied from the model itself. There's probably a better way!
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(3, 1, 3, 13, 2, 13), Shapes.or(Block.box(13, 2, 3,14, 10, 13), Shapes.or(Block.box(2, 2, 3,3, 10, 13), Shapes.or(Block.box(3, 2, 2, 13, 10, 3), Shapes.or(Block.box(3, 2, 13,13, 10, 14))))));
    protected static final VoxelShape INSIDE = Block.box(3, 3, 3, 13, 9, 13);

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    public List<Entity> getEntitesInside(Level level){
        return level.getEntities(null, INSIDE.bounds());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
        if(level.isClientSide()){
            return InteractionResult.SUCCESS;
        }
        if(player.getItemInHand(hand).is(Items.WATER_BUCKET) && !state.getValue(FULL)){
            level.setBlock(pos, state.setValue(FULL, true), 2); // 2 is the flag for "update and notify client"
            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.4F, 1F);
            if(player instanceof ServerPlayer) {
                if(((ServerPlayer) player).gameMode.isSurvival()){
                    player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
