package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.alchemy.special.DissolveEvent;
import dev.hyperlynx.reactive.alchemy.special.EmptyEvent;
import dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin;
import dev.hyperlynx.reactive.integration.kubejs.net.ReactionEffectResetPayload;
import dev.hyperlynx.reactive.integration.kubejs.net.ReactionRequestPayload;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.packs.repository.Pack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;

public class EventTransceiver {
    public static EventGroup EVENTS = EventGroup.of("ReactiveEvents");
    public static EventHandler CRUCIBLE_DISSOLVE_EVENT = EVENTS.common("dissolveItem", () -> KubeDissolveEvent.class);
    public static EventHandler CRUCIBLE_EMPTY_EVENT = EVENTS.common("emptyCrucible", () -> KubeEmptyEvent.class);
    public static EventHandler REACTION_BUILD_EVENT = EVENTS.common("constructReactions", () -> KubeReactionConstructEvent.class);
    public static EventHandler CUSTOM_REACTION_TEST_CONDITIONS_EVENT = EVENTS.common("checkReaction", () -> CustomReactionTickEvent.class).hasResult();
    public static EventHandler CUSTOM_REACTION_RUN_EVENT = EVENTS.server("runReaction", () -> CustomReactionTickEvent.class);
    public static EventHandler CUSTOM_REACTION_RENDER_EVENT = EVENTS.client("renderReaction", () -> CustomReactionTickEvent.class);

    @SubscribeEvent
    private static void translateDissolveEvent(DissolveEvent event){
        CRUCIBLE_DISSOLVE_EVENT.post(ScriptType.SERVER, new KubeDissolveEvent(event));
    }

    @SubscribeEvent
    private static void translateEmptyEvent(EmptyEvent event){
        CRUCIBLE_EMPTY_EVENT.post(ScriptType.SERVER, new KubeEmptyEvent(event));
    }

    @SubscribeEvent
    private static void translateConstructReactionEvent(ReactionMan.ReactionConstructEvent event){
        if(event.level.isClientSide()){
            ReactiveMod.LOGGER.info("Sending request to the server for KubeJS reactions");
            PacketDistributor.sendToServer(new ReactionRequestPayload());
        } else {
            REACTION_BUILD_EVENT.post(ScriptType.SERVER, new KubeReactionConstructEvent(event));
        }
    }

    @SubscribeEvent
    private static void translateReactionResetEvent(ReactionMan.ReactionResetEvent event){
        ReactiveKubeJSPlugin.REACTIONS.resetReactionHandlers();
        ReactiveKubeJSPlugin.REACTIONS.ingestReactionHandlers();
        PacketDistributor.sendToAllPlayers(new ReactionEffectResetPayload());
    }
}
