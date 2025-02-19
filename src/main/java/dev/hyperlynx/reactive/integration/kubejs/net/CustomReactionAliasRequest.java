package dev.hyperlynx.reactive.integration.kubejs.net;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

import static dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin.REACTION_EFFECT_CACHE;

public record CustomReactionAliasRequest() {
    public void encoder(FriendlyByteBuf ignored) {}

    public static CustomReactionAliasRequest decoder(FriendlyByteBuf ignored) {
        return new CustomReactionAliasRequest();
    }

    public void handler(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ReactiveKubeJSPlugin.LOGGER.info("A request to send reaction aliases was received");
            if(REACTION_EFFECT_CACHE.reaction_construct_done){
                ReactiveKubeJSPlugin.LOGGER.info("Sending KubeJS reaction aliases as requested");
                ReactiveKubeJSPlugin.KUBEJS_INTEGRATION_CHANNEL.send(PacketDistributor.PLAYER.with(() -> context.get().getSender()),
                        new CustomReactionAliasResponse(ReactiveKubeJSPlugin.CUSTOM_REACTION_ALIASES));
            }
            context.get().setPacketHandled(true);
        });
    }
}
