package dev.hyperlynx.reactive.client.renderers.rxn;

import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import net.minecraft.world.level.Level;

@FunctionalInterface
public interface ReactionRenderer {
    void render(final Reactor reactor);
}
