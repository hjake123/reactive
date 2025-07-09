package dev.hyperlynx.reactive.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;

public class NakedMultiLineEditBox extends MultiLineEditBox {
    public NakedMultiLineEditBox(Font font, int x, int y, int width, int height, Component placeholder, Component message) {
        super(font, x, y, width, height, placeholder, message);
    }

    @Override
    protected void renderDecorations(GuiGraphics guiGraphics) {

    }
}
