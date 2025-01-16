package dev.hyperlynx.reactive.integration.jei.bottles;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.integration.jei.ReactiveJEIPlugin;
import dev.hyperlynx.reactive.recipes.DissolveRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PowerBottleRecipeCategory implements IRecipeCategory<PowerBottleRecipe> {
    @Override
    public RecipeType<PowerBottleRecipe> getRecipeType() {
        return RecipeType.create(ReactiveMod.MODID, "power_bottle", PowerBottleRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.reactive.power_bottle_contains");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
    }

    @Override
    public void draw(PowerBottleRecipe recipe, @Nullable IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics gui, double mouseX, double mouseY) {
        background().draw(gui);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PowerBottleRecipe recipe, IFocusGroup focus_group) {
        IRecipeSlotBuilder bottle_slot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 1);
        bottle_slot.setSlotName("bottle");
        bottle_slot.addIngredients(recipe.bottle);
        bottle_slot.setStandardSlotBackground();
        IRecipeSlotBuilder power_slot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 21);
        power_slot.setSlotName("power");
        power_slot.addIngredients(ReactiveJEIPlugin.POWER_TYPE, List.of(recipe.power));
        power_slot.setStandardSlotBackground();
    }

    public IDrawable background() {
        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawable(ReactiveMod.location("textures/gui/bottle_jei.png"), 2, 2, 18, 38);
    }

    @Override
    public int getWidth() {
        return background().getWidth();
    }

    @Override
    public int getHeight() {
        return background().getHeight();
    }
}
