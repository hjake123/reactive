package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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
        var success = REQUEST_BLOCKERS.get(alias).tryAcquire(1, TimeUnit.SECONDS);
        if(!success){
            ReactiveMod.LOGGER.error("Timeout loading contents for the reaction formula for {}", alias);
            return "$(4) Timeout while loading";
        }
        return RESPONSES.get(alias);
    }

    public static void handlePageResponse(ReactionPagePayload payload, IPayloadContext ignored) {
        ReactiveMod.LOGGER.debug("Received response for page about {}", payload.alias());
        RESPONSES.put(payload.alias(), payload.contents());
        if (!REQUEST_BLOCKERS.containsKey(payload.alias())) {
            REQUEST_BLOCKERS.put(payload.alias(), new Semaphore(0, false));
        }
        REQUEST_BLOCKERS.get(payload.alias()).release();
    }
}
