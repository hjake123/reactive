package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
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
        resultPower = WorldSpecificValue.getFromCollection(l, alias+"result", Powers.POWERS.getEntries()).get();
        correctOpposingReagents();
    }

    public SynthesisReaction(Level l, String alias, Power resultPower) {
        super(l, alias, 2);
        rate = WorldSpecificValue.get(l, alias+"rate", 20, 50);
        this.resultPower = resultPower;
        correctOpposingReagents();
    }

    public SynthesisReaction(Level l, String alias, Power resultPower, Power reagent1, Power reagent2) {
        super(l, alias, reagent1, reagent2);
        rate = WorldSpecificValue.get(l, alias+"rate", 20, 50);
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

    // Make it so that no synthesis reaction will use opposing Base Powers.
    private void correctOpposingReagents(){
        if(reagents.containsKey(ReactionMan.BASE_POWER_LIST.get(0)) && reagents.containsKey(ReactionMan.BASE_POWER_LIST.get(3))){
            int amount = reagents.remove(ReactionMan.BASE_POWER_LIST.get(3));
            reagents.put(ReactionMan.BASE_POWER_LIST.get(1), amount);
        }else if(reagents.containsKey(ReactionMan.BASE_POWER_LIST.get(1)) && reagents.containsKey(ReactionMan.BASE_POWER_LIST.get(4))){
            int amount = reagents.remove(ReactionMan.BASE_POWER_LIST.get(4));
            reagents.put(ReactionMan.BASE_POWER_LIST.get(2), amount);
        }else if(reagents.containsKey(ReactionMan.BASE_POWER_LIST.get(2)) && reagents.containsKey(ReactionMan.BASE_POWER_LIST.get(5))){
            int amount = reagents.remove(ReactionMan.BASE_POWER_LIST.get(5));
            reagents.put(ReactionMan.BASE_POWER_LIST.get(3), amount);
        }
    }
}
