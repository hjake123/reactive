package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ReactionPageFetcher {
    private static final ConcurrentMap<String, Semaphore> REQUEST_BLOCKERS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> RESPONSES = new ConcurrentHashMap<>();

    public static String requestFormulaFor(String alias) throws InterruptedException {
        if(RESPONSES.containsKey(alias)){
            return RESPONSES.get(alias);
        }

        PacketDistributor.sendToServer(new ReactionPageRequestPayload(alias));
        ReactiveMod.LOGGER.debug("Requested formula for {}", alias);

        if(!REQUEST_BLOCKERS.containsKey(alias)){
            REQUEST_BLOCKERS.put(alias, new Semaphore(0, false));
        }

        // Block until we get a response. If we already have one, this should just go.
        var success = REQUEST_BLOCKERS.get(alias).tryAcquire(10000000, TimeUnit.SECONDS);
        if(!success){
            ReactiveMod.LOGGER.error("Timeout loading contents for the reaction formula for {}", alias);
            return "$(4) Timeout while loading";
        }
        return RESPONSES.get(alias);
    }

    public static void handlePageResponse(ReactionPagePayload payload, IPayloadContext context) {
        ReactiveMod.LOGGER.debug("Received response for page about {}", payload.alias());
        RESPONSES.put(payload.alias(), payload.contents());
        if(!REQUEST_BLOCKERS.containsKey(payload.alias())){
            REQUEST_BLOCKERS.put(payload.alias(), new Semaphore(0, false));
        }
        REQUEST_BLOCKERS.get(payload.alias()).release();
    }

//    public record ReactionPagesConfigurationTask(ServerConfigurationPacketListener listener, ServerLevel level) implements ICustomConfigurationTask {
//        public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(ReactiveMod.location("reaction_formulae_config_task"));
//
//        @Override
//        public void run(Consumer<CustomPacketPayload> consumer) {
//
//            for(Reaction reaction : ReactiveMod.REACTION_MAN.getReactions(level)){
//                consumer.accept(new ReactionPagePayload(reaction.getAlias(), ReactionPageServer.makePageFor(level, reaction.getAlias())));
//            }
//            this.listener().finishCurrentTask(TYPE);
//        }
//
//        @Override
//        public Type type() {
//            return TYPE;
//        }
//    }
}
