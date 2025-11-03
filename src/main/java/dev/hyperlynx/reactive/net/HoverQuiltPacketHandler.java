package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.client.renderers.HoverQuiltRenderer;
import dev.hyperlynx.reactive.net.quilt.HoverQuiltHeightMessage;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/// Utility class which solely exist to send packets to the HoverQuiltRenderer without having to load that class on the Server
public class HoverQuiltPacketHandler {
    public static void handleHeightPacket(HoverQuiltHeightMessage payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> HoverQuiltRenderer.updateHeight(payload));
        context.get().setPacketHandled(true);
    }
}
