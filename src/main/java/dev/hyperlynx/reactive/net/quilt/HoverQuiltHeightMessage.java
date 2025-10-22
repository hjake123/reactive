package dev.hyperlynx.reactive.net.quilt;

import net.minecraft.network.FriendlyByteBuf;

public record HoverQuiltHeightMessage(int id, double height) {
    public void encoder(FriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeDouble(height);
    }

    public static HoverQuiltHeightMessage decoder(FriendlyByteBuf buf) {
        return new HoverQuiltHeightMessage(buf.readInt(), buf.readDouble());
    }
}
