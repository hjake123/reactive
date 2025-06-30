package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.net.LitmusScreenPayload;
import dev.hyperlynx.reactive.net.MaterialRenameScreenPayload;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class ScreenOpener {
    public static void litmus(@NotNull LitmusScreenPayload payload) {
        Minecraft.getInstance().setScreen(new LitmusScreen(payload.measurement(), payload.components()));
    }

    public static void materialRename(@NotNull MaterialRenameScreenPayload payload) {
        Minecraft.getInstance().setScreen(new MaterialRenameScreen(payload.material_id()));
    }
}
