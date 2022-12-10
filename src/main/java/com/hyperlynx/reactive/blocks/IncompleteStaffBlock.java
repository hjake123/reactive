package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.fx.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class IncompleteStaffBlock extends StaffBlock{

    public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, 3);
    private static final double RING_HEIGHT = 1.1;

    public IncompleteStaffBlock(Properties props) {
        super(props.lightLevel((BlockState bs) -> bs.getValue(PROGRESS) > 0 ? 0 : 8));
        registerDefaultState(stateDefinition.any().setValue(PROGRESS, 0));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROGRESS);
    }

    public static void tryMakeProgress(Level l, BlockState state, BlockPos pos, Power exposed_power){
        if(l.isClientSide)
            return;

        int order = WorldSpecificValues.EFFECT_ORDER.get(l);
        Power[] order1 = {Powers.X_POWER.get(), Powers.Y_POWER.get(), Powers.Z_POWER.get()};
        Power[] order2 = {Powers.Y_POWER.get(), Powers.Z_POWER.get(), Powers.X_POWER.get()};
        Power[] order3 = {Powers.Z_POWER.get(), Powers.X_POWER.get(), Powers.Y_POWER.get()};

        if(state.getValue(PROGRESS) == 3){
            // Then complete the staff!
            // TODO: make different staves depending on power exposure.
            l.setBlock(pos, Registration.STAFF_OF_LIGHT.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            l.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 1.0F, 1.0F);
            ParticleScribe.drawParticleRing(l, Registration.RUNE_PARTICLE, pos, RING_HEIGHT, 1, 40);
            return;
        }

        if(order == 1 && order1[state.getValue(PROGRESS)].equals(exposed_power)
                || order == 2 && order2[state.getValue(PROGRESS)].equals(exposed_power)
                || order == 3 && order3[state.getValue(PROGRESS)].equals(exposed_power)){

            l.setBlock(pos, state.setValue(PROGRESS, state.getValue(PROGRESS) + 1), Block.UPDATE_CLIENTS);
            ParticleScribe.drawParticleRing(l, Registration.RUNE_PARTICLE, pos, RING_HEIGHT, state.getValue(PROGRESS) * 0.2 + 0.2, 5);

        }else{
            l.removeBlock(pos, true);
            ItemEntity dropped_staff = new ItemEntity(l, pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, Registration.STAFF_OF_POWER_ITEM.get().getDefaultInstance());
            l.addFreshEntity(dropped_staff);
            l.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.1F);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rng) {
        if(state.getValue(PROGRESS) > 0 && rng.nextFloat() < 0.05 + state.getValue(PROGRESS) * 0.1){
            ParticleScribe.drawParticleRing(level, Registration.RUNE_PARTICLE, pos, RING_HEIGHT, state.getValue(PROGRESS) * 0.2 + 0.2, 1);
        }
    }
}
