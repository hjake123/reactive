package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.AlchemyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AcidBlock extends Block implements BucketPickup {
    public AcidBlock(Properties props) {
        super(props);
    }

    @Override
    public @NotNull ItemStack pickupBlock(@Nullable Player player, LevelAccessor accessor, BlockPos pos, BlockState state) {
        accessor.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        return Registration.ACID_BUCKET.get().getDefaultInstance();
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.SLIME_BLOCK_BREAK);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_154285_, BlockGetter p_154286_, BlockPos p_154287_, CollisionContext p_154288_) {
        return Shapes.empty();
    }

    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if(entity instanceof ItemEntity item){
            ItemStack stack = item.getItem();
            if(stack.getMaxStackSize() > 1){
                stack.shrink(1);
            }else{
                stack.hurtAndBreak(3, (ServerLevel) level, null, (i) -> stack.setCount(0));
            }
            if(stack.getCount() < 1){
                item.kill();
                level.playSound(null, pos, SoundEvents.GENERIC_BURN, SoundSource.BLOCKS, 1.0F, 1.0F);
            }else {
                item.setItem(stack);
            }
            return;
        }

        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        living.makeStuckInBlock(state, new Vec3(0.9F, 1.5D, 0.9F));
        living.hurt(level.damageSources().inFire(), 2);
    }

    private void killPlantsUnderneath(Level level, BlockPos pos, BlockState state){
        if(blockIsOnExcludedList(level.getBlockState(pos.below())))
            return;
        level.scheduleTick(pos, state.getBlock(), 10);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
        if(!(ConfigMan.COMMON.acidMeltBlockEntities.get()) && level.getBlockEntity(pos.below()) != null)
            return;
        BlockState state_beneath = level.getBlockState(pos.below());
        if(blockIsOnExcludedList(state_beneath))
            return;
        if(state_beneath.getBlock() instanceof SnowyDirtBlock){
            level.setBlockAndUpdate(pos.below(), Blocks.COARSE_DIRT.defaultBlockState());
        }
        if(state_beneath.getBlock() instanceof LeavesBlock || state_beneath.getBlock() instanceof CropBlock
                || state_beneath.getBlock() instanceof GrowingPlantBlock){
            level.setBlockAndUpdate(pos.below(), state);
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            level.scheduleTick(pos.below(), state.getBlock(), 10);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
        if(!(ConfigMan.COMMON.acidMeltBlockEntities.get()) && level.getBlockEntity(pos.below()) != null)
            return;
        killPlantsUnderneath(level, pos, state);
        BlockState state_beneath = level.getBlockState(pos.below());
        if(blockIsOnExcludedList(state_beneath))
            return;
        if(state_beneath.getBlock().getCloneItemStack(level, pos, state).is(ItemTags.LOGS)
                || state_beneath.getBlock().getCloneItemStack(level, pos, state).is(ItemTags.PLANKS)
                || state_beneath.getBlock().getCloneItemStack(level, pos, state).is(ItemTags.WOOL)
                || state_beneath.getBlock().getCloneItemStack(level, pos, state).is(ItemTags.WOOL_CARPETS)
                || state_beneath.getBlock() instanceof BonemealableFeaturePlacerBlock || state_beneath.is(Blocks.DRIPSTONE_BLOCK)){
            level.setBlockAndUpdate(pos.below(), state);
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        if(state_beneath.getBlock() instanceof WeatheringCopper){
            if(WeatheringCopper.getNext(state_beneath.getBlock()).isPresent()){
                level.setBlockAndUpdate(pos.below(), WeatheringCopper.getNext(state_beneath.getBlock()).get().defaultBlockState());
            }
        }
    }

    private boolean blockIsOnExcludedList(BlockState b){
        return b.is(AlchemyTags.acidImmune);
    }

    @Override
    public boolean isRandomlyTicking(BlockState irrelevant) {
        return true;
    }

    @Override
    public void onPlace(BlockState p_60566_, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        killPlantsUnderneath(level, pos, level.getBlockState(pos));
    }

    @Override
    public void neighborChanged(BlockState our_state, Level level, BlockPos pos, Block block, BlockPos neighbor_pos, boolean unknown) {
        killPlantsUnderneath(level, pos, our_state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rng) {
        if(!level.getBlockState(pos.above()).isSolidRender()) {
            for (int i = 0; i < 1; i++) {
                double x = pos.getX() + rng.nextFloat();
                double y = pos.getY() + 1.0F;
                double z = pos.getZ() + rng.nextFloat();
                level.addParticle(Registration.ACID_BUBBLE_PARTICLE, x, y, z, 0, 0, 0);
            }
        }
    }
}
