package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Objects;

public abstract class Reaction {

    protected HashMap<Power, Integer> reagents = new HashMap<>();
    protected ReactionStimuli stimulus = ReactionStimuli.NONE;

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
            Power chosen_power = WorldSpecificValue.getFromCollection(l, alias+"r"+i, Powers.POWER_SUPPLIER.get().getValues());
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

    public void setStimulus(ReactionStimuli rxs){
        this.stimulus = rxs;
    }

    public boolean conditionsMet(CrucibleBlockEntity crucible){
        for(Power p : reagents.keySet()){
            if(reagents.get(p) > crucible.getPowerLevel(p)){
                return false;
            }
        }
        return checkStimulus(crucible);
    }

    private boolean checkStimulus(CrucibleBlockEntity c){
        if(stimulus == ReactionStimuli.END){
            return Objects.requireNonNull(c.getLevel()).dimension().equals(Level.END);
        }else if(stimulus == ReactionStimuli.GOLD_SYMBOL){
            return c.areaMemory.exists(c.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get());
        }else if(stimulus == ReactionStimuli.ELECTRIC){
            return c.electricCharge > 0;
        }else if(stimulus == ReactionStimuli.EXPLOSION){
            return c.recentExplosion;
        }else if(stimulus == ReactionStimuli.SACRIFICE){
            return c.sacrificeCount >= WorldSpecificValue.get(Objects.requireNonNull(c.getLevel()), reagents.toString()+"_sacrifice_count", 1, 3);
        }else{
            return true;
        }
    }

    public abstract void run(CrucibleBlockEntity crucible);

    public abstract void render(ClientLevel l, CrucibleBlockEntity crucible);

    public enum ReactionStimuli {
        NONE,
        GOLD_SYMBOL,
        ELECTRIC,
        SACRIFICE,
        EXPLOSION,
        END
    }

    @Override
    public String toString(){
        return reagents.toString();
    }

}
