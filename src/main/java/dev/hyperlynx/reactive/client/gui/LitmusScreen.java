package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.components.LitmusMeasurement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class LitmusScreen extends Screen {
    List<Component> measurement;
    public LitmusScreen(List<Component> measurement) {
        super(Component.translatable("item.reactive.litmus_paper"));
        this.measurement = measurement;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void init() {
        super.init();

        int y = 0;
        for(Component component : measurement){
            StringWidget line = new StringWidget(component, Minecraft.getInstance().font);
            line.setPosition(this.width / 2 - 100, this.height / 3 + y);
            line.alignCenter();
            y += 10;
            this.addRenderableWidget(line);

        }
    }
}
