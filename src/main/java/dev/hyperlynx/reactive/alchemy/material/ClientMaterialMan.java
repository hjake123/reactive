package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.net.MaterialDataSyncRequestPayload;
import dev.hyperlynx.reactive.net.MaterialRenamePayload;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ClientMaterialMan {
    public static AtomicReference<MaterialData> clientside_data = new AtomicReference<>(MaterialData.empty());
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final Semaphore response_ready = new Semaphore(0, false);

    public static MaterialData data() {
        if(initialized.get()) {
            return clientside_data.get();
        }
        ReactiveMod.LOGGER.debug("Requesting material definitions from server");
        try {
            PacketDistributor.sendToServer(new MaterialDataSyncRequestPayload(-1));
            boolean got_result = response_ready.tryAcquire(1, 1, TimeUnit.SECONDS);
            if (got_result) {
                ReactiveMod.LOGGER.debug("Received material definitions from server");
                return clientside_data.get();
            }
            ReactiveMod.LOGGER.error("Timeout while fetching material definitions from the server. Custom materials will not work properly!");
            return MaterialData.empty();
        } catch (NullPointerException exception) {
            // Might be fetching before a connection is available. Just return empty material for now.
            ReactiveMod.LOGGER.debug("Connection wasn't available, this request failed");
            return MaterialData.empty();
        } catch (InterruptedException exception) {
            ReactiveMod.LOGGER.fatal("Client material fetch was interrupted by an outside force. Data will not sync.");
            return MaterialData.empty();
        }
    }

    public static void receiveDataAsync(MaterialData data) {
        clientside_data.set(data);
        initialized.set(true);
        response_ready.release();
    }

    // Sets the seed in the config to your world seed if that option is selected.
    public static void worldLoad(LevelEvent.Load event){
        if(event.getLevel().isClientSide()){
            ClientMaterialMan.initialized.set(false);
            ClientMaterialMan.clientside_data.set(MaterialData.empty());
        }
    }

    public static Component getName(ResourceLocation id) {
        if(clientside_data.get().materials.containsKey(id)) {
            return clientside_data.get().get(id).getNameComponent(id);
        }
        return Component.translatable("block.reactive.invalid_material");
    }

    public static void rename(ResourceLocation material_id, String value) {
        // clientside_data.get().get(material_id).setName(value); // Update on the client side.
        PacketDistributor.sendToServer(new MaterialRenamePayload(material_id, value)); // Tell server to update itself.
    }
}
