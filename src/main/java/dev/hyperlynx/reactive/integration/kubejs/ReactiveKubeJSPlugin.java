package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.client.ReactiveClientMod;
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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ReactiveKubeJSPlugin implements KubeJSPlugin {
    public static final Logger LOGGER = LogManager.getLogger("Reactive/KubeJS Integration");

    public static final EventHandlerCache REACTION_EFFECT_CACHE = new EventHandlerCache();
    public static final Set<String> CUSTOM_REACTION_ALIASES = new HashSet<>();

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry){
        registry.addDefault(Powers.POWER_REGISTRY_KEY, KubePowerBuilder.class, KubePowerBuilder::new);
        registry.addDefault(Registries.TRIGGER_TYPE, FlagTriggerBuilder.class, FlagTriggerBuilder::new);
        registry.of(Registries.ITEM, reg -> reg.add(ReactiveMod.location("power_bottle"), CustomPowerBottleItem.Builder.class, CustomPowerBottleItem.Builder::new));
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
            registrar.commonToClient(
                ReactionAliasPayload.TYPE,
                ReactionAliasPayload.STREAM_CODEC,
                (payload, _context) -> {
                    ReactiveKubeJSPlugin.LOGGER.debug("Received KubeJS reactions, registering them to renderer");
                    REACTION_EFFECT_CACHE.received_renderers = true;
                    for(String alias : payload.aliases()){
                        ReactiveMod.LOGGER.debug("-> {}", alias);
                        ReactiveClientMod.REACTION_RENDERERS.RENDERERS.put(alias, CustomReaction.getRenderFunction(alias));
                    }
                }
            );

        registrar.playToServer(
                ReactionAliasRequestPayload.TYPE,
                ReactionAliasRequestPayload.STREAM_CODEC,
                (payload, _context) -> {
                    ReactiveKubeJSPlugin.LOGGER.info("Sending KubeJS reaction aliases as requested");
                    PacketDistributor.sendToAllPlayers(new ReactionAliasPayload(CUSTOM_REACTION_ALIASES));
                }
        );
    }

    public record ReactionAliasPayload(Set<String> aliases) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ReactionAliasPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("kubejs_reaction_aliases"));
        public static final StreamCodec<? super FriendlyByteBuf, ReactionAliasPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8), ReactionAliasPayload::aliases,
                ReactionAliasPayload::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ReactionAliasRequestPayload() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ReactionAliasRequestPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("kubejs_request_reaction_aliases"));
        public static final StreamCodec<? super FriendlyByteBuf, ReactionAliasRequestPayload> STREAM_CODEC =
                StreamCodec.unit(new ReactionAliasRequestPayload());

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
