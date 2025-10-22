package dev.hyperlynx.reactive.net.material;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.material.MaterialData;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class MaterialDataSyncRequest {
    public void encoder(FriendlyByteBuf ignored) {}

    public static MaterialDataSyncRequest decoder(FriendlyByteBuf ignored) {
        return new MaterialDataSyncRequest();
    }

    public void handler(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if(!(context.get().getSender().level() instanceof ServerLevel slevel)) {
                return;
            }
            Registration.GENERAL_CHANNEL.send(PacketDistributor.PLAYER.with(context.get()::getSender), new MaterialDataSyncMessage(new MaterialData(MaterialMan.data(slevel))));
        });
        context.get().setPacketHandled(true);
    }
}
