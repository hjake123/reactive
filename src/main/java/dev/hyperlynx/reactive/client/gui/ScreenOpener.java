package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.items.LitmusPaperItem;
import dev.hyperlynx.reactive.net.litmus.LitmusData;
import dev.hyperlynx.reactive.net.litmus.UnresolvedLitmusData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ScreenOpener {
    public static void litmus(@NotNull LitmusData data) {
        Minecraft.getInstance().setScreen(new LitmusScreen(data));
    }

    public static void litmus(@NotNull UnresolvedLitmusData udata) {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        LitmusData data = new LitmusData(
                LitmusPaperItem.buildPowerText(udata.paper(), BiomeColors.getAverageWaterColor(player.level(), player.getOnPos()), false),
                LitmusPaperItem.buildReactionText(udata.paper(), true));
        Minecraft.getInstance().setScreen(new LitmusScreen(data));
    }

    public static void materialRename(@NotNull ResourceLocation material_id) {
        Minecraft.getInstance().setScreen(new MaterialRenameScreen(material_id));
    }

    public static void materialList() {
        Minecraft.getInstance().setScreen(new MaterialListScreen());
    }
}
