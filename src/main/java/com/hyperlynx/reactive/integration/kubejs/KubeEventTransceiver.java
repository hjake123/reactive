package com.hyperlynx.reactive.integration.kubejs;

import com.hyperlynx.reactive.alchemy.special.DissolveEvent;
import com.hyperlynx.reactive.alchemy.special.EmptyEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import net.neoforged.bus.api.SubscribeEvent;

public class KubeEventTransceiver {
    protected static EventGroup EVENTS = EventGroup.of("ReactiveEvents");
    public static EventHandler CRUCIBLE_DISSOLVE_EVENT = KubeEventTransceiver.EVENTS.common("dissolveItem", () -> KubeDissolveEvent.class);
    public static EventHandler CRUCIBLE_EMPTY_EVENT = KubeEventTransceiver.EVENTS.common("emptyCrucible", () -> KubeEmptyEvent.class);

    @SubscribeEvent
    private static void translateDissolveEvent(DissolveEvent event){
        CRUCIBLE_DISSOLVE_EVENT.post(ScriptType.SERVER, new KubeDissolveEvent(event));
    }

    @SubscribeEvent
    private static void translateEmptyEvent(EmptyEvent event){
        CRUCIBLE_EMPTY_EVENT.post(ScriptType.SERVER, new KubeEmptyEvent(event));
    }
}
