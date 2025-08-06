package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.be.MaterialBlockEntity;
import dev.hyperlynx.reactive.blocks.MaterialBlock;
import dev.hyperlynx.reactive.net.MaterialDataSyncRequestPayload;
import dev.hyperlynx.reactive.net.MaterialRenamePayload;
import dev.hyperlynx.reactive.registration.ReactiveBlocks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@EventBusSubscriber(modid=ReactiveMod.MODID, value= Dist.CLIENT)
public class ClientMaterialMan {
    public static final AtomicReference<MaterialData> clientside_data = new AtomicReference<>(MaterialData.empty());
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final Semaphore response_ready = new Semaphore(0, false);
    private static final AtomicBoolean query_active = new AtomicBoolean(false);

    public static MaterialData data() {
        if(initialized.get()) {
            return clientside_data.get();
        }
        if(query_active.get()){
            ReactiveMod.LOGGER.warn("Multiple threads are requesting material definitions at one time");
        }
        ReactiveMod.LOGGER.debug("Requesting material definitions from server");
        try {
            query_active.set(true);
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
        query_active.set(false);
        response_ready.release();
    }

    // Sets the seed in the config to your world seed if that option is selected.
    @SubscribeEvent
    public static void worldLoad(LevelEvent.Load event){
        if(event.getLevel().isClientSide()){
            ClientMaterialMan.initialized.set(false);
            ClientMaterialMan.clientside_data.set(MaterialData.empty());
        }
    }

    public static Component getName(ResourceLocation id) {
        if(clientside_data.get().materials.containsKey(id)) {
            return clientside_data.get().get(id).getNameComponent();
        }
        return Component.translatable("block.reactive.invalid_material");
    }

    public static void rename(ResourceLocation material_id, String value) {
        // clientside_data.get().get(material_id).setName(value); // Update on the client side.
        PacketDistributor.sendToServer(new MaterialRenamePayload(material_id, value)); // Tell server to update itself.
    }

    public static List<ResourceLocation> getKeysInDiscoveryOrder() {
        return data().materials.keySet().stream().sorted((left_id, right_id) ->
                Math.clamp(data().materials.get(right_id).getDiscoveryTime() - data().materials.get(left_id).getDiscoveryTime(), -Integer.MAX_VALUE, Integer.MAX_VALUE)).toList();
    }

    public static void handleMaterialBESync(IPayloadContext context, ResourceLocation material_id, BlockPos pos) {
        if(!(context.player().level() instanceof ClientLevel clevel)) {
            return;
        }
        if(!(clevel.getBlockState(pos).getBlock() instanceof MaterialBlock)) {
            clevel.setBlock(pos, ReactiveBlocks.MATERIAL_BLOCK.get().defaultBlockState(), Block.UPDATE_NONE);
        }
        if(clevel.getBlockEntity(pos) instanceof MaterialBlockEntity mbe) {
            mbe.setMaterial(clevel, material_id);
            MaterialBlockEntity.lights.setLightAt(pos, MaterialMan.fetch(clevel, material_id).getOrDefault(MaterialProperties.LIGHT.get(), 0));
        }
    }
}
