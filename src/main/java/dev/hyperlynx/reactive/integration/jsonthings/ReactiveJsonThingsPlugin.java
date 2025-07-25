package dev.hyperlynx.reactive.integration.jsonthings;

import dev.gigaherz.jsonthings.things.parsers.ThingResourceManager;
import dev.gigaherz.jsonthings.things.serializers.FlexItemType;
import net.neoforged.bus.api.IEventBus;

public class ReactiveJsonThingsPlugin {

    @SuppressWarnings("unused")
    public static final FlexItemType<FlexPowerBottleItem> FLEX_POWER_BOTTLE = FlexItemType.register(
            "reactive:power_bottle", (json) -> FlexPowerBottleItem::new
    );

    public static void registerParser(IEventBus bus) {
        ThingResourceManager.instance().registerParser(new PowerParser(bus));
    }

}
