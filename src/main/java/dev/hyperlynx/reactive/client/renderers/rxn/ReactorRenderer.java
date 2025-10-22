package dev.hyperlynx.reactive.client.renderers.rxn;

import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.client.ClientRegistration;

public interface ReactorRenderer {
    default void renderReactions(Reactor reactor){
        for(ReactionRenderer renderer : ClientRegistration.REACTION_RENDERERS.getRenderers(reactor.getRenderReactions())){
            renderer.render(reactor);
        }
    }
}
