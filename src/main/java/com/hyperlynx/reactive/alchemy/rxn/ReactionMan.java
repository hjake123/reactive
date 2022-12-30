package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashSet;

// Uh-oh, looks like ReactionMan's on the hunt!
// This class manages the world's reactions.
// When the level unloads, it resets the cache of reaction.
// When reactions are first asked for (likely by a ticking Crucible BE), it will calculate them.
// The set should be identical each time the world loads.

public class ReactionMan {
    static boolean initialized = false;
    private static final HashSet<Reaction> REACTIONS = new HashSet<>();
    public static ArrayList<Power> BASE_POWER_LIST = new ArrayList<>();

    public HashSet<Reaction> getReactions(Level l){
        return initialized || l.isClientSide() ? REACTIONS : constructReactions(l);
    }

    // Creates, from scratch, a set of all possible reactions that can be done in the world.
    private HashSet<Reaction> constructReactions(Level l){
        // Set up the Base Power List.
        BASE_POWER_LIST.add(Powers.BLAZE_POWER.get());
        BASE_POWER_LIST.add(Powers.WARP_POWER.get());
        BASE_POWER_LIST.add(Powers.SOUL_POWER.get());
        BASE_POWER_LIST.add(Powers.LIGHT_POWER.get());
        BASE_POWER_LIST.add(Powers.MIND_POWER.get());
        BASE_POWER_LIST.add(Powers.VITAL_POWER.get());
        BASE_POWER_LIST = WorldSpecificValue.shuffle(l, "power_list_order", BASE_POWER_LIST);

        // Add assimilation reactions.
        REACTIONS.add(new CurseAssimilationReaction(l, "curse_assimilation"));
        REACTIONS.add(new AssimilationReaction(l, "vital_kill", Powers.ACID_POWER.get(), Powers.VITAL_POWER.get()));
        REACTIONS.add(new AssimilationReaction(l, "vital_eat", Powers.VITAL_POWER.get(), Powers.BODY_POWER.get()));


        switch (WorldSpecificValues.VERDANT_VITAL_RELATIONSHIP.get(l)) {
            case 2 ->
                    REACTIONS.add(new AssimilationReaction(l, "verdant_consume", Powers.VERDANT_POWER.get(), Powers.VITAL_POWER.get()));
            case 3 ->
                    REACTIONS.add(new AssimilationReaction(l, "vital_consume", Powers.VITAL_POWER.get(), Powers.VERDANT_POWER.get()));
            case 4 ->
                    REACTIONS.add(new SynthesisReaction(l, "verdant_growth", Powers.VERDANT_POWER.get(), Powers.VITAL_POWER.get(), Powers.LIGHT_POWER.get()));
            case 5 ->
                    REACTIONS.add(new SynthesisReaction(l, "vital_growth", Powers.VITAL_POWER.get(), Powers.VERDANT_POWER.get(), Powers.LIGHT_POWER.get()));
        }

        // Add annihilation reactions for each 'counteracting' pair of powers.
        // Imagine the base powers to be arranged in a hexagon, numbered clockwise. The opposites are counteracting.
        REACTIONS.add(new AnnihilationReaction(l, "annihilation0v3", BASE_POWER_LIST.get(0), BASE_POWER_LIST.get(3), ReactionEffects::discharge));
        REACTIONS.add(new AnnihilationReaction(l, "annihilation1v4", BASE_POWER_LIST.get(1), BASE_POWER_LIST.get(4), ReactionEffects::sicklySmoke));
        REACTIONS.add(new AnnihilationReaction(l, "annihilation2v5", BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(5), ReactionEffects::weakeningSmoke));

        // Add synthesis reactions for the three esoteric powers.
        REACTIONS.add(new SynthesisReaction(l, "x_synthesis", Powers.X_POWER.get(), BASE_POWER_LIST.get(0), BASE_POWER_LIST.get(1))
                .setStimulus(Reaction.Stimulus.ELECTRIC));
        REACTIONS.add(new SynthesisReaction(l, "y_synthesis", Powers.Y_POWER.get(), BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(3))
                .setStimulus(Reaction.Stimulus.ELECTRIC));
        REACTIONS.add(new SynthesisReaction(l, "z_synthesis", Powers.Z_POWER.get(), BASE_POWER_LIST.get(4), BASE_POWER_LIST.get(5))
                .setStimulus(Reaction.Stimulus.ELECTRIC));

        // Add effect reactions to do crazy things.
        REACTIONS.add(new EffectReaction(l, "growth_effect", ReactionEffects::growth, Powers.VERDANT_POWER.get(), 1));
        REACTIONS.add(new FreeEffectReaction(l, "flames_effect", ReactionEffects::flamethrower, Powers.BLAZE_POWER.get()));
        REACTIONS.add(new EffectReaction(l, "levitation_effect", ReactionEffects::levitation, 3).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));

