package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ReactionPageFetcher {
    private static final Map<String, Semaphore> REQUESTS = new HashMap<>();
    private static final Map<String, String> RESPONSES = new HashMap<>();

    public static String requestFormulaFor(String alias) throws InterruptedException {
        REQUESTS.put(alias, new Semaphore(0, true));
        PacketDistributor.sendToServer(new ReactionPageRequestPayload(alias));
        ReactiveMod.LOGGER.debug("Requested formula for {}", alias);

        // Block until we get a response.
        var success = REQUESTS.get(alias).tryAcquire(30000, TimeUnit.SECONDS);
        if(!success){
            ReactiveMod.LOGGER.error("Timeout loading contents for the reaction formula for {}", alias);
        }
        return RESPONSES.get(alias);
    }

    public static void handlePageResponse(ReactionPagePayload payload, IPayloadContext context) {
        RESPONSES.put(payload.alias(), payload.contents());

        // Free the client thread.
        REQUESTS.get(payload.alias()).release();
    }
}
