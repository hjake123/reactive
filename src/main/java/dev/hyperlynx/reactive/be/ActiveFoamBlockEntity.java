package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class ActiveFoamBlockEntity extends BlockEntity {
    int spreads_left = 16;

    public ActiveFoamBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.ACTIVE_GOLD_FOAM_BE.get(), pos, state);
    }

    private static void trySpread(Level level, BlockPos target, BlockPos spreader, int spreads_left){
        if(level.getBlockState(target).isAir() || level.getBlockState(target).is(Registration.GOLD_FOAM.get())){
            if(level.getBlockEntity(spreader) == null){
                return;
            }
            level.setBlock(target, Registration.ACTIVE_GOLD_FOAM.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            ((ActiveFoamBlockEntity) Objects.requireNonNull(level.getBlockEntity(target))).spreads_left = spreads_left - 1;
            level.setBlock(spreader, Registration.GOLD_FOAM.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            level.playSound(null, target, SoundEvents.WOOL_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    private static void trySpreadInert(Level level, BlockPos pos){
        if(level.getBlockState(pos).isAir()){
            level.setBlock(pos, Registration.GOLD_FOAM.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            level.playSound(null, pos, SoundEvents.WOOL_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    private static void trySpreadSideways(Level level, BlockPos pos, RandomSource random, int spreads_left){
        int active_direction = random.nextIntBetweenInclusive(1, 4);
        int forbidden_direction = WorldSpecificValue.get("gold_foam_spread_direction", 1, 4);
        if(active_direction == forbidden_direction){
            return;
        }
        if(active_direction == 1){
            trySpread(level, pos.east(), pos, spreads_left);
            trySpreadInert(level, pos.west());
            trySpreadInert(level, pos.north());
            trySpreadInert(level, pos.south());
        }else if(active_direction == 2){
            trySpreadInert(level, pos.east());
            trySpread(level, pos.west(), pos, spreads_left);
            trySpreadInert(level, pos.north());
            trySpreadInert(level, pos.south());
        }else if(active_direction == 3){
            trySpreadInert(level, pos.east());
            trySpreadInert(level, pos.west());
            trySpread(level, pos.north(), pos, spreads_left);
            trySpreadInert(level, pos.south());
        }else if(active_direction == 4){
            trySpreadInert(level, pos.east());
            trySpreadInert(level, pos.west());
            trySpreadInert(level, pos.north());
            trySpread(level, pos.south(), pos, spreads_left);
        }
    }

    int tick_counter = 0;

    public static void tick(Level level, BlockPos pos, BlockState state, ActiveFoamBlockEntity foam) {
        if(level.isClientSide)
            return;

        foam.tick_counter++;
        if(foam.tick_counter > 5) {
            foam.tick_counter = 0;
            if (foam.spreads_left == 0){
                level.setBlock(pos, Registration.GOLD_FOAM.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                return;
            }
            spreadAround(level, pos, level.random, foam.spreads_left);
        }
    }

    public static void spreadAround(Level level, BlockPos pos, RandomSource random, int spreads_left) {
        float up_chance = WorldSpecificValue.get("gold_foam_up_chance", 0.2F, 0.5F);
        float down_chance = WorldSpecificValue.get("gold_foam_down_chance", 0.2F, 0.5F);

        if(WorldSpecificValue.getBool("gold_foam_up_priority", 0.5F)){
            if(random.nextFloat() < up_chance){
                trySpread(level, pos.above(), pos, spreads_left);
            }else if(random.nextFloat() < down_chance){
                trySpread(level, pos.below(), pos, spreads_left);
            }else{
                trySpreadSideways(level, pos, random, spreads_left);
            }
        }else{
            if(random.nextFloat() < down_chance){
                trySpread(level, pos.below(), pos, spreads_left);
            }else if(random.nextFloat() < up_chance){
                trySpread(level, pos.above(), pos, spreads_left);
            }else{
                trySpreadSideways(level, pos, random, spreads_left);
            }
        }
    }
}
