package dev.hyperlynx.reactive.client.renderers;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;

public interface ReactorRenderer {
    default void checkReactions(Reactor reactor){
        reactor.resetRenderReactions();
        for(Reaction reaction : ReactiveMod.REACTION_MAN.getReactions()){
            if(reaction.conditionsMet(reactor) == Reaction.Status.REACTING){
                reactor.addRenderReaction(reaction);
            }
        }
    }

    default void renderReactions(Reactor reactor){
        for(Reaction reaction : reactor.getRenderReactions()){
            reaction.render(reactor.getLevel(), reactor);
        }
    }
}
