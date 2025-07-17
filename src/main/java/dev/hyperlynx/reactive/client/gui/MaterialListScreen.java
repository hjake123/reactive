package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MaterialListScreen extends Screen {
    MaterialsList list_panel;
    protected MaterialListScreen() {
        super(Component.translatable("ui.reactive.material_list"));
    }
    MultiLineTextWidget notes_field = new MultiLineTextWidget(Component.empty(), Minecraft.getInstance().font);
    int notes_width;

    @Override
    protected void init() {
        super.init();
        list_panel = new MaterialsList(Minecraft.getInstance(), 175, this.getRectangle().height() - 40, 10, 32);
        list_panel.setPosition(this.getRectangle().left() + 20, this.getRectangle().top() + 20);
        this.addRenderableWidget(list_panel);
        notes_field.setPosition(this.getRectangle().left() + 200, this.getRectangle().top() + 20);
        notes_field.setHeight(this.getRectangle().height() - 40);
        notes_width = this.getRectangle().right() - (this.getRectangle().left() + 200);
        notes_field.setMaxWidth(notes_width);
        this.addRenderableWidget(notes_field);
    }

    @Override
    public void tick() {
        super.tick();
        MaterialEntry entry = list_panel.getSelected();
        if(entry != null) {
            List<String> wrapped_lines = new ArrayList<>();
            for(String line : entry.material.getNotes().orElse("").lines().toList()) {
                String current_line = "";
                for (String word : line.split(" ")) {
                    String potentialLine = current_line.isEmpty() ? word : current_line + " " + word;
                    if (font.width(potentialLine) <= notes_width) {
                        current_line = potentialLine;
                    } else {
                        wrapped_lines.add(current_line);
                        current_line = word;
                    }
                }
                if (!current_line.isEmpty()) {
                    wrapped_lines.add(current_line);
                }
            }
            StringBuilder output_builder = new StringBuilder();
            for(String line : wrapped_lines) {
                output_builder.append(line);
                output_builder.append("\n");
            }

            notes_field.setMessage(Component.literal(output_builder.toString()));
        }
    }

    private static class MaterialsList extends ObjectSelectionList<MaterialEntry> {
        public MaterialsList(Minecraft client, int width, int height, int y, int item_height) {
            super(client, width, height, y, item_height);
            for(ResourceLocation material_id : ClientMaterialMan.getKeysInDiscoveryOrder()) {
                if(ClientMaterialMan.data().get(material_id).wasDiscovered()) {
                    this.addEntry(new MaterialEntry(material_id));
                }
            }
        }

        public int getRowWidth() {
            return 165;
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
            int top_line = top + 4;
            graphics.renderFakeItem(dummy_stack, left + 4, top_line + 2);
            graphics.drawString(Minecraft.getInstance().font, ClientMaterialMan.getName(material_id), left + 24, top_line, 0xFFFFFFFF);
            graphics.drawString(Minecraft.getInstance().font, material.getDiscovererName(Minecraft.getInstance().level), left + 24, top_line + 12, 0xFFFFFFFF);
        }

        @Override
        public @NotNull Component getNarration() {
            return material.getNameComponent();
        }
    }
}
