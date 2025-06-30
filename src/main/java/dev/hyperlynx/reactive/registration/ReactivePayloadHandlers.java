package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.client.gui.ScreenOpener;
import dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin;
import dev.hyperlynx.reactive.net.*;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid=ReactiveMod.MODID)
public class ReactivePayloadHandlers {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.configurationToClient(
                WorldSpecificValue.AlchemySeedData.TYPE,
                WorldSpecificValue.AlchemySeedData.STREAM_CODEC,
                new WorldSpecificValue.AlchemySeedPayloadHandler()
        );
        registrar.commonToClient(
                LitmusScreenPayload.TYPE,
                LitmusScreenPayload.STREAM_CODEC,
                (payload, _context) -> {
                    if (FMLLoader.getDist() == Dist.CLIENT) {
                        ScreenOpener.litmus(payload);
                    }
                }
        );
        registrar.commonToClient(
                ReactionStatusPayload.TYPE,
                ReactionStatusPayload.STREAM_CODEC,
                ReactionStatusPayload::handle
        );
        registrar.playToServer(
                ReactionPageRequestPayload.TYPE,
                ReactionPageRequestPayload.STREAM_CODEC,
                ReactionPageServer::handlePageRequest
        );
        registrar.playToServer(
                MaterialDataSyncRequestPayload.TYPE,
                MaterialDataSyncRequestPayload.STREAM_CODEC,
                MaterialDataSyncRequestPayload::handle
        );
        registrar.playToServer(
                MaterialRenamePayload.TYPE,
                MaterialRenamePayload.STREAM_CODEC,
                MaterialRenamePayload::handle
        );
        registrar.commonToClient(
                MaterialRenameScreenPayload.TYPE,
                MaterialRenameScreenPayload.STREAM_CODEC,
                (payload, _context) -> {
                    if (FMLLoader.getDist() == Dist.CLIENT) {
                        ScreenOpener.materialRename(payload);
                    }
                }
        );

        final PayloadRegistrar async_registrar = event.registrar("1").executesOn(HandlerThread.NETWORK);
        async_registrar.commonToClient(
                ReactionPagePayload.TYPE,
                ReactionPagePayload.STREAM_CODEC,
                ReactionPageFetcher::handlePageResponse
        );

        async_registrar.playToClient(
                MaterialDataSyncPayload.TYPE,
                MaterialDataSyncPayload.STREAM_CODEC,
                MaterialDataSyncPayload::handle
        );

        if(ModList.get().isLoaded("kubejs")){
            ReactiveKubeJSPlugin.registerPayloads(registrar);
        }
    }

    @SubscribeEvent
    public static void register(final RegisterConfigurationTasksEvent event) {
        event.register(new WorldSpecificValue.AlchemySeedConfigurationTask(event.getListener()));
    }
}
