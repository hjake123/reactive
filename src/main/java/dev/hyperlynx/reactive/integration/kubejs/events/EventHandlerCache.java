package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.IEventHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.WrappedException;
import net.neoforged.neoforge.network.PacketDistributor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// Manages caching the event handlers for custom reactions and special cases,
// as well as running those event handlers.
public class EventHandlerCache {
    private final List<IEventHandler> reaction_runners = new ArrayList<>();
    private final List<IEventHandler> reaction_renderers = new ArrayList<>();
    private final List<IEventHandler> server_reaction_tests = new ArrayList<>();

    public void ingestReactionHandlers(){
        EventTransceiver.CUSTOM_REACTION_RUN_EVENT.forEachListener(ScriptType.SERVER, (container) -> {
            reaction_runners.add(container.handler);
        });
        EventTransceiver.CUSTOM_REACTION_RENDER_EVENT.forEachListener(ScriptType.CLIENT, (container) -> {
            reaction_renderers.add(container.handler);
        });
        EventTransceiver.CUSTOM_REACTION_TEST_CONDITIONS_EVENT.forEachListener(ScriptType.SERVER, (container) -> {
            server_reaction_tests.add(container.handler);
        });
        this.reaction_construct_done = true;
    }

    public void resetReactionHandlers(){
        reaction_renderers.clear();
        reaction_runners.clear();
        server_reaction_tests.clear();
    }

    private EventResult processEvent(KubeEvent event, List<IEventHandler> handlers) {
        for(IEventHandler handler : handlers){
            try {
                handler.onEvent(event);
                return EventResult.PASS;
            } catch (EventExit exit){
                return exit.result;
            } catch (WrappedException exception){
                if(exception.getWrappedException() instanceof EventExit exit){
                    return exit.result;
                }
                throw exception;
            }
        }
        return EventResult.PASS;
    }

    public EventResult processServerTestEvent(CustomReactionTickEvent event){
        return processEvent(event, server_reaction_tests);
    }

    public EventResult processRunEvent(CustomReactionTickEvent event){
        return processEvent(event, reaction_runners);
    }

    public EventResult processRenderEvent(CustomReactionTickEvent event){
        return processEvent(event, reaction_renderers);
    }

    // SERVER ONLY
    public boolean reaction_construct_done = false;

    // CLIENT ONLY
    public boolean received_renderers = false;
    private Instant last_request_timestamp;

    public void requestRenderers(){
        if(received_renderers){
            return;
        }

        if(last_request_timestamp == null || last_request_timestamp.isBefore(Instant.now().minus(10, ChronoUnit.SECONDS))){
            ReactiveMod.LOGGER.info("Requesting KubeJS reaction aliases");
            PacketDistributor.sendToServer(new ReactiveKubeJSPlugin.ReactionAliasRequestPayload());
            last_request_timestamp = Instant.now();
        }
    }
}
