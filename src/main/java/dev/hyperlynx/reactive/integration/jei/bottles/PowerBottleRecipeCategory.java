package dev.hyperlynx.reactive.integration.jei.bottles;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.integration.jei.ReactiveJEIPlugin;
import dev.hyperlynx.reactive.integration.jei.SlotManager;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PowerBottleRecipeCategory implements IRecipeCategory<PowerBottleRecipe> {
    SlotManager slot_manager = new SlotManager();

    public PowerBottleRecipeCategory() {
        slot_manager.addSlot("bottle", 1, 1);
        slot_manager.addSlot("power", 1, 21);
    }

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
        slot_manager.drawAllSlotBackgrounds(gui);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PowerBottleRecipe recipe, IFocusGroup focus_group) {
        var access = Minecraft.getInstance().getConnection().registryAccess();
        var power = Powers.get(recipe.power_key, access);
        var bottle = Ingredient.of(power.getBottle());
        IRecipeSlotBuilder bottle_slot = slot_manager.buildSlot(builder, "bottle", RecipeIngredientRole.INPUT);
        bottle_slot.addIngredients(bottle);
        IRecipeSlotBuilder power_slot = slot_manager.buildSlot(builder, "power", RecipeIngredientRole.INPUT);
        power_slot.addIngredients(ReactiveJEIPlugin.POWER_TYPE, List.of(power));

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
