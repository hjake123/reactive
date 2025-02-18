package dev.hyperlynx.reactive.integration.pehkui;

import dev.hyperlynx.reactive.ClientRegistration;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.advancements.FlagCriterion;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.FreeEffectReaction;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.datagen.ReactionAdvancementGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid= ReactiveMod.MODID)
public class ReactivePehkuiPlugin {
    private static boolean has_pehkui = false;
    public static void init(FMLCommonSetupEvent evt, boolean has_pehkui) {
        ReactivePehkuiPlugin.has_pehkui = has_pehkui;
        data_init();
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(SIZE_CHANGED));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(SIZE_REVERTED));
    }

    @SubscribeEvent
    public static void setupReactionRenders(FMLClientSetupEvent evt) {
        ClientRegistration.REACTION_RENDERERS.RENDERERS.put("size_grow_effect", ResizeReactionRenders::acid_based);
        ClientRegistration.REACTION_RENDERERS.RENDERERS.put("size_shrink_effect", ResizeReactionRenders::verdant_based);
    }

    public static void data_init() {
        ReactionAdvancementGenerator.add("size_grow_effect");
        ReactionAdvancementGenerator.add("size_shrink_effect");
        ReactionAdvancementGenerator.add("size_revert_effect");
        ReactionAdvancementGenerator.add("size_revert_effect_2");
    }
    protected static final FlagCriterion SIZE_CHANGED = new FlagCriterion(ReactiveMod.location("size_change_criterion"));
    protected static final FlagCriterion SIZE_REVERTED = new FlagCriterion(ReactiveMod.location("size_revert_criterion"));

    @SubscribeEvent
    public static void onReactionConstruct(ReactionMan.ReactionConstructEvent evt){
        if(!has_pehkui)
            return;

        ReactionMan.addReactions(
            new FreeEffectReaction("size_shrink_effect", ResizeReactionEffects::shrink, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.ACID_POWER.get()).setStimulus(Reaction.Stimulus.NO_ELECTRIC),
            new FreeEffectReaction("size_grow_effect", ResizeReactionEffects::grow, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.VERDANT_POWER.get()).setStimulus(Reaction.Stimulus.NO_ELECTRIC),
            new FreeEffectReaction("size_revert_effect", ResizeReactionEffects::revert_from_small, null, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.ACID_POWER.get()).setStimulus(Reaction.Stimulus.ELECTRIC),
            new FreeEffectReaction("size_revert_effect_2", ResizeReactionEffects::revert_from_large, null, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.VERDANT_POWER.get()).setStimulus(Reaction.Stimulus.ELECTRIC)
        );
    }

}
