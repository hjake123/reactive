package dev.hyperlynx.reactive.net.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.alchemy.material.MaterialData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record MaterialDataSyncMessage(MaterialData data) {
    public void encoder(FriendlyByteBuf buf) {
        buf.writeNbt(data.save(new CompoundTag()));
    }

    public static MaterialDataSyncMessage decoder(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        if(tag == null) {
            ReactiveMod.LOGGER.error("Failed to capture material data NBT. The material system will break!");
            return new MaterialDataSyncMessage(new MaterialData());
        }
        return new MaterialDataSyncMessage(MaterialData.load(tag));
    }

    public void handler(Supplier<NetworkEvent.Context> context) {
        ClientMaterialMan.receiveDataAsync(data);
        context.get().setPacketHandled(true);
    }
}
