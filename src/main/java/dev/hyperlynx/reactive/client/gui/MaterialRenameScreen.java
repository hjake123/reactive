package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MaterialRenameScreen extends Screen {
    ResourceLocation material_to_rename;
    EditBox name_box = new EditBox(Minecraft.getInstance().font,200, 20, Component.empty());

    protected MaterialRenameScreen(ResourceLocation material_to_rename) {
        super(Component.translatable("title.reactive.name_material_screen"));
        this.material_to_rename = material_to_rename;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    protected void init() {
        super.init();
        name_box.setMaxLength(32);
        name_box.setHint(ClientMaterialMan.getName(material_to_rename));
        name_box.setPosition(this.width / 2 - (name_box.getWidth() / 2), this.height / 2);
        this.addRenderableWidget(name_box);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if(Minecraft.getInstance().level == null) {
            return;
        }
        ItemStack dummy_stack = ReactiveItems.MATERIAL.get().getDefaultInstance();
        dummy_stack.set(ReactiveComponentTypes.MATERIAL_ID.get(), material_to_rename);
        graphics.pose().pushPose();
        graphics.pose().scale(4, 4, 4); // Zoom in to make the material block preview bigger
        graphics.renderFakeItem(dummy_stack, this.width / 8 - 8, this.height / 8 - 17); // The pixel scaling is also altered by the zoom in
        graphics.pose().popPose();
    }

    @Override
    public void onClose() {
        super.onClose();
        ClientMaterialMan.rename(material_to_rename, name_box.getValue());
    }
}
