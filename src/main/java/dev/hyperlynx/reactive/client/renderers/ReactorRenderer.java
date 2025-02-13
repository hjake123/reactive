package dev.hyperlynx.reactive.client.renderers;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import net.minecraft.client.Minecraft;

public interface ReactorRenderer {
    default void checkReactions(Reactor reactor){
        reactor.resetRenderReactions();
        for(Reaction reaction : ReactiveMod.REACTION_MAN.getReactions(Minecraft.getInstance().level)){
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
