package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.net.LitmusScreenPayload;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class LitmusScreenOpener {
    public static void open(@NotNull LitmusScreenPayload payload) {
        Minecraft.getInstance().setScreen(new LitmusScreen(payload.measurement(), payload.components()));
    }
}
