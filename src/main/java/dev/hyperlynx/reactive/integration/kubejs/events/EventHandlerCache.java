package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.IEventHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;

import java.util.ArrayList;
import java.util.List;

// Manages caching the event handlers for custom reactions and special cases,
// as well as running those event handlers.
public class EventHandlerCache {
    private final List<IEventHandler> reaction_runners = new ArrayList<>();
    private final List<IEventHandler> reaction_renderers = new ArrayList<>();
    private final List<IEventHandler> server_reaction_tests = new ArrayList<>();
    private final List<IEventHandler> client_reaction_tests = new ArrayList<>();

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
        EventTransceiver.CUSTOM_REACTION_TEST_CONDITIONS_EVENT.forEachListener(ScriptType.CLIENT, (container) -> {
            client_reaction_tests.add(container.handler);
        });
    }

    public void resetReactionHandlers(){
        reaction_renderers.clear();
        reaction_runners.clear();
        server_reaction_tests.clear();
        client_reaction_tests.clear();
    }

    private EventResult processEvent(KubeEvent event, List<IEventHandler> handlers){
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

    public EventResult processClientTestEvent(CustomReactionTickEvent event){
        return processEvent(event, client_reaction_tests);
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

}
