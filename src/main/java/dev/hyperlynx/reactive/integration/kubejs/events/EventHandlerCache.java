package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin;
import dev.hyperlynx.reactive.integration.kubejs.net.CustomReactionAliasRequest;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.IEventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraftforge.network.PacketDistributor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// Manages caching the event handlers for custom reactions and special cases,
// as well as running those event handlers.
public class EventHandlerCache {
    private final List<IEventHandler> reaction_runners = new ArrayList<>();
    private final List<IEventHandler> server_reaction_tests = new ArrayList<>();

    public void ingestReactionHandlers(){
        EventTransceiver.CUSTOM_REACTION_RUN_EVENT.forEachListener(ScriptType.SERVER, (container) -> {
            reaction_runners.add(container.handler);
        });
        EventTransceiver.CUSTOM_REACTION_TEST_CONDITIONS_EVENT.forEachListener(ScriptType.SERVER, (container) -> {
            server_reaction_tests.add(container.handler);
        });
    }

    public void resetReactionHandlers(){
        reaction_runners.clear();
        server_reaction_tests.clear();
    }

    private EventResult processEvent(EventJS event, List<IEventHandler> handlers){
        for(IEventHandler handler : handlers){
            try {
                handler.onEvent(event);
                return EventResult.PASS;
            } catch (EventExit exit){
                return exit.result;
            }
        }
        return EventResult.PASS;
    }

    public EventResult processServerTestEvent(CustomReactionTickEventJS event){
        return processEvent(event, server_reaction_tests);
    }

    public EventResult processRunEvent(CustomReactionTickEventJS event){
        return processEvent(event, reaction_runners);
    }

    // CLIENT ONLY
    public boolean received_renderers = false;
    private Instant last_request_timestamp;

    public void requestRenderers(){
        if(received_renderers){
            return;
        }

        if(last_request_timestamp == null || last_request_timestamp.isBefore(Instant.now().minus(10, ChronoUnit.SECONDS))){
            ReactiveKubeJSPlugin.LOGGER.info("Requesting KubeJS reaction aliases");
            ReactiveKubeJSPlugin.KUBEJS_INTEGRATION_CHANNEL.send(PacketDistributor.SERVER.noArg(), new CustomReactionAliasRequest());
            last_request_timestamp = Instant.now();
        }
    }

}