        // Add end crystal conversion reactions
        switch (WorldSpecificValues.CONDUIT_POWER.get(l)) {
            case 1 -> REACTIONS.add(new DecomposeReaction(l, "soul_to_warp", Powers.SOUL_POWER.get(), Powers.WARP_POWER.get()).setStimulus(Reaction.Stimulus.END_CRYSTAL));
            case 2 -> REACTIONS.add(new DecomposeReaction(l, "warp_to_soul", Powers.WARP_POWER.get(), Powers.SOUL_POWER.get()).setStimulus(Reaction.Stimulus.END_CRYSTAL));
        }

        // Add esoteric effect reactions and make one of them degrade when not electrified.
        int order = WorldSpecificValues.EFFECT_ORDER.get(l);
        switch (order) {
            case 1 -> {
                REACTIONS.add(new DecomposeReaction(l, "compound_degradation", Powers.X_POWER.get(), BASE_POWER_LIST.get(0), BASE_POWER_LIST.get(1)).setStimulus(Reaction.Stimulus.NO_ELECTRIC));
                REACTIONS.add(new EffectReaction(l, "vortex_effect", ReactionEffects::explosion, Powers.X_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction(l, "formation_effect", ReactionEffects::formation, Powers.Y_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction(l, "falling_effect", ReactionEffects::blockfall, Powers.Z_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
            }
            case 2 -> {
                REACTIONS.add(new DecomposeReaction(l, "compound_degradation", Powers.Y_POWER.get(), BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(3)).setStimulus(Reaction.Stimulus.NO_ELECTRIC));
                REACTIONS.add(new EffectReaction(l, "vortex_effect", ReactionEffects::explosion, Powers.Y_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction(l, "formation_effect", ReactionEffects::formation, Powers.Z_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction(l, "falling_effect", ReactionEffects::blockfall, Powers.X_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
            }
            case 3 -> {
                REACTIONS.add(new DecomposeReaction(l, "compound_degradation", Powers.Z_POWER.get(), BASE_POWER_LIST.get(4), BASE_POWER_LIST.get(5)).setStimulus(Reaction.Stimulus.NO_ELECTRIC));
                REACTIONS.add(new EffectReaction(l, "vortex_effect", ReactionEffects::explosion, Powers.Z_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction(l, "formation_effect", ReactionEffects::formation, Powers.X_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction(l, "falling_effect", ReactionEffects::blockfall, Powers.Y_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
            }
        }

        // Add the sacrificial reactions.
        REACTIONS.add(new DecomposeReaction(l, "necromancy_1", Powers.SOUL_POWER.get(), Powers.CURSE_POWER.get()).setStimulus(Reaction.Stimulus.SACRIFICE));
        REACTIONS.add(new DecomposeReaction(l, "necromancy_2", Powers.VITAL_POWER.get(), Powers.CURSE_POWER.get()).setStimulus(Reaction.Stimulus.SACRIFICE));

        initialized = true;
        return REACTIONS;
    }

    @SubscribeEvent
    public void worldUnload(LevelEvent.Unload event){
        initialized = false;
        REACTIONS.clear();
        BASE_POWER_LIST.clear();
    }
}
