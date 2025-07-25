package dev.hyperlynx.reactive.integration.jei;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.registration.ReactiveRecipes;
import dev.hyperlynx.reactive.recipes.TransmuteRecipe;
import dev.hyperlynx.reactive.registration.ReactiveItems;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TransmuteRecipeCategory implements IRecipeCategory<RecipeHolder<TransmuteRecipe>> {
    final SlotManager slot_manager = new SlotManager();

    public TransmuteRecipeCategory() {
        slot_manager.addSlot("reactant", 1, 1);
        slot_manager.addSlot("product",55, 1);
        slot_manager.addSlot("center_reagent", 28, 22);
        slot_manager.addSlot("mid_left_reagent", 19, 22);
        slot_manager.addSlot("mid_right_reagent", 37, 22);
        slot_manager.addSlot("left_reagent", 10, 22);
        slot_manager.addSlot("right_reagent", 46, 22);
    }
    @Override
    public @Nullable ResourceLocation getRegistryName(@Nullable RecipeHolder<TransmuteRecipe> holder) {
        if(holder == null) {
            return null;
        }
        return holder.id();
    }

    @Override
    public RecipeType<RecipeHolder<TransmuteRecipe>> getRecipeType() {
        return RecipeType.createFromVanilla(ReactiveRecipes.TRANS_RECIPE_TYPE.get());
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("title.reactive.transmute");
    }

    public IDrawable background() {
        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawable(ReactiveMod.location("textures/gui/tf_jei.png"), 2, 2, 72, 39);
    }

    @Override
    public int getWidth() {
        return background().getWidth();
    }

    @Override
    public int getHeight() {
        return background().getHeight();
    }

    @Override
    public IDrawable getIcon() {
        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, ReactiveItems.CRUCIBLE.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TransmuteRecipe> holder, @Nullable IFocusGroup focuses) {
        TransmuteRecipe recipe = holder.value();

        IRecipeSlotBuilder input_slot = slot_manager.buildSlot(builder, "reactant", RecipeIngredientRole.INPUT);
        input_slot.addItemStacks(List.of(recipe.getReactant().getItems()));

        IRecipeSlotBuilder output_slot = slot_manager.buildSlot(builder, "product", RecipeIngredientRole.OUTPUT);
        assert Minecraft.getInstance().level != null;
        output_slot.addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));

        switch (recipe.getReagents().size()) {
            case 1 -> {
                IRecipeSlotBuilder power_slot = slot_manager.buildSlot(builder, "center_reagent", RecipeIngredientRole.CATALYST);
                power_slot.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().getFirst());
            }
            case 2 -> {
                IRecipeSlotBuilder power_slotl = slot_manager.buildSlot(builder, "mid_left_reagent", RecipeIngredientRole.CATALYST);
                power_slotl.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().getFirst());

                IRecipeSlotBuilder power_slotr = slot_manager.buildSlot(builder, "mid_right_reagent", RecipeIngredientRole.CATALYST);
                power_slotr.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(1));
            }
            case 3 -> {
                IRecipeSlotBuilder power_slotl = slot_manager.buildSlot(builder, "left_reagent", RecipeIngredientRole.CATALYST);
                power_slotl.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().getFirst());

                IRecipeSlotBuilder power_slot = slot_manager.buildSlot(builder, "center_reagent", RecipeIngredientRole.CATALYST);
                power_slot.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(1));

                IRecipeSlotBuilder power_slotr = slot_manager.buildSlot(builder, "right_reagent", RecipeIngredientRole.CATALYST);
                power_slotr.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(2));
            }
            default -> {
                IRecipeSlotBuilder power_slot = slot_manager.buildSlot(builder, "center_reagent", RecipeIngredientRole.CATALYST);
                power_slot.addIngredients(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents());
            }
        }
    }

    @Override
    public void draw(RecipeHolder<TransmuteRecipe> holder, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics gui, double mouseX, double mouseY) {
        background().draw(gui);
        if(holder.value().needs_electricity){
            drawElectricLabel(gui);
        }
        slot_manager.drawSlotBackgrounds(gui, List.of("reactant", "product"));
        switch (holder.value().getReagents().size()) {
            case 2 -> slot_manager.drawSlotBackgrounds(gui, List.of("mid_left_reagent", "mid_right_reagent"));
            case 3 -> slot_manager.drawSlotBackgrounds(gui, List.of("left_reagent", "center_reagent", "right_reagent"));
            default -> slot_manager.drawSlotBackground(gui, "center_reagent");
        }
    }

    private void drawElectricLabel(GuiGraphics gui) {
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.font.width("Charge");
        int center = getWidth() / 2;
        int x = center - (width / 2);
        int y = 11;
        gui.drawString(minecraft.font, "Charge",  x, y,0x0DA8A8);
    }
}
