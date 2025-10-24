package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.material.BuiltInMaterials;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.alchemy.special.DissolveEvent;
import dev.hyperlynx.reactive.alchemy.special.EmptyEvent;
import dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventTransceiver {
    public static EventGroup EVENTS = EventGroup.of("ReactiveEvents");
    public static EventHandler CRUCIBLE_DISSOLVE_EVENT = EVENTS.common("dissolveItem", () -> DissolveEventJS.class);
    public static EventHandler CRUCIBLE_EMPTY_EVENT = EVENTS.common("emptyCrucible", () -> EmptyEventJS.class);
    public static EventHandler REACTION_BUILD_EVENT = EVENTS.common("constructReactions", () -> ReactionConstructEventJS.class);
    public static EventHandler CUSTOM_REACTION_TEST_CONDITIONS_EVENT = EVENTS.common("checkReaction", () -> CustomReactionTickEventJS.class).hasResult();
    public static EventHandler CUSTOM_REACTION_RUN_EVENT = EVENTS.server("runReaction", () -> CustomReactionTickEventJS.class);
    public static EventHandler CUSTOM_REACTION_RENDER_EVENT = EVENTS.client("renderReaction", () -> CustomReactionTickEventJS.class);
    public static EventHandler BUILT_IN_MATERIAL_EVENT = EVENTS.common("defineMaterials", () -> BuiltInMaterialEventJS.class);

    @SubscribeEvent
    public static void translateDissolveEvent(DissolveEvent event){
        CRUCIBLE_DISSOLVE_EVENT.post(ScriptType.SERVER, new DissolveEventJS(event));
    }

    @SubscribeEvent
    public static void translateEmptyEvent(EmptyEvent event){
        CRUCIBLE_EMPTY_EVENT.post(ScriptType.SERVER, new EmptyEventJS(event));
    }

    @SubscribeEvent
    public static void translateConstructReactionEvent(ReactionMan.ReactionConstructEvent event){
        REACTION_BUILD_EVENT.post(ScriptType.SERVER, new ReactionConstructEventJS(event));
    }

    @SubscribeEvent
    public static void translateReactionResetEvent(ReactionMan.ReactionResetEvent event){
        ReactiveKubeJSPlugin.REACTION_EFFECT_CACHE.resetReactionHandlers();
    }

    @SubscribeEvent
    public static void translateBuiltInMaterialEvent(BuiltInMaterials.BuiltInMaterialEvent event){
        BUILT_IN_MATERIAL_EVENT.post(ScriptType.SERVER, new BuiltInMaterialEventJS(event));
    }
}
