package dev.hyperlynx.reactive.net.quilt;

import net.minecraft.network.FriendlyByteBuf;

public record HoverQuiltVelocityMessage(double velocity) {
    public void encoder(FriendlyByteBuf buf) {
        buf.writeDouble(velocity);
    }

    public static HoverQuiltVelocityMessage decoder(FriendlyByteBuf buf) {
        return new HoverQuiltVelocityMessage(buf.readDouble());
    }
}
