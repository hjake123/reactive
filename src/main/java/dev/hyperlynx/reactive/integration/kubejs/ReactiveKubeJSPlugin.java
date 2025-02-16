package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.integration.kubejs.events.EventHandlerCache;
import dev.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
import dev.hyperlynx.reactive.integration.kubejs.net.ReactionPayload;
import dev.hyperlynx.reactive.integration.kubejs.net.ReactionRequestPayload;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
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
                    ReactionMan.addReactions(payload.reaction());
                    ReactiveMod.LOGGER.debug("Received reaction {} from server", payload.reaction().toString());
                }
        );

        registrar.playToServer(
                ReactionRequestPayload.TYPE,
                ReactionRequestPayload.STREAM_CODEC,
                (payload, context) -> fetchCustomReactionsForPlayer((ServerPlayer) context.player())
        );
    }

    private static void fetchCustomReactionsForPlayer(ServerPlayer player){
        ReactiveMod.LOGGER.info("Server is preparing to send custom reactions...");
        for(Reaction reaction : ReactiveMod.REACTION_MAN.getReactions(player.level())){
            if(reaction instanceof CustomReaction custom){
                PacketDistributor.sendToPlayer(player, new ReactionPayload(custom));
                ReactiveMod.LOGGER.debug("Sent reaction {}", custom.toString());
            }
        }
        ReactiveMod.LOGGER.info("Server has sent all custom reactions.");
    }

}
