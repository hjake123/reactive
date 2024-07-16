package com.hyperlynx.reactive.integration.pehkui;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.advancements.FlagTrigger;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.rxn.FreeEffectReaction;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid= ReactiveMod.MODID)
public class ReactivePehkuiPlugin {
    private static boolean has_pehkui = false;
    public static void init(FMLCommonSetupEvent evt, boolean has_pehkui) {
        ReactivePehkuiPlugin.has_pehkui = has_pehkui;
        data_init();
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(SIZE_CHANGED));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(SIZE_REVERTED));
    }
    public static void data_init() {
        ReactionMan.CRITERIA_BUILDER.add("size_grow_effect");
        ReactionMan.CRITERIA_BUILDER.add("size_shrink_effect");
        ReactionMan.CRITERIA_BUILDER.add("size_revert_effect");
        ReactionMan.CRITERIA_BUILDER.add("size_revert_effect_2");
    }
    protected static final FlagTrigger SIZE_CHANGED = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "size_change_criterion"));
    protected static final FlagTrigger SIZE_REVERTED = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "size_revert_criterion"));

    @SubscribeEvent
    public static void onReactionConstruct(ReactionMan.ReactionConstructEvent evt){
        if(!has_pehkui)
            return;

        ReactionMan.addReactions(
            new FreeEffectReaction("size_shrink_effect", ResizeReactionEffects::shrink, ResizeReactionRenders::acid_based, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.ACID_POWER.get()).setStimulus(Reaction.Stimulus.NO_ELECTRIC),
            new FreeEffectReaction("size_grow_effect", ResizeReactionEffects::grow, ResizeReactionRenders::verdant_based, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.VERDANT_POWER.get()).setStimulus(Reaction.Stimulus.NO_ELECTRIC),
            new FreeEffectReaction("size_revert_effect", ResizeReactionEffects::revert_from_small, null, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.ACID_POWER.get()).setStimulus(Reaction.Stimulus.ELECTRIC),
            new FreeEffectReaction("size_revert_effect_2", ResizeReactionEffects::revert_from_large, null, Powers.MIND_POWER.get(), Powers.BODY_POWER.get(), Powers.VERDANT_POWER.get()).setStimulus(Reaction.Stimulus.ELECTRIC)
        );
    }

}
