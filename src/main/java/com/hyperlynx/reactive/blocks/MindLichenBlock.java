package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.items.CrystalIronItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class MindLichenBlock extends GlowLichenBlock {
    public static final BooleanProperty EMPOWERED = BooleanProperty.create("empowered");

    public MindLichenBlock(Properties props) {
        super(props);
        registerDefaultState(this.defaultBlockState().setValue(EMPOWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EMPOWERED);
        super.createBlockStateDefinition(builder);
    }

    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if(state.getValue(EMPOWERED)){
            return;
        }
        if (entity instanceof Player player && !player.isCreative() && player.isCrouching()) {
            if(CrystalIronItem.effectNotBlocked(player, 1) && player.totalExperience > 10){
                player.giveExperiencePoints(-10);
                ExperienceOrb xp = new ExperienceOrb(level, player.getX(), player.getY() + 0.8, player.getZ(), 10);
                double throw_power = 1.3;
                xp.setDeltaMovement((level.getRandom().nextDouble() - 0.5) * throw_power, (level.getRandom().nextDouble() - 0.5) * throw_power, (level.getRandom().nextDouble() - 0.5) * throw_power);
                level.addFreshEntity(xp);
            }
            return;
        }
        if (entity instanceof ExperienceOrb orb && level instanceof ServerLevel server){
            for(int i = 0; i < level.random.nextIntBetweenInclusive(2, 5); i++){
                performBonemeal(server, server.random, pos, state);
            }
            orb.value = orb.value - 1;
            if(orb.value == 0){
                orb.kill();
            }
            level.setBlock(pos, state.setValue(EMPOWERED, true), Block.UPDATE_CLIENTS);
        }
    }
}
