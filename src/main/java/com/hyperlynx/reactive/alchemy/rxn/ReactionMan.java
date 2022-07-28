package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import com.mojang.authlib.BaseAuthenticationService;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.checkerframework.checker.units.qual.C;

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
        BASE_POWER_LIST.add(Registration.BLAZE_POWER.get());
        BASE_POWER_LIST.add(Registration.WARP_POWER.get());
        BASE_POWER_LIST.add(Registration.SOUL_POWER.get());
        BASE_POWER_LIST.add(Registration.LIGHT_POWER.get());
        BASE_POWER_LIST.add(Registration.VITAL_POWER.get());
        BASE_POWER_LIST.add(Registration.MIND_POWER.get());
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
        REACTIONS.add(new SynthesisReaction(l, "x_synthesis", Registration.X_POWER.get(), BASE_POWER_LIST.get(0), BASE_POWER_LIST.get(1)));
        REACTIONS.add(new SynthesisReaction(l, "y_synthesis", Registration.Y_POWER.get(), BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(3)));
        REACTIONS.add(new SynthesisReaction(l, "z_synthesis", Registration.Z_POWER.get(), BASE_POWER_LIST.get(4), BASE_POWER_LIST.get(5)));

        initialized = true;

        return REACTIONS;
    }

    private Function<CrucibleBlockEntity, CrucibleBlockEntity> chooseEffect(Level l, String alias){
        if(WorldSpecificValue.get(l, alias+"effect_roll", 0, 100) >= EFFECT_CHANCE_PERCENT){
            return null; // Don't provide a function unless you roll the lucky chance.
        }

        int choice = WorldSpecificValue.get(l, alias+"effect", 0, 0);
        if(choice == 0){
            return ReactionEffects::discharge;
        }
        return null;
    }

    @SubscribeEvent
    public void worldUnload(LevelEvent.Unload event){
        initialized = false;
        REACTIONS.clear();
    }
}
