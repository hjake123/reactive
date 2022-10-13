package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
            if(level.dimensionType().ultraWarm()){
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
                for(int i = 0; i < 5; i++)
                    Helper.drawParticleCrucibleTop(level, ParticleTypes.LARGE_SMOKE, pos);
                Registration.TRY_NETHER_CRUCIBLE_TRIGGER.trigger((ServerPlayer)player);
            }else{
                level.setBlock(pos, state.setValue(FULL, true), Block.UPDATE_CLIENTS);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.4F, 1F);
            }
            if(player instanceof ServerPlayer){
                if(((ServerPlayer) player).gameMode.isSurvival()){
                    player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                }
            }
        }else if(player.getItemInHand(hand).is(Items.LAVA_BUCKET) && !state.getValue(FULL)){
            level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5F, 1.0F);
            level.playSound(null, pos, SoundEvents.GENERIC_BURN, SoundSource.BLOCKS, 0.5F, 1.0F);
            for(int i = 0; i < 5; i++)
                Helper.drawParticleCrucibleTop(level, ParticleTypes.LARGE_SMOKE, pos);
            level.setBlock(pos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_CLIENTS);
            Registration.TRY_LAVA_CRUCIBLE_TRIGGER.trigger((ServerPlayer)player);
        }

        if(state.getValue(FULL)){
            BlockEntity ent = level.getBlockEntity(pos);
            if(ent instanceof CrucibleBlockEntity c){
                // Clear the crucible with shift-right-click.
                if(player.isShiftKeyDown()){
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.6F, 0.8F);
                    level.setBlock(pos, state.setValue(FULL, false), Block.UPDATE_CLIENTS);
                }

                if(player.getItemInHand(hand).is(Items.GLASS_BOTTLE)){
                    int amount = player.getItemInHand(hand).getCount();
                    amount = Math.min(amount, 3);
                    for(int i = 0; i < amount; i++) {
                        if (c.getTotalPowerLevel() == 0) {
                            player.addItem(Items.POTION.getDefaultInstance());
                        } else {
                            ItemStack potion = Items.POTION.getDefaultInstance();
                            if (c.getPowerLevel(Powers.BODY_POWER.get()) > 10) {
                                PotionUtils.setPotion(potion, Potions.THICK);
                            } else if (c.getPowerLevel(Powers.ACID_POWER.get()) > 50) {
                                PotionUtils.setPotion(potion, Potions.AWKWARD);
                            } else {
                                PotionUtils.setPotion(potion, Potions.MUNDANE);
                            }
                            player.addItem(potion);
                        }
                    }
                    player.getItemInHand(hand).shrink(amount);
                    level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1F, 1F);
                    level.setBlock(pos, state.setValue(FULL, false), Block.UPDATE_CLIENTS);
                }

                // Collect bottles of mundane Powers.
                if(player.getItemInHand(hand).is(Registration.QUARTZ_BOTTLE.get())){
                    for(Power p : c.getPowerMap().keySet()){
                        if(c.getPowerLevel(p) == c.getTotalPowerLevel() && c.getPowerLevel(p) > 1000){
                            c.expendPower(p, 1000);
                            player.addItem(p.getBottle());
                            player.getItemInHand(hand).shrink(1);
                            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 1F);
                        }
                    }
                    c.setDirty();
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
