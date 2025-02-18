package dev.hyperlynx.reactive.net.litmus;

import dev.hyperlynx.reactive.fx.gui.LitmusScreenOpener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record LitmusScreenMessage(UnresolvedLitmusData udata)  {
    public void encoder(FriendlyByteBuf buf) {
        buf.writeItemStack(udata.paper(), false);
    }


    public static LitmusScreenMessage decoder(FriendlyByteBuf buf) {
        var paper = buf.readItem();
        return new LitmusScreenMessage(new UnresolvedLitmusData(paper));
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(FMLLoader.getDist().isClient()){
                LitmusScreenOpener.open(this.udata);
            }
        });
    }
}
