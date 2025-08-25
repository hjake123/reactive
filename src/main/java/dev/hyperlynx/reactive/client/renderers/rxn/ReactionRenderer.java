package dev.hyperlynx.reactive.client.renderers.rxn;

import dev.hyperlynx.reactive.be.CrucibleBlockEntity;

@FunctionalInterface
public interface ReactionRenderer {
    void render(final CrucibleBlockEntity reactor);
}
