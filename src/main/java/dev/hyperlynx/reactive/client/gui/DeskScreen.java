package dev.hyperlynx.reactive.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.menu.DeskMenu;
import dev.hyperlynx.reactive.net.MaterialNotesPayload;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.css.Rect;

import java.util.List;
import java.util.Objects;

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

    MultiLineEditBox notes_box = new MultiLineEditBox(Minecraft.getInstance().font, 0, 0, 104, 60, Component.translatable("ui.reactive.notes_hint"), Component.empty());
    boolean notes_loaded = false;
    boolean notes_changed = false;

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
        rename_button.setPosition(slot_x - 16,slot_y + 24);
        rename_button.setWidth(51);
        addRenderableWidget(rename_button);

        notes_box.active = false;
        notes_box.visible = false;
        notes_box.setPosition(slot_x + 39, slot_y - 6);
        notes_box.setValueListener(this::updateNotes);
        notes_box.setCharacterLimit(256);
        addRenderableWidget(notes_box);
    }

    private Material getMaterial() {
        return MaterialMan.fetch(Minecraft.getInstance().level, getMaterialId());
    }

    private ResourceLocation getMaterialId() {
        ItemStack stack = this.getMenu().getSlot(0).getItem();
        if(!stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
            return null;
        }
        return stack.get(ReactiveComponentTypes.MATERIAL_ID.get());
    }

    @Override
    protected void containerTick() {
        rename_button.active = false;
        rename_button.visible = false;
        rename_button.setTooltip(null);
        notes_box.visible = false;
        notes_box.active = false;
        notes_loaded = false;
        if(this.getMenu().getSlot(0).hasItem()) {
            Material material = getMaterial();
            if(material != null) {
                if(!notes_loaded) {
                    loadNotes(material);
                    notes_loaded = true;
                }
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

    private void loadNotes(Material material) {
        notes_box.active = true;
        notes_box.visible = true;
        if(material.getNotes().isPresent()) {
            notes_box.setValue(material.getNotes().get());
        }
    }

    private void updateNotes(String notes) {
        Objects.requireNonNull(getMaterial()).setNotes(notes);
        notes_changed = true;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(DESK_BACKGROUND_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        renderDecorations(guiGraphics);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        if (notes_box.isFocused() && Minecraft.getInstance().options.keyInventory.isActiveAndMatches(mouseKey)) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
        if(notes_changed) {
            ClientMaterialMan.syncNotes();
        }
    }

    // -- Square glowing decoration --
    private record Rect(int x1, int y1, int x2, int y2) {}

    private static final List<Rect> DECORATIONS = List.of(
            new Rect(21, 20, 46, 20),
            new Rect(21, 45, 46, 45),
            new Rect(21, 20, 21, 45),
            new Rect(46, 20, 46, 45)
    );

    private void renderDecorations(GuiGraphics graphics) {
        if(getMaterialId() == null) {
            return;
        }
        Color color = getMaterial().get(MaterialProperties.COLOR.get());
        if(color == null) {
            return;
        }
        for (Rect rect : DECORATIONS) {
            graphics.fill(rect.x1 + getGuiLeft(), rect.y1 + getGuiTop(),
                    rect.x2 + getGuiLeft() + 1, rect.y2 + getGuiTop() + 1,
                    0xFF000000 | color.hex);
        }
    }

}
