package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.menu.DeskMenu;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DeskScreen extends AbstractContainerScreen<DeskMenu> {
    private static final ResourceLocation DESK_BACKGROUND_LOCATION = ReactiveMod.location("textures/gui/discovery_desk.png");
    private static final ResourceLocation VIEW_DISCOVERIES_BUTTON = ReactiveMod.location("discoveries_tab");
    private static final ResourceLocation VIEW_DISCOVERIES_BUTTON_INACTIVE = ReactiveMod.location("discoveries_tab_inactive");
    private static final ResourceLocation VIEW_DISCOVERIES_BUTTON_FOCUSED = ReactiveMod.location("discoveries_tab_focused");

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

    Button discoveries_button = new ImageButton(20, 20, new WidgetSprites(VIEW_DISCOVERIES_BUTTON, VIEW_DISCOVERIES_BUTTON_INACTIVE, VIEW_DISCOVERIES_BUTTON_FOCUSED),
            button -> ScreenOpener.materialList(), Component.empty());
    BetterFittingMultiLineTextWidget readout;
    int slot_x;
    int slot_y;
    ResourceLocation last_material = ReactiveMod.location("null");

    public DeskScreen(DeskMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        slot_x = this.getMenu().getSlot(0).x + this.getGuiLeft();
        slot_y = this.getMenu().getSlot(0).y + this.getGuiTop();

        rename_button.visible = false;
        rename_button.active = false;
        rename_button.setPosition(slot_x - 16,slot_y + 24);
        rename_button.setWidth(51);
        addRenderableWidget(rename_button);

        discoveries_button.setPosition(this.getGuiLeft() + 150, this.getGuiTop() - 17);
        addRenderableWidget(discoveries_button);

        readout = null;
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
        if(this.getMenu().getSlot(0).hasItem()) {
            Material material = getMaterial();
            if(material != null) {
                updateButton(material);
                updateReadout(material, getMaterialId());
            }
        } else {
            if(readout != null) {
                removeWidget(readout);
                last_material = ReactiveMod.location("null");
            }
            readout = null;
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

    private void updateReadout(Material material, ResourceLocation material_id) {
        if(material_id.equals(last_material) && readout != null) {
            return;
        }
        last_material = material_id;
        if(readout != null) {
            removeWidget(readout);
        }
        MutableComponent readout_message = Component.empty();
        Map<Power, Integer> original_formula = material.getOriginalFormula().orElse(Map.of());
        if(original_formula.isEmpty()) {
            readout_message = Component.translatable("ui.reactive.no_formula");
        }
        List<Component> power_lines = new ArrayList<>();
        for(Power power : original_formula.keySet().stream().sorted(Comparator.comparing(original_formula::get)).toList().reversed()) {
            power_lines.add(Component.literal(power.getName() + ": " + Math.round(original_formula.get(power) / 16.0) + "%")
                    .withColor(ConfigMan.CLIENT.colorizeLitmusOutput.get() ? power.getColor().hex() : 0xFFFFFF));
        }
        for(int i = 0; i < power_lines.size(); i++) {
            readout_message.append(power_lines.get(i));
            if(i < power_lines.size() - 1) {
                readout_message.append("\n");
            }
        }
        readout = new BetterFittingMultiLineTextWidget(slot_x + 39, slot_y - 6, 104, 60, readout_message, Minecraft.getInstance().font);
        addRenderableWidget(readout);
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
