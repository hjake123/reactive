package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Function;

// This class manages the world's reactions.
// When the level unloads, it resets the cache of reaction.
// When reactions are first asked for (likely by a ticking Crucible BE), it will calculate them.
// The set should be identical each time the world loads.

public class ReactionMan {
    static boolean initialized = false;
    private static final HashSet<Reaction> REACTIONS = new HashSet<>();
    public static ArrayList<Power> BASE_POWER_LIST = new ArrayList<>();
    private final static int EFFECT_CHANCE_PERCENT = 100;

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
        BASE_POWER_LIST.add(Powers.VITAL_POWER.get());
        BASE_POWER_LIST.add(Powers.MIND_POWER.get());
        BASE_POWER_LIST = WorldSpecificValue.shuffle(l, "power_list_order", BASE_POWER_LIST);

        // Add assimilation reactions.
        REACTIONS.add(new AssimilationReaction(l, "assimilationX"));
        REACTIONS.add(new CurseAssimilationReaction(l, "curse_assimilation"));

        // Add annihilation reactions for each 'counteracting' pair of powers.
        // Imagine the base powers to be arranged in a hexagon, numbered clockwise. The opposites are counteracting.
        REACTIONS.add(new AnnihilationReaction(l, "annihilation0v3", BASE_POWER_LIST.get(0), BASE_POWER_LIST.get(3)));
        REACTIONS.add(new AnnihilationReaction(l, "annihilation1v4", BASE_POWER_LIST.get(1), BASE_POWER_LIST.get(4)));
        REACTIONS.add(new AnnihilationReaction(l, "annihilation2v5", BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(5)));

        // Add synthesis reactions for the three esoteric powers.
        REACTIONS.add(new SynthesisReaction(l, "x_synthesis", Powers.X_POWER.get(), BASE_POWER_LIST.get(0), BASE_POWER_LIST.get(1)));
        REACTIONS.add(new SynthesisReaction(l, "y_synthesis", Powers.Y_POWER.get(), BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(3)));
        REACTIONS.add(new SynthesisReaction(l, "z_synthesis", Powers.Z_POWER.get(), BASE_POWER_LIST.get(4), BASE_POWER_LIST.get(5)));

        // Special reactions.
        REACTIONS.add(new BlazeBurnReaction(l, "blaze_burn"));

        // Add effect reactions to do crazy things.
        REACTIONS.add(new EffectReaction(l, "vortex_effect", ReactionEffects::vortex, Powers.CURSE_POWER.get()));
        REACTIONS.add(new EffectReaction(l, "formation_effect", ReactionEffects::formation, Powers.X_POWER.get()));
        REACTIONS.add(new EffectReaction(l, "luminescence_effect", ReactionEffects::luminescence, Powers.LIGHT_POWER.get()));
        REACTIONS.add(new EffectReaction(l, "smoke_bomb_effect", ReactionEffects::sicklySmoke));
        REACTIONS.add(new EffectReaction(l, "discharge_effect_1", ReactionEffects::discharge));
        REACTIONS.add(new EffectReaction(l, "discharge_effect_2", ReactionEffects::discharge));

        initialized = true;
        return REACTIONS;
    }

    @SubscribeEvent
    public void worldUnload(LevelEvent.Unload event){
        initialized = false;
        REACTIONS.clear();
    }
}
