package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MaterialListScreen extends Screen {
    MaterialsList list_panel;
    protected MaterialListScreen() {
        super(Component.translatable("ui.reactive.material_list"));
    }

    @Override
    protected void init() {
        super.init();
        list_panel = new MaterialsList(Minecraft.getInstance(), 300, 150, 30, 50);
        list_panel.setPosition(this.width / 5, 50);
        this.addRenderableWidget(list_panel);
    }

    private static class MaterialsList extends ObjectSelectionList<MaterialEntry> {
        public MaterialsList(Minecraft client, int width, int height, int y, int item_height) {
            super(client, width, height, y, item_height);
            for(ResourceLocation material_id : ClientMaterialMan.data().getKeys()) {
                if(ClientMaterialMan.data().get(material_id).wasDiscovered()) {
                    this.addEntry(new MaterialEntry(material_id));
                }
            }
        }
    }

    static class MaterialEntry extends ObjectSelectionList.Entry<MaterialEntry> implements GuiEventListener {
        ResourceLocation material_id;
        Material material;
        boolean focused = false;

        public MaterialEntry(ResourceLocation material_id) {
            this.material_id = material_id;
            this.material = ClientMaterialMan.data().get(material_id);
        }

        @Override
        public void setFocused(boolean focused) {
            this.focused = focused;
        }

        @Override
        public boolean isFocused() {
            return focused;
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            ItemStack dummy_stack = ReactiveItems.MATERIAL.get().getDefaultInstance();
            dummy_stack.set(ReactiveComponentTypes.MATERIAL_ID.get(), material_id);
            int center_y = top + (height / 2) - 5;
            graphics.renderFakeItem(dummy_stack, left, center_y);
            graphics.drawString(Minecraft.getInstance().font, ClientMaterialMan.getName(material_id), left + 20, center_y, 0xFFFFFFFF);
        }

        @Override
        public @NotNull Component getNarration() {
            return material.getNameComponent();
        }
    }
}
