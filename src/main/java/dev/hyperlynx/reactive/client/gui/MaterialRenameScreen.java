package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
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
    EditBox name_box = new EditBox(Minecraft.getInstance().font,142, 20, Component.empty());
    Button name_set_button = new Button.Builder(Component.translatable("ui.reactive.discover_button"),
            button -> {
                ClientMaterialMan.rename(material_to_rename, name_box.getValue());
                Minecraft.getInstance().popGuiLayer();
            }).width(142).build();

    protected MaterialRenameScreen(ResourceLocation material_to_rename) {
        super(Component.translatable("ui.reactive.name_material_screen"));
        this.material_to_rename = material_to_rename;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    protected void init() {
        super.init();

        name_box.setMaxLength(24);
        name_box.setHint(ClientMaterialMan.getName(material_to_rename));
        name_box.setPosition(this.width / 2 - (name_box.getWidth() / 2), this.height / 2);
        this.addRenderableWidget(name_box);

        Material material = MaterialMan.fetch(Minecraft.getInstance().level, material_to_rename);
        if(!(material == null) && material.wasDiscovered()) {
            name_set_button.setMessage(Component.translatable("ui.reactive.rename_button"));
        }
        name_set_button.setPosition(this.width / 2 - (name_set_button.getWidth() / 2), this.height / 2 + 25);
        name_set_button.active = false;
        this.addRenderableWidget(name_set_button);
    }

    @Override
    public void tick() {
        name_set_button.active = !name_box.getValue().isEmpty();
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

    }
}
