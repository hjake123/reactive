package com.hyperlynx.reactive.integration.kubejs;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;

import java.util.List;

public class ReactionFactory {
    CustomReaction rxn;

    public ReactionFactory(String alias, List<Power> reagent_locations) {
        rxn = new CustomReaction(alias, reagent_locations);
    }

    public ReactionFactory setStimulus(String name){
        rxn.setStimulus(Reaction.Stimulus.valueOf(name));
        return this;
    }

    public void build(){
        ReactionMan.addReactions(rxn);
    }
}
