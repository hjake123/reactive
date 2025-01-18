package dev.hyperlynx.reactive.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
Previously, I was using JEI's slot background feature for this,
but EMI does not implement that feature at the moment.

Until it does, it's best to do this myself...

And since I'm doing this, might as well give this class more responsibility as well.
 */
public class SlotManager {
    HashMap<String, Tuple<Integer, Integer>> slots = new HashMap<>();

    public void drawSlotBackground(GuiGraphics gui, String key) {
        var slot = slots.get(key);
        IGuiHelper helper = ReactiveJEIPlugin.HELPERS.getGuiHelper();
        helper.getSlotDrawable().draw(gui, slot.getA() - 1, slot.getB() - 1);

    }

    public void drawSlotBackgrounds(GuiGraphics gui, List<String> keys) {
        for(var slot_name : keys) {
            drawSlotBackground(gui, slot_name);
        }
    }

    public void drawAllSlotBackgrounds(GuiGraphics gui) {
        drawSlotBackgrounds(gui, slots.keySet().stream().toList());
    }

    public void addSlot(String key, int x, int y) {
        slots.put(key, new Tuple<>(x, y));
    }

    public IRecipeSlotBuilder buildSlot(IRecipeLayoutBuilder builder, String key, RecipeIngredientRole role) {
        var slot = slots.get(key);
        var slot_builder = builder.addSlot(role, slot.getA(), slot.getB());
        slot_builder.setSlotName(key);
        return slot_builder;
    }
}
