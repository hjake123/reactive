package dev.hyperlynx.reactive.client.renderers.rxn;

import dev.hyperlynx.reactive.alchemy.rxn.Reactor;

@FunctionalInterface
public interface ReactionRenderer {
    void render(final Reactor reactor);
}
