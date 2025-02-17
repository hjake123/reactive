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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ReactiveKubeJSPlugin implements KubeJSPlugin {
    public static EventHandlerCache REACTION_EFFECT_CACHE = new EventHandlerCache();
    public static Set<String> CUSTOM_REACTION_ALIASES = new HashSet<>();

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
            registrar.commonToClient(
                ReactionAliasPayload.TYPE,
                ReactionAliasPayload.STREAM_CODEC,
                (payload, _context) -> {
                    for(String alias : payload.aliases()){
                        ReactiveClientMod.REACTION_RENDERERS.RENDERERS.put(alias, CustomReaction.getRenderFunction(alias));
                    }
                }
            );
    }

    protected record ReactionAliasPayload(Set<String> aliases) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ReactionAliasPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("kubejs_reaction_alias"));
        public static final StreamCodec<? super FriendlyByteBuf, ReactionAliasPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8), ReactionAliasPayload::aliases,
                ReactionAliasPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void registerConfigurationTasks(final RegisterConfigurationTasksEvent event) {
        event.register(new CustomReactionSyncConfigTask(event.getListener()));
    }

    private record CustomReactionSyncConfigTask(ServerConfigurationPacketListener listener) implements ICustomConfigurationTask {
        public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(ReactiveMod.location("kubejs_reaction_sync_task"));

        @Override
        public void run(Consumer<CustomPacketPayload> sender) {
            sender.accept(new ReactionAliasPayload(CUSTOM_REACTION_ALIASES));
            this.listener().finishCurrentTask(this.type());
        }

        @Override
        public Type type() {
            return TYPE;
        }
    }
}
