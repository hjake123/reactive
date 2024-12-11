package dev.hyperlynx.reactive.client.gui;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;

public class LitmusScreenOpener {
    public static void open(@NotNull LitmusScreenPayload payload) {
        Minecraft.getInstance().setScreen(new LitmusScreen(payload.measurement(), payload.components()));
    }
}
