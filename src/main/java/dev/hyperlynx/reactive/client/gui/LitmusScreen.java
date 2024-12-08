package dev.hyperlynx.reactive.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

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
            int line_width = line.getWidth();
            line.setPosition(this.width / 2 - line_width / 2, this.height / 4 + y);
            line.alignCenter();
            y += 10;
            this.addRenderableWidget(line);

        }
    }
}
