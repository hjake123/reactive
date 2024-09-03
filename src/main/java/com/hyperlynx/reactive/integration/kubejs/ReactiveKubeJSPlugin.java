package com.hyperlynx.reactive.integration.kubejs;

import com.hyperlynx.reactive.alchemy.Powers;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;

public class ReactiveKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry){
        registry.addDefault(Powers.POWER_REGISTRY_KEY, PowerBuilder.class, PowerBuilder::new);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(KubeEventTransceiver.EVENTS);
    }


}
