package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.client.gui.LitmusScreenOpener;
import dev.hyperlynx.reactive.client.gui.LitmusScreenPayload;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.integration.kubejs.events.EventHandlerCache;
import dev.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ReactiveKubeJSPlugin implements KubeJSPlugin {
    public static EventHandlerCache REACTIONS = new EventHandlerCache();

    @Override
    public void init() {

    }

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry){
        registry.addDefault(Powers.POWER_REGISTRY_KEY, PowerBuilder.class, PowerBuilder::new);
        registry.addDefault(Registries.TRIGGER_TYPE, FlagTriggerBuilder.class, FlagTriggerBuilder::new);
        registry.of(Registries.ITEM, reg -> {
            reg.add("reactive:power_bottle", CustomPowerBottleItem.Builder.class, CustomPowerBottleItem.Builder::new);
        });
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(EventTransceiver.EVENTS);
    }

    @Override
    public void registerClasses(ClassFilter filter) {
        filter.allow(WorldSpecificValue.class);
        filter.allow(ParticleScribe.class);
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("WorldSpecificValue", WorldSpecificValue.class);
        bindings.add("ParticleScribe", ParticleScribe.class);
        bindings.add("ReactionMan", ReactionMan.class);
    }

    public static void registerPayloads(PayloadRegistrar registrar) {
        registrar.playToClient(
                ReactionPayload.TYPE,
                ReactionPayload.STREAM_CODEC,
                (payload, _context) -> {
                    if (FMLLoader.getDist() == Dist.CLIENT) {
                        ReactionMan.addReactions(payload.reaction());
                    }
                }
        );
    }

}
