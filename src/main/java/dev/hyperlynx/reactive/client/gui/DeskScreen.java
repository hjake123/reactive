package dev.hyperlynx.reactive.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.menu.DeskMenu;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class DeskScreen extends AbstractContainerScreen<DeskMenu> {
    private static final ResourceLocation DESK_BACKGROUND_LOCATION = ReactiveMod.location("textures/gui/discovery_desk.png");

    Button rename_button = new Button.Builder(Component.empty(),
            button -> {
                if(this.getMenu().getSlot(0).hasItem()) {
                    ItemStack stack = this.getMenu().getSlot(0).getItem();
                    ResourceLocation location = stack.get(ReactiveComponentTypes.MATERIAL_ID.get());
                    if(location != null) {
                        ScreenOpener.materialRename(location);
                    }
                }
            }).build();

    MultiLineEditBox notes_box = new MultiLineEditBox(Minecraft.getInstance().font, 0, 0, 104, 60, Component.empty(), Component.empty());

    public DeskScreen(DeskMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        int slot_x = this.getMenu().getSlot(0).x + this.getGuiLeft();
        int slot_y = this.getMenu().getSlot(0).y + this.getGuiTop();

        rename_button.visible = false;
        rename_button.active = false;
        rename_button.setPosition(slot_x - 17,slot_y + 24);
        rename_button.setWidth(50);
        addRenderableWidget(rename_button);

        notes_box.active = false;
        notes_box.visible = false;
        notes_box.setPosition(slot_x + 39, slot_y - 6);
        addRenderableWidget(notes_box);
    }

    @Override
    protected void containerTick() {
        rename_button.active = false;
        rename_button.visible = false;
        rename_button.setTooltip(null);
        notes_box.visible = false;
        notes_box.active = false;
        if(this.getMenu().getSlot(0).hasItem()) {
            ItemStack stack = this.getMenu().getSlot(0).getItem();
            if(!stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
                return;
            }
            Material material = MaterialMan.fetch(Minecraft.getInstance().level, stack.get(ReactiveComponentTypes.MATERIAL_ID.get()));
            if(material != null) {
                updateNotes(material);
                updateButton(material);
            }
        }
    }

    private void updateButton(Material material) {
        rename_button.visible = true;
        if(material.wasDiscovered()) {
            rename_button.setMessage(Component.translatable("ui.reactive.rename_button"));
            if(material.playerDiscoveredThis(Minecraft.getInstance().player)) {
                rename_button.active = true;
            } else {
                rename_button.setTooltip(Tooltip.create(Component.translatable("text.reactive.cannot_rename_others_discovery")));
            }
        } else {
            rename_button.setMessage(Component.translatable("ui.reactive.discover_button"));
            rename_button.active = true;
        }
    }

    private void updateNotes(Material material) {
        notes_box.active = true;
        notes_box.visible = true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(DESK_BACKGROUND_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        if (notes_box.isFocused() && Minecraft.getInstance().options.keyInventory.isActiveAndMatches(mouseKey)) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
