package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// Uh-oh, looks like ReactionMan's on the hunt!
// This class manages the world's reactions.
// When the level unloads, it resets the cache of reaction.
// When reactions are first asked for (likely by a ticking Crucible BE), it will calculate them.
// The list should be identical each time the world loads.

public class ReactionMan {
    static boolean initialized = false;
    private static final LinkedList<Reaction> REACTIONS = new LinkedList<>();
    public static ArrayList<Power> BASE_POWER_LIST = new ArrayList<>();

    public List<Reaction> getReactions(Level l){
        return initialized || l.isClientSide() ? REACTIONS : constructReactions();
    }

    public boolean isInitialized(){
        return initialized;
    }

    // Creates, from scratch, a set of all possible reactions that can be done in the world.
    private LinkedList<Reaction> constructReactions(){
        // Set up the Base Power List.
        BASE_POWER_LIST.add(Powers.BLAZE_POWER.get());
        BASE_POWER_LIST.add(Powers.WARP_POWER.get());
        BASE_POWER_LIST.add(Powers.SOUL_POWER.get());
        BASE_POWER_LIST.add(Powers.LIGHT_POWER.get());
        BASE_POWER_LIST.add(Powers.MIND_POWER.get());
        BASE_POWER_LIST.add(Powers.VITAL_POWER.get());
        BASE_POWER_LIST = WorldSpecificValue.shuffle("power_list_order", BASE_POWER_LIST);

        // Add assimilation reactions.
        REACTIONS.add(new CurseAssimilationReaction("curse_assimilation"));
        REACTIONS.add(new AssimilationReaction("vital_kill", Powers.ACID_POWER.get(), Powers.VITAL_POWER.get()));
        REACTIONS.add(new AssimilationReaction("vital_eat", Powers.VITAL_POWER.get(), Powers.BODY_POWER.get()));


        switch (WorldSpecificValues.VERDANT_VITAL_RELATIONSHIP.get()) {
            case 2 ->
                    REACTIONS.add(new AssimilationReaction("verdant_consume", Powers.VERDANT_POWER.get(), Powers.VITAL_POWER.get()));
            case 3 ->
                    REACTIONS.add(new AssimilationReaction("vital_consume", Powers.VITAL_POWER.get(), Powers.VERDANT_POWER.get()));
            case 4 ->
                    REACTIONS.add(new SynthesisReaction("verdant_growth", Powers.VERDANT_POWER.get(), Powers.VITAL_POWER.get(), Powers.LIGHT_POWER.get()));
            case 5 ->
                    REACTIONS.add(new SynthesisReaction("vital_growth", Powers.VITAL_POWER.get(), Powers.VERDANT_POWER.get(), Powers.LIGHT_POWER.get()));
        }

        // Add annihilation reactions for each 'counteracting' pair of powers.
        // Imagine the base powers to be arranged in a hexagon, numbered clockwise. The opposites are counteracting.
        REACTIONS.add(new AnnihilationReaction("annihilation0v3", BASE_POWER_LIST.get(0), BASE_POWER_LIST.get(3), ReactionEffects::discharge));
        REACTIONS.add(new AnnihilationReaction("annihilation1v4", BASE_POWER_LIST.get(1), BASE_POWER_LIST.get(4), ReactionEffects::smoke));
        REACTIONS.add(new AnnihilationReaction("annihilation2v5", BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(5), ReactionEffects::salt));

        // Add synthesis reactions for the three esoteric powers.
        REACTIONS.add(new SynthesisReaction("x_synthesis", Powers.X_POWER.get(), BASE_POWER_LIST.get(0), BASE_POWER_LIST.get(1))
                .setStimulus(Reaction.Stimulus.ELECTRIC));
        REACTIONS.add(new SynthesisReaction("y_synthesis", Powers.Y_POWER.get(), BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(3))
                .setStimulus(Reaction.Stimulus.ELECTRIC));
        REACTIONS.add(new SynthesisReaction("z_synthesis", Powers.Z_POWER.get(), BASE_POWER_LIST.get(4), BASE_POWER_LIST.get(5))
                .setStimulus(Reaction.Stimulus.ELECTRIC));

        // Add effect reactions to do crazy things.
        REACTIONS.add(new EffectReaction("growth_effect", ReactionEffects::growth, Powers.VERDANT_POWER.get(), 1).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
        REACTIONS.add(new FreeEffectReaction("flames_effect", ReactionEffects::flamethrower, Powers.BLAZE_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
        REACTIONS.add(new EffectReaction("levitation_effect", ReactionEffects::levitation, 1).setStimulus(Reaction.Stimulus.END_CRYSTAL));

        // Add end crystal conversion reactions
        switch (WorldSpecificValues.CONDUIT_POWER.get()) {
            case 1 -> REACTIONS.add(new DecomposeReaction("soul_to_warp", Powers.SOUL_POWER.get(), Powers.WARP_POWER.get()).setStimulus(Reaction.Stimulus.END_CRYSTAL));
            case 2 -> REACTIONS.add(new DecomposeReaction("warp_to_soul", Powers.WARP_POWER.get(), Powers.SOUL_POWER.get()).setStimulus(Reaction.Stimulus.END_CRYSTAL));
        }

        // Add esoteric effect reactions and make one of them degrade when not electrified.
        int order = WorldSpecificValues.EFFECT_ORDER.get();
        switch (order) {
            case 1 -> {
                REACTIONS.add(new DecomposeReaction("compound_degradation", Powers.X_POWER.get(), BASE_POWER_LIST.get(0), BASE_POWER_LIST.get(1)).setStimulus(Reaction.Stimulus.NO_ELECTRIC));
                REACTIONS.add(new EffectReaction("vortex_effect", ReactionEffects::explosion, Powers.X_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction("formation_effect", ReactionEffects::foaming, Powers.Y_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction("falling_effect", ReactionEffects::blockfall, Powers.Z_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
            }
            case 2 -> {
                REACTIONS.add(new DecomposeReaction("compound_degradation", Powers.Y_POWER.get(), BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(3)).setStimulus(Reaction.Stimulus.NO_ELECTRIC));
                REACTIONS.add(new EffectReaction("vortex_effect", ReactionEffects::explosion, Powers.Y_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction("formation_effect", ReactionEffects::foaming, Powers.Z_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction("falling_effect", ReactionEffects::blockfall, Powers.X_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
            }
            case 3 -> {
                REACTIONS.add(new DecomposeReaction("compound_degradation", Powers.Z_POWER.get(), BASE_POWER_LIST.get(4), BASE_POWER_LIST.get(5)).setStimulus(Reaction.Stimulus.NO_ELECTRIC));
                REACTIONS.add(new EffectReaction("vortex_effect", ReactionEffects::explosion, Powers.Z_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction("formation_effect", ReactionEffects::foaming, Powers.X_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
                REACTIONS.add(new EffectReaction("falling_effect", ReactionEffects::blockfall, Powers.Y_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
            }
        }

        // Add the sacrificial reactions.
        REACTIONS.add(new DecomposeReaction("necromancy", Powers.SOUL_POWER.get(), Powers.CURSE_POWER.get()).setStimulus(Reaction.Stimulus.SACRIFICE));

        initialized = true;
        return REACTIONS;
    }

    @SubscribeEvent
    public void worldUnload(LevelEvent.Unload event){
        reset();
    }

    public void reset() {
        initialized = false;
        REACTIONS.clear();
        BASE_POWER_LIST.clear();
    }
}
