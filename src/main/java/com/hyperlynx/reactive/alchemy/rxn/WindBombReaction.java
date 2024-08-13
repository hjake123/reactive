package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.client.particles.ParticleScribe;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class WindBombReaction extends FreeEffectReaction{
    public WindBombReaction(String alias) {
        super(alias, ReactionEffects::flowTooStrong, null, Powers.FLOW_POWER.get());
        this.setReagentCost(Powers.FLOW_POWER.get(), 1);
    }

    @Override
    public Status conditionsMet(CrucibleBlockEntity crucible) {
        Reaction.Status status = super.conditionsMet(crucible);
        if(status != Status.REACTING){
            return status;
        }
        if(checkBreezeRods(crucible)){
            return Status.REACTING;
        }
        return Status.INHIBITED;
    }

    @Override
    public boolean isPerfect(CrucibleBlockEntity crucible) {
        return super.isPerfect(crucible);
    }

    // Returns true if there are NOT enough Breeze Rods to prevent a reaction!
    public boolean checkBreezeRods(CrucibleBlockEntity crucible) {
        Level level = crucible.getLevel();
        if(level == null){
            return true;
        }
        BlockPos crucible_pos = crucible.getBlockPos();
        Random wsv = WorldSpecificValue.getSource("breeze_rod_placements");

        BlockPos rod_1_pos = crucible_pos.offset(3, 1, 1);
        if(!(level.getBlockState(rod_1_pos).is(Registration.BREEZE_ROD.get()))){
            ParticleScribe.drawParticleSphere(level, ParticleTypes.SMALL_GUST, rod_1_pos, 0.5, 0.1, 5);
            return true;
        }

        BlockPos rod_2_pos = rod_1_pos.offset(wsv.nextInt(-3, 4), 0, wsv.nextInt(-3, 4));
        ParticleScribe.drawParticleLine(level, ParticleTypes.SMALL_GUST, Vec3.atCenterOf(rod_1_pos).add(0, 0.5, 0), Vec3.atCenterOf(rod_2_pos).add(0, -0.5, 0), 5, 0);
        if(!(level.getBlockState(rod_2_pos).is(Registration.BREEZE_ROD.get()))){
            ParticleScribe.drawParticleSphere(level, ParticleTypes.SMALL_GUST, rod_2_pos, 0.5, 0.1, 5);
            return true;
        }

        BlockPos rod_3_pos = rod_2_pos.offset(wsv.nextInt(-3, 4), 0, wsv.nextInt(-3, 4));
        ParticleScribe.drawParticleLine(level, ParticleTypes.SMALL_GUST, Vec3.atCenterOf(rod_2_pos).add(0, 0.5, 0), Vec3.atCenterOf(rod_3_pos).add(0, -0.5, 0), 5, 0);
        if(!(level.getBlockState(rod_3_pos).is(Registration.BREEZE_ROD.get()))){
            ParticleScribe.drawParticleSphere(level, ParticleTypes.SMALL_GUST, rod_3_pos, 0.5, 0.1, 5);
            return true;
        }
        ParticleScribe.drawParticleLine(level, ParticleTypes.SMALL_GUST, Vec3.atCenterOf(rod_3_pos).add(0, 0.5, 0), Vec3.atCenterOf(rod_1_pos).add(0, -0.5, 0), 5, 0);
        return false;
    }
}
