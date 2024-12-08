package dev.hyperlynx.reactive.client.gui;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import dev.hyperlynx.reactive.components.LitmusMeasurement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LitmusScreen extends Screen {
    LitmusMeasurement measurement;
    List<Component> reaction_lines;
    int y = 0;

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

        for(Component component : buildPowerText(measurement)){
            addLine(component);
        }
        for(Component component : reaction_lines){
            addLine(component);
        }
    }

    private void addLine(Component component) {
        StringWidget line = new StringWidget(component, Minecraft.getInstance().font);
        int line_width = line.getWidth();
        line.setPosition(this.width / 2 - line_width / 2, this.height / 4 + y);
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
