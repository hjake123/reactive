package com.hyperlynx.reactive.alchemy.rxn;

import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;

// This class manages the world's reactions.
// When the level unloads, it resets the cache of reaction.
// When reactions are first asked for (likely by a ticking Crucible BE), it will calculate them.
// The set should be identical each time the world loads.

public class ReactionMan {
    static boolean initialized = false;
    private static final HashSet<Reaction> REACTIONS = new HashSet<>();

    public HashSet<Reaction> getReactions(Level l){
        return initialized ? REACTIONS : constructReactions(l);
    }

    // Creates, from scratch, a set of all possible reactions that can be done in the world.
    private HashSet<Reaction> constructReactions(Level l){

        // Add three assimilation reactions.
        REACTIONS.add(new AssimilationReaction(l, "assimilation1"));
        REACTIONS.add(new AssimilationReaction(l, "assimilation2"));
        REACTIONS.add(new AssimilationReaction(l, "assimilation3"));

        // Add two annihilation reactions.
        REACTIONS.add(new AnnihilationReaction(l, "annihilation1"));
        REACTIONS.add(new AnnihilationReaction(l, "annihilation2"));
        initialized = true;
        System.out.println(REACTIONS);
        return REACTIONS;
    }

    @SubscribeEvent
    public void worldUnload(LevelEvent.Unload event){
        initialized = false;
        REACTIONS.clear();
    }
}
