package dev.hyperlynx.reactive.integration.kubejs.net;

import dev.hyperlynx.reactive.ClientRegistration;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.integration.kubejs.CustomReaction;
import dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin.REACTION_EFFECT_CACHE;

public record CustomReactionAliasResponse(Set<String> aliases) {
    public void encoder(FriendlyByteBuf buffer) {
        buffer.writeCollection(aliases, FriendlyByteBuf::writeUtf);
    }

    public static CustomReactionAliasResponse decoder(FriendlyByteBuf buffer) {
        return new CustomReactionAliasResponse(buffer.readCollection(HashSet::new, FriendlyByteBuf::readUtf));
    }

    public void handler(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ReactiveKubeJSPlugin.LOGGER.info("Received KubeJS reactions, registering them to renderer");
            REACTION_EFFECT_CACHE.received_renderers = true;
            for(String alias : aliases){
                ReactiveMod.LOGGER.debug("-> {}", alias);
                ClientRegistration.REACTION_RENDERERS.RENDERERS.put(alias, CustomReaction.getRenderFunction(alias));
            }
        });
    }
}
