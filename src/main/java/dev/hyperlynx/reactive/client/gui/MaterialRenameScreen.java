package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MaterialRenameScreen extends Screen {
    ResourceLocation material_to_rename;
    EditBox name_box = new EditBox(Minecraft.getInstance().font, 250, 20, Component.empty());
    Button name_set_button = new Button.Builder(Component.translatable("text.reactive.name_material_button"),
            button -> {
                ClientMaterialMan.rename(material_to_rename, name_box.getValue());
            }).pos(0, 50).build();

    protected MaterialRenameScreen(ResourceLocation material_to_rename) {
        super(Component.translatable("title.reactive.rename_material_screen"));
        this.material_to_rename = material_to_rename;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(name_box);
        this.addRenderableWidget(name_set_button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
