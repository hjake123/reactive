package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.fx.particles.ParticleScribe;
import dev.hyperlynx.reactive.integration.kubejs.events.EventHandlerCache;
import dev.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
import dev.hyperlynx.reactive.integration.kubejs.net.CustomReactionAliasRequest;
import dev.hyperlynx.reactive.integration.kubejs.net.CustomReactionAliasResponse;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class ReactiveKubeJSPlugin extends KubeJSPlugin {
    public static final Logger LOGGER = LogManager.getLogger("Reactive/KubeJS Integration");
    protected static RegistryInfo<Power> POWER_REGISTRY_INFO;
    public static EventHandlerCache REACTION_EFFECT_CACHE = new EventHandlerCache();
    public static Set<String> CUSTOM_REACTION_ALIASES = new HashSet<>();

    public ReactiveKubeJSPlugin(){
        POWER_REGISTRY_INFO = RegistryInfo.of(Powers.POWERS.getRegistryKey(), Power.class);
    }

    public void init() {
        POWER_REGISTRY_INFO.addType("custom_power", KubePowerBuilder.class, KubePowerBuilder::new);
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

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel KUBEJS_INTEGRATION_CHANNEL = NetworkRegistry.newSimpleChannel(
            ReactiveMod.location("reactive_kubejs_integration"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMessages(){
        KUBEJS_INTEGRATION_CHANNEL.registerMessage(0, CustomReactionAliasRequest.class,
                CustomReactionAliasRequest::encoder,
                CustomReactionAliasRequest::decoder,
                CustomReactionAliasRequest::handler);

        KUBEJS_INTEGRATION_CHANNEL.registerMessage(1, CustomReactionAliasResponse.class,
                CustomReactionAliasResponse::encoder,
                CustomReactionAliasResponse::decoder,
                CustomReactionAliasResponse::handler);
    }
}
