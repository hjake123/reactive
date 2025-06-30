package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.components.LitmusMeasurement;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
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
    private final List<Color> decoration_colors = new ArrayList<>();

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

    @Override
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
        page_backward.setPosition(getBoxLeftX() - page_backward.getWidth(), getBoxTopY()+8);
        page_backward.visible = false;
        this.addRenderableWidget(page_backward);


        page_forward = Button.builder(
                Component.literal(">"),
                LitmusScreen::pageForward).build();
        page_forward.setWidth(20);
        page_forward.setPosition(getBoxRightX(), getBoxTopY()+8);
        page_forward.visible = false;
        this.addRenderableWidget(page_forward);

    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        page_forward.visible = page < max_page;
        page_backward.visible = page > 0;
        for(Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }
        for(LitmusScreenLine line : paginate(lines_to_draw)){
            renderLine(graphics, line.sequence, line.power_text);
        }
        renderDecorations(graphics);
        this.y = 0;
    }

    private void renderLine(GuiGraphics graphics, FormattedCharSequence sequence, boolean power_text) {
        int line_width = this.font.width(sequence);
        graphics.drawString(this.font, sequence,getBoxCenterX() - line_width / 2, getBoxTopY() + 20 + y, 0xFFFFFF, power_text);
        y += 10;
    }

    /*
        Select only lines on page (this.page).
         */
    static int PAGE_LENGTH = 16;
    private List<LitmusScreenLine> paginate(List<LitmusScreenComponent> components) {
        this.max_page = (components.size() - 1) / PAGE_LENGTH;
        List<LitmusScreenLine> paginated = new LinkedList<>();
        for(LitmusScreenComponent c : components){
            if(c.component.equals(Component.empty())){
                paginated.add(new LitmusScreenLine(FormattedCharSequence.EMPTY, false, false));
                continue;
            }
            var lines = this.font.split(c.component, BOX_WIDTH);
            for (FormattedCharSequence sequence : lines) {
                paginated.add(new LitmusScreenLine(sequence, c.power_text, c.header));
            }
        }

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
                    if(decoration_colors.size() < DECORATIONS.size() && !power.invisible) {
                        if(power.equals(Powers.ASTRAL_POWER.get())){
                            // As a special signal, an entry with -1 color signifies ASTRAL.
                            decoration_colors.add(new Color(-1));
                        } else {
                            decoration_colors.add(new Color(color.getValue()));
                        }
                    }
                }
            }
            text.add(new LitmusScreenComponent(Component.literal(line.line()).withColor(color.getValue()), true, false));
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
        graphics.fill(
                this.width / 2 - (BOX_WIDTH/2),
                getBoxTopY(),
                this.width / 2 + (BOX_WIDTH/2),
                getBoxTopY() + BOX_HEIGHT,
                0x60606060
        );
        graphics.blit(PAPER_BACKGROUND, getBoxLeftX(), getBoxTopY(), 0, 0, 180, 224);
    }

    private static final List<Rect> DECORATIONS = List.of(
            new Rect(28, 3, 44, 4),
            new Rect(127, 3, 135, 4),
            new Rect(8, 195, 15, 196),
            new Rect(90, 3, 99, 4),
            new Rect(135, 195, 151, 196),
            new Rect(123, 195, 133, 196),
            new Rect(80, 195, 89, 196),
            new Rect(44, 195, 52, 196),
            new Rect(164, 3, 171, 4),
            new Rect(105, 3, 109, 4)
    );
    private void renderDecorations(GuiGraphics graphics) {
        for(int i = 0; i < decoration_colors.size(); i++) {
            if(decoration_colors.get(i).hex == -1){
                // This is Astral. Render differently.
                graphics.fill(RenderType.END_GATEWAY, getBoxLeftX(), getBoxTopY() + 2,
                        getBoxLeftX() + BOX_WIDTH, getBoxTopY() + 6,
                        0xFFFFFFFF);
                graphics.fill(RenderType.END_GATEWAY, getBoxLeftX(), getBoxBottomY() - 6,
                        getBoxLeftX() + BOX_WIDTH, getBoxBottomY() - 2,
                        0xFFFFFFFF);
                return;
            }

            Rect rect = DECORATIONS.get(i);
            graphics.fill(rect.x1 + getBoxLeftX(), rect.y1 + getBoxTopY(),
                        rect.x2 + getBoxLeftX() + 1, rect.y2 + getBoxTopY() + 1,
                        0xFF000000 | decoration_colors.get(i).hex);
        }
    }

    private record Rect(int x1, int y1, int x2, int y2) {}

    record LitmusScreenComponent (Component component, boolean power_text, boolean header) {}

    record LitmusScreenLine (FormattedCharSequence sequence, boolean power_text, boolean header) {}

}
