package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.util.PrimedWSV;

/*
This class is a holder for alias strings for WSV generation that might need to be repeated.
 */
public class WorldSpecificValues {
    public final static PrimedWSV GOLEM_CAUSE = new PrimedWSV("golem_cause", 1, 2);
    // Determines the reason that golems animate when a carved pumpkin in placed. The options are...
    // 1: A benevolent spirit is trapped by the pumpkin and seeks to aid its creator.
    // 2: A malevolent manifests near the pumpkin and is bound to the will of the creator.

    public final static PrimedWSV CONDUIT_POWER = new PrimedWSV("conduit_power", 1, 2);
    // Determines which power Conduit Power breaks down into in the crucible.
    // 1: SOUL
    // 2: WARP

    public final static PrimedWSV ANNIHILATION_THRESHOLD = new PrimedWSV("annihilation_threshold", 50, 200);
    // Determines the minimum power balance for Annihilation reactions to occur.

    public final static PrimedWSV BEST_SACRIFICE = new PrimedWSV("best_sacrifice", 1, 4);
    // Determines which of these categories gives the most Vital Power when sacrificed.
    // 1: Livestock
    // 2: Villagers/Illagers
    // 3: Piglin/Hoglin
    // 4: Spiders and Creepers

    public final static PrimedWSV EFFECT_ORDER = new PrimedWSV("effect_order", 1, 3);
    // Determines which of three orders the Effect Reactions require, which decides which esoteric each requires.
    // Refer to ReactionMan::constructReactions for details.

    public final static PrimedWSV CURSE_RATE = new PrimedWSV("curse_assimilation_rate", 10, 20);

    public final static PrimedWSV VERDANT_VITAL_RELATIONSHIP = new PrimedWSV("verdant_vital_relation", 1, 5);
    // Determines the relationship Verdant and Vital power have.
    // 1: No reaction
    // 2: Verdant consumes Vital
    // 3: Vital consumes Verdant
    // 4: Vital is Verdant + Light
    // 5: Verdant is Vital + Light

    public final static PrimedWSV CRYSTAL_IRON_UTILITY = new PrimedWSV("crystal_iron_utility", 0, 3);
    // Determines what kinds of effects Crystal Iron can absorb without being damaged.
    // 0 or 1: Crucible effects
    // 2: Health-draining effects (Poison, Wither)
    // 3: Other debuffs

}
