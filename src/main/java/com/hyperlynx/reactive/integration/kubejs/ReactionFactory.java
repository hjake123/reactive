package com.hyperlynx.reactive.integration.kubejs;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class ReactionFactory {
    CustomReaction rxn;

    public ReactionFactory(String alias, List<Power> reagent_locations) {
        rxn = new CustomReaction(alias, reagent_locations);
    }

    private void warnOverwrites(){
        if(!rxn.getStimulus().equals(Reaction.Stimulus.NONE)){
            System.out.println("Overwriting the previous Reaction Stimulus for " + rxn.getAlias());
        }
    }

    public ReactionFactory needsGoldSymbol(){
        warnOverwrites();
        rxn.setStimulus(Reaction.Stimulus.GOLD_SYMBOL);
        return this;
    }

    public ReactionFactory needsElectric(){
        warnOverwrites();
        rxn.setStimulus(Reaction.Stimulus.ELECTRIC);
        return this;
    }

    public ReactionFactory needsNoElectric(){
        warnOverwrites();
        rxn.setStimulus(Reaction.Stimulus.NO_ELECTRIC);
        return this;
    }

    public ReactionFactory needsEndCrystal(){
        warnOverwrites();
        rxn.setStimulus(Reaction.Stimulus.END_CRYSTAL);
        return this;
    }

    public ReactionFactory needsNoEndCrystal(){
        warnOverwrites();
        rxn.setStimulus(Reaction.Stimulus.NO_END_CRYSTAL);
        return this;
    }

    public ReactionFactory setCost(int cost){
        rxn.cost = cost;
        return this;
    }

    public ReactionFactory setYield(String power_id, int yield){
        Power p = Powers.POWER_REGISTRY.get(ResourceLocation.parse(power_id));
        if(p != null){
            rxn.yield = yield;
            rxn.output_power = Optional.of(p);
        }
        return this;
    }

    public void build(){
        ReactionMan.addReactions(rxn);
    }
}
