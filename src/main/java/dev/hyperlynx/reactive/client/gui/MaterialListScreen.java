package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.items.MaterialItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MaterialListScreen extends Screen {
    MaterialsList list_panel;
    protected MaterialListScreen() {
        super(Component.translatable("ui.reactive.material_list"));
    }
    BetterFittingMultiLineTextWidget formula_box;
    boolean notes_initialized = false;
    MaterialEntry last_selection = null;

    @Override
    protected void init() {
        super.init();
        list_panel = new MaterialsList(Minecraft.getInstance(), 165, this.getRectangle().height() - 40, this.getRectangle().top() + 20, this.getRectangle().height() - 40, 32);
        list_panel.setLeftPos(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) - 165);
        this.addRenderableWidget(list_panel);
        formula_box = new BetterFittingMultiLineTextWidget(this.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL) + 25, this.getRectangle().top() + 20, 150, this.getRectangle().height() - 40, Component.empty(), getMinecraft().font);
        formula_box.visible = false;
        this.addRenderableWidget(formula_box);
    }

    @Override
    public void tick() {
        super.tick();
        MaterialEntry selection = list_panel.getSelected();
        if(selection == null) {
            formula_box.visible = false;
            removeWidget(formula_box);
            notes_initialized = false;
        }else if(selection != last_selection) {
            if(notes_initialized) {
                removeWidget(formula_box);
            }
            Component formula = selection.material.formulaComponent();
            int max_width = 0;
            int max_height = 0;
            for(String line : formula.getString().lines().toList()) {
                int width = Minecraft.getInstance().font.width(line);
                if(max_width < width) {
                    max_width = width;
                }
                max_height += Minecraft.getInstance().font.lineHeight;
            }
            formula_box = new BetterFittingMultiLineTextWidget(
                    formula_box.getX(),
                    formula_box.getY(),
                    max_width + formula_box.getInnerPadding() * 2,
                    max_height + formula_box.getInnerPadding() * 2,
                    selection.material.formulaComponent(),
                    Minecraft.getInstance().font);
            formula_box.visible = true;
            addRenderableWidget(formula_box);
            notes_initialized = true;
            last_selection = selection;
        }
    }

    private static class MaterialsList extends ObjectSelectionList<MaterialEntry> {
        public MaterialsList(Minecraft client, int width, int height, int y, int y_1, int item_height) {
            super(client, width, height, y, y_1, item_height);
            for(ResourceLocation material_id : ClientMaterialMan.getKeysInDiscoveryOrder()) {
                if(ClientMaterialMan.data().get(material_id).wasDiscovered()) {
                    this.addEntry(new MaterialEntry(material_id));
                }
            }
            setRenderTopAndBottom(false);
        }

        public int getRowWidth() {
            return 165;
        }

        @Override
        protected int getScrollbarPosition() {
            return getLeft() + getRowWidth() + 10;
        }
    }

    static class MaterialEntry extends ObjectSelectionList.Entry<MaterialEntry> implements GuiEventListener {
        final ResourceLocation material_id;
        final Material material;
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
            ItemStack dummy_stack = Registration.MATERIAL_ITEM.get().getDefaultInstance();
            MaterialItem.setMaterialId(dummy_stack, material_id);
            int top_line = top + 4;
            graphics.renderFakeItem(dummy_stack, left + 4, top_line + 2);
            graphics.drawString(Minecraft.getInstance().font, ClientMaterialMan.getName(material_id), left + 24, top_line, 0xFFFFFFFF);
            graphics.drawString(Minecraft.getInstance().font, material.getDiscovererName(Minecraft.getInstance().level), left + 24, top_line + 12, 0xFFFFFFFF);
        }

        @Override
        public @NotNull Component getNarration() {
            return material.getNameComponent();
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            setFocused(true);
            return true;
        }
    }
}
