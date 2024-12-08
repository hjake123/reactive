package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.components.LitmusMeasurement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LitmusScreen extends Screen {
    LitmusMeasurement measurement;
    List<Component> reaction_lines;
    int y = 0;
    DisplayState state = DisplayState.POWERS;
    int page = 0;
    int max_page = 0;

    enum DisplayState {
        POWERS,
        REACTIONS
    }

    public LitmusScreen(LitmusMeasurement measurement, List<Component> reaction_lines) {
        super(Component.translatable("item.reactive.litmus_paper"));
        this.reaction_lines = reaction_lines;
        this.measurement = measurement;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void init() {
        super.init();
        addLine(Component.empty());
        switch (state){
            case DisplayState.POWERS -> {
                List<Component> measurement_lines = paginate(buildPowerText(measurement));
                for (Component component : measurement_lines) {
                    addLine(component);
                }
            }
            case DisplayState.REACTIONS -> {
                for(Component component : paginate(reaction_lines)){
                    addLine(component);
                }
            }
        }
        Button switch_button = Button.builder(
                Component.translatable(state.equals(DisplayState.POWERS) ? "text.reactive.view_reactions" : "text.reactive.view_powers"),
                LitmusScreen::toggle).build();
        switch_button.setPosition(this.width / 2 - (switch_button.getWidth() / 2), this.height / 5 - switch_button.getHeight());
        this.addRenderableWidget(switch_button);

        if(this.page > 0) {
            Button page_backward = Button.builder(
                    Component.literal("<"),
                    LitmusScreen::pageBackward).build();
            page_backward.setWidth(20);
            page_backward.setPosition(this.width / 2 - (switch_button.getWidth() / 2) - page_backward.getWidth(), this.height / 5 - switch_button.getHeight());
            this.addRenderableWidget(page_backward);
        }

        if(this.page < this.max_page){
            Button page_forward = Button.builder(
                    Component.literal(">"),
                    LitmusScreen::pageForward).build();
            page_forward.setWidth(20);
            page_forward.setPosition(this.width / 2 + (switch_button.getWidth() / 2), this.height / 5 - switch_button.getHeight());
            this.addRenderableWidget(page_forward);
        }
    }

    /*
    Select only lines on page (this.page).
    There are 10 lines on each page.
     */
    static int PAGE_LENGTH = 16;
    private List<Component> paginate(List<Component> components) {
        List<Component> paginated = new LinkedList<>(components);
        for(int i = 0; i < page * PAGE_LENGTH; i++) {
            paginated.removeFirst();
        }
        while (paginated.size() > PAGE_LENGTH) {
            paginated.removeLast();
        }
        this.max_page = components.size() / PAGE_LENGTH;
        return paginated;
    }

    private static void toggle(Button button) {
        if (Minecraft.getInstance().screen instanceof LitmusScreen lit_screen){
            switch (lit_screen.state) {
                case DisplayState.POWERS -> lit_screen.state = DisplayState.REACTIONS;
                case DisplayState.REACTIONS -> lit_screen.state = DisplayState.POWERS;
            }
            lit_screen.page = 0;
            lit_screen.y = 0;
            Minecraft.getInstance().setScreen(lit_screen);
        }
    }

    private static void pageForward(Button button) {
        if (Minecraft.getInstance().screen instanceof LitmusScreen lit_screen){
            if(lit_screen.page < lit_screen.max_page){
                lit_screen.page++;
            }
            lit_screen.y = 0;
            Minecraft.getInstance().setScreen(lit_screen);
        }
    }

    private static void pageBackward(Button button) {
        if (Minecraft.getInstance().screen instanceof LitmusScreen lit_screen){
            if(lit_screen.page > 0){
                lit_screen.page--;
            }
            lit_screen.y = 0;
            Minecraft.getInstance().setScreen(lit_screen);
        }
    }

    private void addLine(Component component) {
        StringWidget line = new StringWidget(component, Minecraft.getInstance().font);
        int line_width = line.getWidth();
        line.setPosition(this.width / 2 - line_width / 2, this.height / 5 + y);
        line.alignCenter();
        y += 10;
        this.addRenderableWidget(line);
    }

    private List<Component> buildPowerText(LitmusMeasurement measurement){
        List<Component> text = new ArrayList<>();
        text.add(Component.translatable("text.reactive.measurement_header").withStyle(ChatFormatting.GRAY));
        for(LitmusMeasurement.Line line : measurement.measurements()){
            TextColor color = TextColor.fromRgb(0xFFFFFF);
            if(ConfigMan.CLIENT.colorizeLitmusOutput.get()){
                Power power = Powers.POWER_REGISTRY.get(line.power());
                if(power != null) {
                    color = power.getTextColor();
                }
//                if(power == Powers.OMEN_POWER.get() && player instanceof ServerPlayer splayer){
//                    Registration.ISOLATE_OMEN_TRIGGER.get().trigger(splayer);
//                } TODO re-implement
            }
            text.add(Component.literal(line.line()).withStyle(Style.EMPTY.withColor(color)));
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if(measurement.measurements().isEmpty()){
            text.add(Component.translatable("text.reactive.measurement_empty")
                    .withStyle(ConfigMan.CLIENT.colorizeLitmusOutput.get() ? Style.EMPTY.withColor(BiomeColors.getAverageWaterColor(player.level(), player.getOnPos())) : Style.EMPTY));
        }
        text.add(Component.empty());
        if(measurement.integrity_violated()){
            text.add(Component.translatable("text.reactive.litmus_integrity_failure")
                    .withStyle(ConfigMan.CLIENT.colorizeLitmusOutput.get() ? ChatFormatting.DARK_RED : ChatFormatting.WHITE));
        }

        return text;
    }
}
