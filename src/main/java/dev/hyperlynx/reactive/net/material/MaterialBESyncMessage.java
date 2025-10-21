package dev.hyperlynx.reactive.net.material;

import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record MaterialBESyncMessage(ResourceLocation material_id, BlockPos pos) {
    public void encoder(FriendlyByteBuf buf) {
        buf.writeResourceLocation(material_id);
        buf.writeBlockPos(pos);
    }


    public static MaterialBESyncMessage decoder(FriendlyByteBuf buf) {
        return new MaterialBESyncMessage(buf.readResourceLocation(), buf.readBlockPos());
    }

    public void handler(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if(FMLLoader.getDist().isClient()){
                ClientMaterialMan.handleMaterialBESync(material_id, pos);
            }
        });
        context.get().setPacketHandled(true);
    }
}
