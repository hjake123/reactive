package dev.hyperlynx.reactive.fx.gui;

import dev.hyperlynx.reactive.items.LitmusPaperItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class LitmusScreenOpener {
    public static void open(@NotNull LitmusData data) {
        Minecraft.getInstance().setScreen(new LitmusScreen(data));
    }

    public static void open(@NotNull UnresolvedLitmusData udata) {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        LitmusData data = new LitmusData(
                LitmusPaperItem.buildPowerText(udata.paper(), BiomeColors.getAverageWaterColor(player.level(), player.getOnPos()), false),
                LitmusPaperItem.buildReactionText(udata.paper(), true));
        Minecraft.getInstance().setScreen(new LitmusScreen(data));
    }
}
