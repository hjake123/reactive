package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.fx.particles.ParticleScribe;
import dev.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;

public class ReactiveKubeJSPlugin extends KubeJSPlugin {
    protected static RegistryInfo<Power> POWER_REGISTRY_INFO;

    public ReactiveKubeJSPlugin(){
        POWER_REGISTRY_INFO = RegistryInfo.of(Powers.POWERS.getRegistryKey(), Power.class);
    }

    public void init() {
        POWER_REGISTRY_INFO.addType("custom_power", PowerBuilder.class, PowerBuilder::new);
        RegistryInfo.ITEM.addType("reactive:power_bottle", CustomPowerBottleItem.Builder.class, CustomPowerBottleItem.Builder::new);
    }

    @Override
    public void registerEvents() {
        EventTransceiver.EVENTS.register();
    }

    @Override
    public void registerClasses(ScriptType type, ClassFilter filter) {
        filter.allow(WorldSpecificValue.class);
        filter.allow(ParticleScribe.class);
    }

    @Override
    public void registerBindings(BindingsEvent bindings) {
        bindings.add("WorldSpecificValue", WorldSpecificValue.class);
        bindings.add("ParticleScribe", ParticleScribe.class);
        bindings.add("ReactionMan", ReactionMan.class);
    }
}
