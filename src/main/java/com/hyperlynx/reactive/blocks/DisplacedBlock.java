package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.DisplacedBlockEntity;
import com.hyperlynx.reactive.util.HarvestChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
                .strength(45F));
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
        displaceWithChain(state_to_be_displaced, pos, level, duration, null);
        // Chain target will be null, indicating no chain.
    }

    // Convert some other block into a Displaced Block, and then link it to the chain target.
    // If the chain target is also a Displaced Block, it will NEVER revert. Be careful here!
    public static void displaceWithChain(BlockState state_to_be_displaced, BlockPos pos, Level level, int duration, BlockPos chain){
        if(level.getBlockEntity(pos) != null || !HarvestChecker.canMineBlock(level, pos, state_to_be_displaced, 35F)
                || state_to_be_displaced.isAir()){
            return;
        }

        level.setBlock(pos, Registration.DISPLACED_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS);
        BlockEntity be = level.getBlockEntity(pos);
        if(!(be instanceof DisplacedBlockEntity)){
            System.err.println("Displaced Block Entity didn't attach...? Report this to hyperlynx!");
            return;
        }
        DisplacedBlockEntity displaced = (DisplacedBlockEntity)be;

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
            level.scheduleTick(pos, Registration.DISPLACED_BLOCK.get(), 100);
            return;
        }

        level.setBlock(pos, ((DisplacedBlockEntity) blockentity).self_state, Block.UPDATE_CLIENTS);
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
    protected void spawnDestroyParticles(Level level, Player p_152423_, BlockPos pos, BlockState p_152425_) {
        for(int i = 0; i < 7; i++){
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.IGNORE;
    }
}
