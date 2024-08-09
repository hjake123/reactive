package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.items.CrystalIronItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class MindLichenBlock extends GlowLichenBlock {
    public MindLichenBlock(Properties props) {
        super(props);
    }

    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Player player && !player.isCreative() && level.random.nextFloat() < 0.03F) {
            if(player.totalExperience > 0){
                for(int i = 0; i < level.random.nextIntBetweenInclusive(1, 3); i++){
                    var to_pos = this.getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, level.random);
                    if(to_pos.isPresent()){
                        player.giveExperiencePoints(-1);
                        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.02F, 0.7F+level.random.nextFloat()*0.1F);
                    }
                }
            }
            return;
        }
        if (entity instanceof ExperienceOrb orb){
            this.getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, level.random);
            orb.value -= 1;
            if(orb.value == 0){
                orb.kill();
            }
        }
    }
}
