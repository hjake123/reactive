package dev.hyperlynx.reactive.integration.jsonthings;

import dev.gigaherz.jsonthings.things.parsers.ThingResourceManager;
import net.neoforged.bus.api.IEventBus;

public class ReactiveJsonThingsPlugin {

    public static void registerParser(IEventBus bus) {
        ThingResourceManager.instance().registerParser(new PowerParser(bus));
    }

}
