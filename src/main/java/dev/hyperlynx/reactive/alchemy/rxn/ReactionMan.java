package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.advancements.ReactionCriteriaBuilder;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.client.renderers.ReactionRenders;
import dev.hyperlynx.reactive.util.WorldSpecificValue;

import java.util.*;

import net.minecraft.world.item.Items;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

// Uh-oh, looks like ReactionMan's on the hunt!
// This class manages the world's reactions.
// When the level unloads, it resets the cache of reaction.
// When reactions are first asked for (likely by a ticking Crucible BE), it will calculate them.
// The list should be identical each time the world loads.

public class ReactionMan {
    static boolean initialized = false;
    static boolean initializer_lock = false; // Prevent multiple things trying to initialize reactions at once.
    private static final ReactionMap REACTIONS = new ReactionMap();
    public static ArrayList<Power> BASE_POWER_LIST = new ArrayList<>();
    public static ReactionCriteriaBuilder CRITERIA_BUILDER = new ReactionCriteriaBuilder();

    public ReactionMan(){
        CRITERIA_BUILDER.add("curse_assimilation");
        CRITERIA_BUILDER.add("vital_kill");
        CRITERIA_BUILDER.add("vital_eat");
        CRITERIA_BUILDER.add("verdant_consume");
        CRITERIA_BUILDER.add("vital_consume");
        CRITERIA_BUILDER.add("verdant_growth");
        CRITERIA_BUILDER.add("vital_growth");
        CRITERIA_BUILDER.add("discharge_annihilation");
        CRITERIA_BUILDER.add("smoke_annihilation");
        CRITERIA_BUILDER.add("salt_annihilation");
        CRITERIA_BUILDER.add("x_synthesis");
        CRITERIA_BUILDER.add("y_synthesis");
        CRITERIA_BUILDER.add("z_synthesis");
        CRITERIA_BUILDER.add("growth");
        CRITERIA_BUILDER.add("flames");
        CRITERIA_BUILDER.add("levitation");
        CRITERIA_BUILDER.add("sunlight");
        CRITERIA_BUILDER.add("immobilize");
        CRITERIA_BUILDER.add("soul_to_warp");
        CRITERIA_BUILDER.add("warp_to_soul");
        CRITERIA_BUILDER.add("compound_degradation");
        CRITERIA_BUILDER.add("explosion_effect");
        CRITERIA_BUILDER.add("formation_effect");
        CRITERIA_BUILDER.add("block_fall_effect");
        CRITERIA_BUILDER.add("slowfall_effect");
        CRITERIA_BUILDER.add("astral_synthesis");
        CRITERIA_BUILDER.add("astral");
        CRITERIA_BUILDER.add("astral_curse_annihilation");
        CRITERIA_BUILDER.add("size_grow_effect");
        CRITERIA_BUILDER.add("size_shrink_effect");
        CRITERIA_BUILDER.add("size_revert_effect");
        CRITERIA_BUILDER.add("size_revert_effect_2");
        CRITERIA_BUILDER.add("ominous_transformation");
        CRITERIA_BUILDER.add("omen_settling");
        CRITERIA_BUILDER.add("wind_bomb");
        CRITERIA_BUILDER.add("lightning");
        CRITERIA_BUILDER.add("chomp");
    }

    public List<Reaction> getReactions(){
        if(!initialized){
            constructReactions();
        }
        return REACTIONS.values().stream().toList();
    }

    public List<String> getReactionAliases(){
        if(!initialized){
            constructReactions();
        }
        return REACTIONS.keySet().stream().toList();
    }

    public Reaction get(String alias){
        if(!initialized){
            constructReactions();
        }
        return REACTIONS.get(alias);
    }

