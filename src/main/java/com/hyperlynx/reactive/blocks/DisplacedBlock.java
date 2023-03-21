package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.advancements.FlagCriterion;
import com.hyperlynx.reactive.be.DisplacedBlockEntity;
import com.hyperlynx.reactive.util.HarvestChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DisplacedBlock extends Block implements EntityBlock {
    public DisplacedBlock() {
        super(Properties.copy(Blocks.GLASS)
                .isViewBlocking((BlockState state, BlockGetter getter, BlockPos pos) -> false)
                .noOcclusion()
                .noLootTable()
                .strength(1F)
                .explosionResistance(-1F)
                .sound(SoundType.CHAIN));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rng) {
        for(int i = 0; i < 4; i++){
            double x = pos.getX() + level.getRandom().nextFloat();
            double y = pos.getY() + level.getRandom().nextFloat();
            double z = pos.getZ() + level.getRandom().nextFloat();
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0, 0, 0);
        }
    }

    // Convert some other block into a Displaced Block.
    public static void displace(BlockState state_to_be_displaced, BlockPos pos, Level level, int duration){
        // Trigger the research for Displacement. This should happen only once per activation, so it's not that bad.
        if(!level.isClientSide)
            FlagCriterion.triggerForNearbyPlayers((ServerLevel) level, CriteriaTriggers.SEE_DISPLACEMENT_TRIGGER, pos, 16);
        displaceWithChain(state_to_be_displaced, pos, level, duration, null);
    }

    // Convert some other block into a Displaced Block, and then link it to the chain target.
    // If the chain target is also a Displaced Block, it will NEVER revert. Be careful here!
    public static void displaceWithChain(BlockState state_to_be_displaced, BlockPos pos, Level level, int duration, BlockPos chain){
        if(level.getBlockEntity(pos) != null || !HarvestChecker.canMineBlock(level, pos, state_to_be_displaced, 35F)
                || state_to_be_displaced.isAir()){
            return;
        }

        level.setBlockAndUpdate(pos, Registration.DISPLACED_BLOCK.get().defaultBlockState());
        BlockEntity be = level.getBlockEntity(pos);
        if(!(be instanceof DisplacedBlockEntity displaced)){
            System.err.println("Displaced Block Entity didn't attach...? Report this to hyperlynx!");
            return;
        }

        displaced.self_state = state_to_be_displaced;
        displaced.chain_target = chain;

        level.scheduleTick(pos, Registration.DISPLACED_BLOCK.get(), duration);
    }

    private boolean shouldNotReappear(ServerLevel level, BlockPos pos, DisplacedBlockEntity self_entity){
        boolean ret = level.getBlockState(pos.below()).is(Registration.VOLT_CELL.get());
        if(self_entity.chain_target != null)
            ret = ret || level.getBlockState(self_entity.chain_target).is(Registration.DISPLACED_BLOCK.get());
        return ret;
    }

    // After the duration expires, change back to an actual block.
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (!(blockentity instanceof DisplacedBlockEntity)){
            System.err.println("Something went wrong restoring block from displaced block. Report this to hyperlynx! I hope it wasn't expensive...");
            level.setBlock(pos, Blocks.GRAVEL.defaultBlockState(), Block.UPDATE_CLIENTS);
            return;
        }

        if(shouldNotReappear(level, pos, (DisplacedBlockEntity) blockentity)){
            level.scheduleTick(pos, Registration.DISPLACED_BLOCK.get(), 20);
            return;
        }

        level.setBlockAndUpdate(pos, ((DisplacedBlockEntity) blockentity).self_state);
    }

    // When broken, the displacement should end.
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if(level.getBlockEntity(pos) instanceof DisplacedBlockEntity displaced){
            level.setBlockAndUpdate(pos, displaced.self_state);
        }
        level.playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 1.0F, 0.8F);
        return true;
    }

    // Middle click brings up the block being displaced.
    @Override
    public ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
        BlockEntity entity = getter.getBlockEntity(pos);
        if(!(entity instanceof DisplacedBlockEntity displaced_entity))
            return ItemStack.EMPTY;
        return displaced_entity.self_state.getBlock().getCloneItemStack(getter, pos, state);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DisplacedBlockEntity(pos, state);
    }

    public RenderShape getRenderShape(BlockState p_48758_) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.IGNORE;
    }
}
