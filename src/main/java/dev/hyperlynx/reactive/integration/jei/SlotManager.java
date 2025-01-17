package dev.hyperlynx.reactive.integration.jei;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
Previously, I was using JEI's slot background feature for this,
but EMI does not implement that feature at the moment.

Until it does, it's best to do this myself...
 */
public class SlotManager {
    HashMap<String, SlotConfiguration> configurations = new HashMap<>();

    public void drawSlotBackgrounds(GuiGraphics gui, String key) {
        for(Tuple<Integer, Integer> slot : configurations.get(key).slots()) {
            IGuiHelper helper = ReactiveJEIPlugin.HELPERS.getGuiHelper();
            helper.getSlotDrawable().draw(gui, slot.getA(), slot.getB());
        }
    }

    public SlotConfigurationBuilder builder(String key) {
        return new SlotConfigurationBuilder(this, key);
    }

    private record SlotConfiguration(List<Tuple<Integer, Integer>> slots) {}

    public static class SlotConfigurationBuilder {
        private List<Tuple<Integer, Integer>> slots = new ArrayList<>();
        private SlotManager parent;
        private String key;

        public SlotConfigurationBuilder(SlotManager parent, String key) {
            this.key = key;
            this.parent = parent;
        }

        public SlotConfigurationBuilder addSlot(int x, int y) {
            slots.add(new Tuple<>(x - 1, y - 1));
            return this;
        }

        public void build() {
            parent.configurations.put(key, new SlotConfiguration(slots));
        }
    }
}
