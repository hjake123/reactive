package dev.hyperlynx.reactive.net.material;

import dev.hyperlynx.reactive.client.gui.ScreenOpener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record MaterialRenameScreenMessage(ResourceLocation material_id)  {
    public void encoder(FriendlyByteBuf buf) {
        buf.writeResourceLocation(material_id);
    }


    public static MaterialRenameScreenMessage decoder(FriendlyByteBuf buf) {
        return new MaterialRenameScreenMessage(buf.readResourceLocation());
    }

    public void handler(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (FMLLoader.getDist() == Dist.CLIENT) {
                ScreenOpener.materialRename(material_id());
            }
        });
        context.get().setPacketHandled(true);
    }
}
