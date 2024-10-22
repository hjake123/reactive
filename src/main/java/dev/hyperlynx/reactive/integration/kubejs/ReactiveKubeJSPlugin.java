//package dev.hyperlynx.reactive.integration.kubejs;
//
//import dev.hyperlynx.reactive.alchemy.Powers;
//import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
//import dev.hyperlynx.reactive.client.particles.ParticleScribe;
//import dev.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
//import dev.hyperlynx.reactive.util.WorldSpecificValue;
//import dev.latvian.mods.kubejs.event.EventGroupRegistry;
//import dev.latvian.mods.kubejs.plugin.ClassFilter;
//import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
//import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
//import dev.latvian.mods.kubejs.script.BindingRegistry;
//import net.minecraft.core.registries.Registries;
//
//public class ReactiveKubeJSPlugin implements KubeJSPlugin {
//    @Override
//    public void registerBuilderTypes(BuilderTypeRegistry registry){
//        registry.addDefault(Powers.POWER_REGISTRY_KEY, PowerBuilder.class, PowerBuilder::new);
//        registry.addDefault(Registries.TRIGGER_TYPE, FlagTriggerBuilder.class, FlagTriggerBuilder::new);
//    }
//
//    @Override
//    public void registerEvents(EventGroupRegistry registry) {
//        registry.register(EventTransceiver.EVENTS);
//    }
//
//    @Override
//    public void registerClasses(ClassFilter filter) {
//        filter.allow(WorldSpecificValue.class);
//        filter.allow(ParticleScribe.class);
//    }
//
//    @Override
//    public void registerBindings(BindingRegistry bindings) {
//        bindings.add("WorldSpecificValue", WorldSpecificValue.class);
//        bindings.add("ParticleScribe", ParticleScribe.class);
//        bindings.add("ReactionMan", ReactionMan.class);
//    }
//}
