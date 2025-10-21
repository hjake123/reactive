package dev.hyperlynx.reactive.net.material;

import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record MaterialRenameMessage(ResourceLocation material_id, String name) {
    public void encoder(FriendlyByteBuf buf) {
        buf.writeResourceLocation(material_id);
        buf.writeUtf(name);
    }


    public static MaterialRenameMessage decoder(FriendlyByteBuf buf) {
        return new MaterialRenameMessage(buf.readResourceLocation(), buf.readUtf());
    }

    public void handler(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if(!(context.get().getSender().level() instanceof ServerLevel slevel)) {
                return;
            }
            MaterialMan.rename(slevel, context.get().getSender(), material_id, name);
        });
        context.get().setPacketHandled(true);
    }
}

