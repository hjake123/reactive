package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.advancements.FlagTrigger;
import dev.hyperlynx.reactive.be.DisplacedBlockEntity;
import dev.hyperlynx.reactive.util.BlockMoveChecker;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DisplacedBlock extends Block implements EntityBlock {
    public DisplacedBlock(Properties props) {
        super(props);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rng) {
        for(int i = 0; i < 4; i++){
            double x = pos.getX() + rng.nextFloat();
            double y = pos.getY() + rng.nextFloat();
            double z = pos.getZ() + rng.nextFloat();
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0, 0, 0);
        }
    }

    // Convert some other block into a Displaced Block.
    public static boolean displace(BlockState state_to_be_displaced, BlockPos pos, Level level, int duration){
        // Trigger the research for Displacement. This should happen only once per activation, so it's not that bad.
        if(!level.isClientSide)
            FlagTrigger.triggerForNearbyPlayers((ServerLevel) level, Registration.SEE_DISPLACEMENT_TRIGGER.get(), pos, 16);
        return displaceWithChain(state_to_be_displaced, pos, level, duration, 0, null);
    }

    // Convert some other block into a Displaced Block, and then link it to the chain target.
    // If the chain target is also a Displaced Block, it will NEVER revert. Be careful here!
    public static boolean displaceWithChain(BlockState state_to_be_displaced, BlockPos pos, Level level, int duration, int depth, BlockPos chain){
        if(level.getBlockEntity(pos) != null || !BlockMoveChecker.canDisplaceBlock(level, pos, state_to_be_displaced)
                || state_to_be_displaced.isAir()){
            return false;
        }

        level.setBlock(pos, Registration.DISPLACED_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS);
        BlockEntity be = level.getBlockEntity(pos);
        if(!(be instanceof DisplacedBlockEntity displaced)){
            System.err.println("Displaced Block Entity didn't attach...? Report this to hyperlynx!");
            return false;
        }

        displaced.setSelfState(state_to_be_displaced);
        displaced.chain_target = chain;
        displaced.depth = depth;

        level.scheduleTick(pos, Registration.DISPLACED_BLOCK.get(), duration);
        return true;
    }

    private boolean shouldNotReappear(ServerLevel level, BlockPos pos, DisplacedBlockEntity self_entity){
        if(self_entity.getSelfState() == null)
            return true;
        boolean ret = level.getBlockState(pos.below()).is(Registration.VOLT_CELL.get());
        if(self_entity.chain_target != null)
            ret = ret || level.getBlockState(self_entity.chain_target).is(Registration.DISPLACED_BLOCK.get());
        return ret;
    }

    // After the duration expires, change back to an actual block.
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (!(blockentity instanceof DisplacedBlockEntity displaced)){
            System.err.println("Something went wrong restoring block from displaced block. Report this to hyperlynx! I hope it wasn't expensive...");
            level.setBlock(pos, Blocks.GRAVEL.defaultBlockState(), Block.UPDATE_CLIENTS);
            return;
        }

        if(shouldNotReappear(level, pos, displaced)){
            if(displaced.first_tick){
                level.scheduleTick(pos, Registration.DISPLACED_BLOCK.get(), 2 + displaced.depth);
                displaced.first_tick = false;
            }else {
                level.scheduleTick(pos, Registration.DISPLACED_BLOCK.get(), 20);
            }
            return;
        }

        level.setBlockAndUpdate(pos, displaced.getSelfState());
        level.updateNeighborsAt(pos, this);
    }

    // When broken, the displacement should end. If needed, this will instead break the Volt Cell.
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        reform(level, pos, Blocks.AIR.defaultBlockState());
        return true;
    }

    private static void reform(Level level, BlockPos pos, BlockState new_state) {
        if(level.getBlockEntity(pos) instanceof DisplacedBlockEntity displaced){
            if(displaced.getSelfState().is(new_state.getBlock()))
                return;
            if(displaced.getSelfState().getBlock() instanceof DisplacedBlock){
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 1.0F, 0.6F);
                return;
            }
            if(level.getBlockState(pos.below()).is(Registration.VOLT_CELL.get())){
                level.destroyBlock(pos.below(), true);
            }
            level.setBlockAndUpdate(pos, displaced.getSelfState());
            level.playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 1.0F, 0.8F);
        }else{
            System.err.println("Didn't find a valid block entity associated with the displaced block at " + pos + "! Report this to hyperlynx!");
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState new_state, boolean moving) {
        if (new_state.is(Registration.DISPLACED_BLOCK.get()))
            return;
        reform(level, pos, new_state);
        super.onRemove(state, level, pos, new_state, moving);
    }

    // Middle click brings up the block being displaced.
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(!(entity instanceof DisplacedBlockEntity displaced_entity))
            return ItemStack.EMPTY;
        if(displaced_entity.getSelfState().getBlock() instanceof DisplacedBlock)
            return ItemStack.EMPTY;
        return displaced_entity.getSelfState().getBlock().getCloneItemStack(state, target, level, pos, player);
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
}
