package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Objects;

public class IncompleteStaffBlock extends BaseStaffBlock{

    public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, 3);
    private static final double RING_HEIGHT_1 = 0.8;
    private static final double RING_HEIGHT_2 = 1.1;
    private static final double RING_HEIGHT_3 = 1.4;

    public IncompleteStaffBlock(Properties props) {
        super(props.lightLevel((BlockState bs) -> bs.getValue(PROGRESS) > 0 ? 0 : 8));
        registerDefaultState(this.defaultBlockState().setValue(PROGRESS, 0));
    }

    public static void staffCraftStep(CrucibleBlockEntity c, BlockPos staff_pos){
        for(Power p : c.getPowerMap().keySet()){
            if(c.getPowerLevel(p) > 800){
                tryMakeProgress(Objects.requireNonNull(c.getLevel()), c.getLevel().getBlockState(staff_pos), staff_pos, p);
            }
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PROGRESS);
    }

    public static void tryMakeProgress(Level level, BlockState state, BlockPos pos, Power exposed_power) {
        if (level.isClientSide)
            return;

        int order = WorldSpecificValues.EFFECT_ORDER.get();
        Power[] order1 = {Powers.X_POWER.get(), Powers.Y_POWER.get(), Powers.Z_POWER.get()};
        Power[] order2 = {Powers.Y_POWER.get(), Powers.Z_POWER.get(), Powers.X_POWER.get()};
        Power[] order3 = {Powers.Z_POWER.get(), Powers.X_POWER.get(), Powers.Y_POWER.get()};

        if (state.getValue(PROGRESS) == 3) {
            // Then complete the staff!

            Block staff_to_become = Blocks.AIR;

            if (exposed_power == Powers.LIGHT_POWER.get())
                staff_to_become = Registration.STAFF_OF_LIGHT.get();
            else if (exposed_power == Powers.WARP_POWER.get())
                staff_to_become = Registration.STAFF_OF_WARP.get();
            else if (exposed_power == Powers.BLAZE_POWER.get())
                staff_to_become = Registration.STAFF_OF_BLAZE.get();
            else if (exposed_power == Powers.MIND_POWER.get())
                staff_to_become = Registration.STAFF_OF_MIND.get();
            else if (exposed_power == Powers.VITAL_POWER.get())
                staff_to_become = Registration.STAFF_OF_LIFE.get();
            else if (exposed_power == Powers.SOUL_POWER.get())
                staff_to_become = Registration.STAFF_OF_SOUL.get();

            if (staff_to_become == Blocks.AIR) {
                return;
            }

            level.setBlock(pos, staff_to_become.defaultBlockState(), UPDATE_CLIENTS);
            level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 1.0F, 1.0F);
            return;
        }
        if (order == 1 && order1[state.getValue(PROGRESS)].equals(exposed_power)
                || order == 2 && order2[state.getValue(PROGRESS)].equals(exposed_power)
                || order == 3 && order3[state.getValue(PROGRESS)].equals(exposed_power)) {

            level.setBlock(pos, state.setValue(PROGRESS, state.getValue(PROGRESS) + 1), UPDATE_CLIENTS);
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.1F);
        }
        else{
            if(exposed_power.equals(Powers.X_POWER.get())
                    || exposed_power.equals(Powers.Y_POWER.get())
                    || exposed_power.equals(Powers.Z_POWER.get())) {
                failCrafting(level, pos);
            }
        }
    }

    private static void failCrafting(Level l, BlockPos pos){
        l.removeBlock(pos, true);
        ItemEntity dropped_staff = new ItemEntity(l, pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, Registration.INCOMPLETE_STAFF_ITEM.get().getDefaultInstance());
        l.addFreshEntity(dropped_staff);
        l.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.1F);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rng) {
        if(state.getValue(PROGRESS) > 0 && rng.nextFloat() < 0.05 + state.getValue(PROGRESS) * 0.1){
            ParticleScribe.drawParticleRing(level, Registration.RUNE_PARTICLE, pos, RING_HEIGHT_1, state.getValue(PROGRESS) * 0.2 + 0.2, state.getValue(PROGRESS));
            level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.3F, 1.1F);
        }
        if(state.getValue(PROGRESS) > 1 && rng.nextFloat() < 0.05 + state.getValue(PROGRESS) * 0.1){
            ParticleScribe.drawParticleRing(level, Registration.RUNE_PARTICLE, pos, RING_HEIGHT_2, state.getValue(PROGRESS) * 0.2 + 0.2, state.getValue(PROGRESS));
        }
        if(state.getValue(PROGRESS) > 2 && rng.nextFloat() < 0.05 + state.getValue(PROGRESS) * 0.1){
            ParticleScribe.drawParticleRing(level, Registration.RUNE_PARTICLE, pos, RING_HEIGHT_3, state.getValue(PROGRESS) * 0.2 + 0.2, state.getValue(PROGRESS));
        }
    }
}
