package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.menu.DeskMenu;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class DeskScreen extends AbstractContainerScreen<DeskMenu> {
    Button rename_button = new Button.Builder(Component.translatable("text.reactive.rename"),
            button -> {
                if(this.getMenu().getSlot(0).hasItem()) {
                    ItemStack stack = this.getMenu().getSlot(0).getItem();
                    ResourceLocation location = stack.get(ReactiveComponentTypes.MATERIAL_ID.get());
                    if(location != null) {
                        ScreenOpener.materialRename(location);
                    }
                }
            }).build();


    public DeskScreen(DeskMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        rename_button.setPosition(30, 10);
        addRenderableWidget(rename_button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);

        /*
         * This method is added by the container screen to render
         * the tooltip of the hovered slot.
         */
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // TODO
    }
}
