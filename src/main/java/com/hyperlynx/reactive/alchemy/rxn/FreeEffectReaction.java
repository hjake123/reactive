package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class FreeEffectReaction extends EffectReaction{
    public FreeEffectReaction(Level l, String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Power required_power) {
        super(l, alias, function, required_power);
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        if(effectFunction != null)
            effectFunction.apply(crucible);
    }
}