    // Creates, from scratch, a set of all possible reactions that can be done in the world.
    private void constructReactions(){
        if(initializer_lock)
            return;
        initializer_lock = true;

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

        int vvr = WorldSpecificValues.VERDANT_VITAL_RELATIONSHIP.get();
        switch (vvr) {
            case 2 ->
                    REACTIONS.add(new AssimilationReaction("verdant_consume", Powers.VERDANT_POWER.get(), Powers.VITAL_POWER.get()));
            case 3 ->
                    REACTIONS.add(new AssimilationReaction("vital_consume", Powers.VITAL_POWER.get(), Powers.VERDANT_POWER.get()));
            case 4 ->
                    REACTIONS.add(new SynthesisReaction("verdant_growth", Powers.VERDANT_POWER.get(), Powers.VITAL_POWER.get(), Powers.BODY_POWER.get()));
            case 5 ->
                    REACTIONS.add(new SynthesisReaction("vital_growth", Powers.VITAL_POWER.get(), Powers.VERDANT_POWER.get(), Powers.LIGHT_POWER.get()));
        }

        // Add annihilation reactions for each 'counteracting' pair of powers.
        // Imagine the base powers to be arranged in a hexagon, numbered clockwise. The opposites are counteracting.
        REACTIONS.add(new AnnihilationReaction("discharge_annihilation", BASE_POWER_LIST.get(0), BASE_POWER_LIST.get(3), ReactionEffects::discharge, null));
        REACTIONS.add(new AnnihilationReaction("smoke_annihilation", BASE_POWER_LIST.get(1), BASE_POWER_LIST.get(4), ReactionEffects::smoke, ReactionRenders::smoke));
        REACTIONS.add(new AnnihilationReaction("salt_annihilation", BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(5), ReactionEffects::salt, null));

        // Add synthesis reactions for the three esoteric powers.
        REACTIONS.add(new BasePowerSynthesisReaction("x_synthesis", Powers.X_POWER.get(), BASE_POWER_LIST.get(0), BASE_POWER_LIST.get(1))
                .setStimulus(Reaction.Stimulus.ELECTRIC));
        REACTIONS.add(new BasePowerSynthesisReaction("y_synthesis", Powers.Y_POWER.get(), BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(3))
                .setStimulus(Reaction.Stimulus.ELECTRIC));
        REACTIONS.add(new BasePowerSynthesisReaction("z_synthesis", Powers.Z_POWER.get(), BASE_POWER_LIST.get(4), BASE_POWER_LIST.get(5))
                .setStimulus(Reaction.Stimulus.ELECTRIC));

        // Add effect reactions to do crazy things.
        REACTIONS.add(new EffectReaction("growth", ReactionEffects::growth, ReactionRenders::growth, Powers.VERDANT_POWER.get(), Powers.MIND_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
        REACTIONS.add(new FreeEffectReaction("flames", ReactionEffects::flamethrower, ReactionRenders::flamethrower, Powers.BLAZE_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
        REACTIONS.add(new FreeEffectReaction("levitation", ReactionEffects::levitation, null, Powers.LIGHT_POWER.get()).setStimulus(Reaction.Stimulus.END_CRYSTAL));
        REACTIONS.add(new EffectReaction("sunlight", ReactionEffects::sunlight, null, Powers.LIGHT_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
        REACTIONS.add(new EffectReaction("immobilize", ReactionEffects::immobilize, null, Powers.WARP_POWER.get(), Powers.VERDANT_POWER.get()).setStimulus(Reaction.Stimulus.NO_ELECTRIC));


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
                REACTIONS.add(new EffectReaction("explosion_effect", ReactionEffects::explosion, null, Powers.X_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL).markAlwaysPerfect());
                REACTIONS.add(new EffectReaction("formation_effect", ReactionEffects::foaming, null, Powers.Y_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL).markAlwaysPerfect());
                REACTIONS.add(new EffectReaction("block_fall_effect", ReactionEffects::blockfall, null, Powers.Z_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL).markAlwaysPerfect());
                REACTIONS.add(new CatalystEffectReaction("slowfall_effect", ReactionEffects::slowfall, null, Powers.Z_POWER.get(), Registration.PHANTOM_RESIDUE.get()).markAlwaysPerfect());
            }
            case 2 -> {
                REACTIONS.add(new DecomposeReaction("compound_degradation", Powers.Y_POWER.get(), BASE_POWER_LIST.get(2), BASE_POWER_LIST.get(3)).setStimulus(Reaction.Stimulus.NO_ELECTRIC).markAlwaysPerfect());
                REACTIONS.add(new EffectReaction("explosion_effect", ReactionEffects::explosion, null, Powers.Y_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL).markAlwaysPerfect());
                REACTIONS.add(new EffectReaction("formation_effect", ReactionEffects::foaming, null, Powers.Z_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL).markAlwaysPerfect());
                REACTIONS.add(new EffectReaction("block_fall_effect", ReactionEffects::blockfall, null, Powers.X_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL).markAlwaysPerfect());
                REACTIONS.add(new CatalystEffectReaction("slowfall_effect", ReactionEffects::slowfall, null, Powers.X_POWER.get(), Registration.PHANTOM_RESIDUE.get()).markAlwaysPerfect());
            }
            case 3 -> {
                REACTIONS.add(new DecomposeReaction("compound_degradation", Powers.Z_POWER.get(), BASE_POWER_LIST.get(4), BASE_POWER_LIST.get(5)).setStimulus(Reaction.Stimulus.NO_ELECTRIC).markAlwaysPerfect());
                REACTIONS.add(new EffectReaction("explosion_effect", ReactionEffects::explosion, null, Powers.Z_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL).markAlwaysPerfect());
                REACTIONS.add(new EffectReaction("formation_effect", ReactionEffects::foaming, null, Powers.X_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL).markAlwaysPerfect());
                REACTIONS.add(new EffectReaction("block_fall_effect", ReactionEffects::blockfall, null, Powers.Y_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL).markAlwaysPerfect());
                REACTIONS.add(new CatalystEffectReaction("slowfall_effect", ReactionEffects::slowfall, null, Powers.Y_POWER.get(), Registration.PHANTOM_RESIDUE.get()).markAlwaysPerfect());
            }
        }

        REACTIONS.add(new AstralSynthesisReaction("astral_synthesis", Powers.ASTRAL_POWER.get(), Powers.X_POWER.get(), Powers.Y_POWER.get(), Powers.Z_POWER.get()).markAlwaysPerfect());
        REACTIONS.add(new AstralReaction("astral"));
        REACTIONS.add(new AnnihilationReaction("astral_curse_annihilation", Powers.ASTRAL_POWER.get(), Powers.CURSE_POWER.get(), ReactionEffects::creation, ReactionRenders::creation));

        Reaction size_shrink_effect = new FreeEffectReaction("size_shrink_effect", ReactionEffects::shrink, ReactionRenders::acid_based, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.ACID_POWER.get()).setStimulus(Reaction.Stimulus.NO_ELECTRIC);
        Reaction size_grow_effect = new FreeEffectReaction("size_grow_effect", ReactionEffects::grow, ReactionRenders::verdant_based, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.VERDANT_POWER.get()).setStimulus(Reaction.Stimulus.NO_ELECTRIC);
        Reaction size_revert_effect = new FreeEffectReaction("size_revert_effect", ReactionEffects::revert_from_small, null, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.ACID_POWER.get()).setStimulus(Reaction.Stimulus.ELECTRIC);
        size_revert_effect.cloneReagentsOf(size_shrink_effect);
        Reaction size_revert_effect_2 = new FreeEffectReaction("size_revert_effect_2", ReactionEffects::revert_from_large, null, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.VERDANT_POWER.get()).setStimulus(Reaction.Stimulus.ELECTRIC);
        size_revert_effect_2.cloneReagentsOf(size_grow_effect);

        REACTIONS.addAll(size_shrink_effect, size_grow_effect, size_revert_effect, size_revert_effect_2);

        REACTIONS.add(new OmenConversionReaction("ominous_transformation"));
        REACTIONS.add(new FreeEffectReaction("omen_settling", ReactionEffects::omenSettling, ReactionRenders::ominous, Powers.OMEN_POWER.get()).setStimulus(Reaction.Stimulus.GOLD_SYMBOL));
        REACTIONS.add(new CatalystEffectReaction("chomp", ReactionEffects::chomp, null, Powers.OMEN_POWER.get(), Items.IRON_INGOT));

        REACTIONS.add(new WindBombReaction("wind_bomb"));
        REACTIONS.add(new EffectReaction("lightning", ReactionEffects::lightning, null, Powers.FLOW_POWER.get(), Powers.LIGHT_POWER.get()).setStimulus(Reaction.Stimulus.ELECTRIC));

        NeoForge.EVENT_BUS.post(new ReactionConstructEvent());

        initialized = true;
        initializer_lock = false;
    }

    public static void addReactions(Reaction... additions){
        REACTIONS.addAll(additions);
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

    /**
     * This event is fired after ReactionMan constructs the world's reactions.
     * You can add new reactions using ReactionMan.addReactions(). <br><br>
     * You will need to also register the reaction's alias to ReactionCriteriaBuilder.add() at class load
     * if you want reaction advancements and their data gen to work.
     */
    public static class ReactionConstructEvent extends Event {

    }

    protected static class ReactionMap extends HashMap<String, Reaction> {
        public Reaction add(Reaction reaction) {
            String alias = reaction.alias;
            if(ConfigMan.SERVER.disabledReactions.get().contains(alias)){
                return null;
            }
            return super.put(reaction.alias, reaction);
        }

        public void addAll(Reaction... reactions){
            for(Reaction reaction : reactions){
                add(reaction);
            }
        }
    }
}
