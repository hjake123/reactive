package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class AstralSynthesisReaction extends SynthesisReaction{
    public AstralSynthesisReaction(String alias, Power resultPower, Power... reagents) {
        super(alias, resultPower, reagents);
        this.reagents.replaceAll((p, v) -> 1);
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        super.run(crucible);
        crucible.addPower(Powers.ASTRAL_POWER.get(), CrucibleBlockEntity.CRUCIBLE_MAX_POWER);
        Vec3 loc = Vec3.atCenterOf(crucible.getBlockPos());
        crucible.getLevel().playSound(null, crucible.getBlockPos(), SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS);
        crucible.integrity = 12;
    }
}