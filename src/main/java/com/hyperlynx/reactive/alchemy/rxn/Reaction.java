package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.function.Function;

public abstract class Reaction {

    protected HashMap<Power, Integer> reagents = new HashMap<>();
    protected Function<CrucibleBlockEntity, CrucibleBlockEntity> effectFunction;

    public Reaction(HashMap<Power, Integer> reagents){
        this.reagents = reagents;
    }

    // Creates the reaction with a random set of reagents.
    public Reaction(Level l, String alias, int max_reagent_count){
        int reagent_count;
        if(max_reagent_count < 3){
            reagent_count = max_reagent_count;
        }else{
            reagent_count = WorldSpecificValue.get(l, alias+"reagent_count", 2, max_reagent_count);
        }
        int i = 0;

        while(reagents.size() < reagent_count){
            Power chosen_power = WorldSpecificValue.getFromCollection(l, alias+"r"+i, ReactionMan.BASE_POWER_LIST);
            int min = WorldSpecificValue.get(l, alias+"r"+i, 1, 400);
            reagents.put(chosen_power, min);
            i++;
        }
    }

    // Creates the reaction with two preset powers, but random minimum requirements.
    public Reaction(Level l, String alias, Power p1, Power p2){
        reagents.put(p1, WorldSpecificValue.get(l, alias+"r1", 1, 400));
        reagents.put(p2, WorldSpecificValue.get(l, alias+"r2", 1, 400));
    }

    public boolean conditionsMet(CrucibleBlockEntity crucible){
        for(Power p : reagents.keySet()){
            if(reagents.get(p) > crucible.getPowerLevel(p)){
                return false;
            }
        }
        return true;
    }

    public void setEffect(Function<CrucibleBlockEntity, CrucibleBlockEntity> f){
        effectFunction = f;
    }

    public void run(CrucibleBlockEntity crucible){
        if(effectFunction != null) effectFunction.apply(crucible);
    }

    public abstract void render(ClientLevel l, CrucibleBlockEntity crucible);



    @Override
    public String toString(){
        return reagents.toString();
    }

}
