package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

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

    public static List<Entity> getEntitesInside(BlockPos pos, Level level){
        return level.getEntities(null, INSIDE.bounds().move(pos));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
        if(level.isClientSide()){
            return InteractionResult.SUCCESS;
        }
        if(player.getItemInHand(hand).is(Items.WATER_BUCKET) && !state.getValue(FULL)){
            level.setBlock(pos, state.setValue(FULL, true), Block.UPDATE_CLIENTS);
            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.4F, 1F);
            if(player instanceof ServerPlayer) {
                if(((ServerPlayer) player).gameMode.isSurvival()){
                    player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                }
            }
        }

        if(state.getValue(FULL)){
            BlockEntity ent = level.getBlockEntity(pos);
            if(ent instanceof CrucibleBlockEntity){
                // Clear the crucible with shift-right-click.
                if(player.isShiftKeyDown()){
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.6F, 0.8F);
                    level.setBlock(pos, state.setValue(FULL, false), Block.UPDATE_CLIENTS);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <CrucibleBlockEntity extends BlockEntity> BlockEntityTicker<CrucibleBlockEntity> getTicker(Level level, BlockState state, BlockEntityType<CrucibleBlockEntity> type) {
       if(type == Registration.CRUCIBLE_BE_TYPE.get()){
           return (l, p, s, c) -> com.hyperlynx.reactive.be.CrucibleBlockEntity.tick(l, p, s, (com.hyperlynx.reactive.be.CrucibleBlockEntity) c);
       }
       return null;
    }

}
