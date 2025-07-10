package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.net.LitmusScreenPayload;
import dev.hyperlynx.reactive.net.MaterialRenameScreenPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ScreenOpener {
    public static void litmus(@NotNull LitmusScreenPayload payload) {
        Minecraft.getInstance().setScreen(new LitmusScreen(payload.measurement(), payload.components()));
    }

    public static void materialRename(@NotNull ResourceLocation material_id) {
        Minecraft.getInstance().setScreen(new MaterialRenameScreen(material_id));
    }

    public static void materialList() {
        Minecraft.getInstance().setScreen(new MaterialListScreen());
    }
}
