package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

public class SynthesisReaction extends Reaction{
    Power resultPower;
    int rate;

    public SynthesisReaction(Level l, String alias) {
        super(l, alias, 2);
        rate = WorldSpecificValue.get(l, alias+"rate", 1, 10);
        resultPower = WorldSpecificValue.getFromCollection(l, alias+"result", Registration.POWERS.getEntries()).get();
    }

    public SynthesisReaction(Level l, String alias, Power resultPower) {
        super(l, alias, 2);
        rate = WorldSpecificValue.get(l, alias+"rate", 1, 10);
        this.resultPower = resultPower;
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        for(Power p : reagents.keySet()){
            crucible.expendPower(p, (int) Math.ceil(rate/(double) reagents.size()));
        }
        crucible.addPower(resultPower, rate);
    }

    @Override
    public void render(ClientLevel l, CrucibleBlockEntity crucible) {
        // No need.
    }
}
