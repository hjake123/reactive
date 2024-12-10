package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.components.LitmusMeasurement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LitmusScreen extends Screen {
    LitmusMeasurement measurement;
    List<Component> reaction_lines;
    private List<LitmusScreenComponent> lines_to_draw;
    int y = 0;
    int page = 0;
    int max_page = 0;
    private Button page_backward;
    private Button page_forward;
    private static final int BOX_WIDTH = 180;
    private static final int BOX_HEIGHT = 200;

    public LitmusScreen(LitmusMeasurement measurement, List<Component> reaction_lines) {
        super(Component.translatable("item.reactive.litmus_paper"));
        this.reaction_lines = reaction_lines;
        this.measurement = measurement;
        assert Minecraft.getInstance().player != null;
    }

    private int getBoxCenterY() {
        return this.height / 2;
    }

    private int getBoxTopY() {
        return getBoxCenterY() - (BOX_HEIGHT / 2);
    }

    private int getBoxBottomY() {
        return getBoxCenterY() + (BOX_HEIGHT / 2);
    }

    private int getBoxCenterX() {
        return this.width / 2;
    }

    private int getBoxLeftX() {
        return getBoxCenterX() - (BOX_WIDTH / 2);
    }

    private int getBoxRightX() {
        return getBoxCenterX() + (BOX_WIDTH / 2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void init() {
        super.init();
        List<LitmusScreenComponent> lines = buildPowerText(measurement);
        lines.add(new LitmusScreenComponent(Component.empty(), false, false));
        lines.add(new LitmusScreenComponent(
                Component.translatable("text.reactive.reaction_header").withStyle(ConfigMan.COMMON.litmusScreen.get() ? ChatFormatting.BOLD : ChatFormatting.GRAY),
                false, true));
        lines.addAll(reaction_lines.stream().map((line) -> new LitmusScreenComponent(line, false, false)).toList());
        lines_to_draw = lines;

        page_backward = Button.builder(
                Component.literal("<"),
                LitmusScreen::pageBackward).build();
        page_backward.setWidth(20);
        page_backward.setPosition(getBoxLeftX() - page_backward.getWidth(), getBoxTopY());
        page_backward.visible = false;
        this.addRenderableWidget(page_backward);


        page_forward = Button.builder(
                Component.literal(">"),
                LitmusScreen::pageForward).build();
        page_forward.setWidth(20);
        page_forward.setPosition(getBoxRightX(), getBoxTopY());
        page_forward.visible = false;
        this.addRenderableWidget(page_forward);

    }

    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        page_forward.visible = page < max_page;
        page_backward.visible = page > 0;
        for(Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }
        for(LitmusScreenComponent component : paginate(lines_to_draw)){
            renderLine(graphics, component.component, component.power_text);
        }
        this.y = 0;
    }

    private void renderLine(GuiGraphics graphics, Component component, boolean power_text) {
        int line_width = this.font.width(component);
        graphics.drawString(this.font, component,getBoxCenterX() - line_width / 2, getBoxTopY() + 20 + y, 0xFFFFFF, power_text);
        y += 10;
    }

    /*
        Select only lines on page (this.page).
         */
    static int PAGE_LENGTH = 16;
    private List<LitmusScreenComponent> paginate(List<LitmusScreenComponent> components) {
        List<LitmusScreenComponent> paginated = new LinkedList<>(components);
        for(int i = 0; i < page * PAGE_LENGTH; i++) {
            if(paginated.getFirst().header && i == (page * PAGE_LENGTH - 1)){
                continue;
            }
            paginated.removeFirst();
        }
        while (paginated.size() > PAGE_LENGTH) {
            paginated.removeLast();
        }
        if(paginated.getLast().header){
            paginated.removeLast();
        }
        this.max_page = (components.size() - 1) / PAGE_LENGTH;
        return paginated;
    }

    private static void pageForward(Button button) {
        if (Minecraft.getInstance().screen instanceof LitmusScreen lit_screen){
            if(lit_screen.page < lit_screen.max_page){
                lit_screen.page++;
            }
            lit_screen.y = 0;
        }
    }

    private static void pageBackward(Button button) {
        if (Minecraft.getInstance().screen instanceof LitmusScreen lit_screen){
            if(lit_screen.page > 0){
                lit_screen.page--;
            }
            lit_screen.y = 0;
        }
    }



    private List<LitmusScreenComponent> buildPowerText(LitmusMeasurement measurement){
        List<LitmusScreenComponent> text = new ArrayList<>();
        text.add(new LitmusScreenComponent(
                Component.translatable("text.reactive.measurement_header").withStyle(ChatFormatting.BOLD),
                false, true));
        for(LitmusMeasurement.Line line : measurement.measurements()){
            TextColor color = TextColor.fromRgb(0xFFFFFF);
            if(ConfigMan.CLIENT.colorizeLitmusOutput.get()){
                Power power = Powers.POWER_REGISTRY.get(line.power());
                if(power != null) {
                    color = power.getTextColor();
                }
            }
            text.add(new LitmusScreenComponent(Component.literal(line.line()).withStyle(Style.EMPTY.withColor(color).withBold(false)),
                    true, false));
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if(measurement.measurements().isEmpty()){
            text.add(new LitmusScreenComponent(
                    Component.translatable("text.reactive.measurement_empty")
                    .withStyle(ConfigMan.CLIENT.colorizeLitmusOutput.get() ? Style.EMPTY.withColor(BiomeColors.getAverageWaterColor(player.level(), player.getOnPos())) : Style.EMPTY),
                    true, false));
        }
        if(measurement.integrity_violated()){
            text.add(new LitmusScreenComponent(
                    Component.translatable("text.reactive.litmus_integrity_failure")
                    .withStyle(ConfigMan.CLIENT.colorizeLitmusOutput.get() ? ChatFormatting.RED : ChatFormatting.WHITE),
                    true, false));
        }

        return text;
    }

    private static final ResourceLocation PAPER_BACKGROUND = ReactiveMod.location("textures/gui/litmus.png");

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        //graphics.blit(PAPER_BACKGROUND, this.width / 2 - 90, this.height / 8, 26, 8, 181, 200);

        graphics.fill(
                this.width / 2 - (BOX_WIDTH/2),
                getBoxTopY(),
                this.width / 2 + (BOX_WIDTH/2),
                getBoxTopY() + BOX_HEIGHT,
                0x60606060
        );
    }

    static record LitmusScreenComponent (Component component, boolean power_text, boolean header) {}
}
