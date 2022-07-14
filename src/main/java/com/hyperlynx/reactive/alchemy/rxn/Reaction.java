package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public abstract class Reaction {

    protected HashMap<Power, Integer> reagents = new HashMap<>();

    public Reaction(HashMap<Power, Integer> reagents){
        this.reagents = reagents;
    }

    // Creates the reaction with a random set of reagents.
    public Reaction(Level l, String alias, int max_reagent_count){
        int reagent_count = WorldSpecificValue.get(l, alias+"reagent_count", 2, max_reagent_count);
        int i = 0;

        while(reagents.size() < reagent_count){
            Power chosen_power = WorldSpecificValue.getFromCollection(l, alias+"r"+i, Registration.POWERS.getEntries()).get();
            int min = WorldSpecificValue.get(l, alias+"r"+i, 1, 400);
            reagents.put(chosen_power, min);
            i++;
        }
    }

    public boolean conditionsMet(CrucibleBlockEntity crucible){
        for(Power p : reagents.keySet()){
            if(reagents.get(p) > crucible.getPowerLevel(p)){
                return false;
            }
        }
        return true;
    }

    public abstract void run(CrucibleBlockEntity crucible);

    public abstract void render(ClientLevel l, CrucibleBlockEntity crucible);

    @Override
    public String toString(){
        return reagents.toString();
    }

}
