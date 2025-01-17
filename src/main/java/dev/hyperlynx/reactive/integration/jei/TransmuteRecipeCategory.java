package dev.hyperlynx.reactive.integration.jei;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.recipes.TransmuteRecipe;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TransmuteRecipeCategory implements IRecipeCategory<TransmuteRecipe> {
    SlotManager slot_manager = new SlotManager();

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
    public @Nullable ResourceLocation getRegistryName(TransmuteRecipe recipe) {
        return recipe.getId();
    }

    @Override
    public RecipeType<TransmuteRecipe> getRecipeType() {
        return RecipeType.create(ReactiveMod.MODID, "transmute", TransmuteRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.reactive.transmute");
    }

    public IDrawable background() {
        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawable(ReactiveMod.location("textures/gui/tf_jei.png"), 2, 2, 72, 39);
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return background();
    }

    @Override
    public IDrawable getIcon() {
        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, Registration.CRUCIBLE_ITEM.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TransmuteRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder input_slot = slot_manager.buildSlot(builder, "reactant", RecipeIngredientRole.INPUT);
        input_slot.addItemStacks(List.of(recipe.getReactant().getItems()));

        IRecipeSlotBuilder output_slot = slot_manager.buildSlot(builder, "product", RecipeIngredientRole.OUTPUT);
        output_slot.addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));

        switch (recipe.getReagents().size()) {
            case 1 -> {
                IRecipeSlotBuilder power_slot = slot_manager.buildSlot(builder, "center_reagent", RecipeIngredientRole.CATALYST);
                power_slot.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(0));
            }
            case 2 -> {
                IRecipeSlotBuilder power_slotl = slot_manager.buildSlot(builder, "mid_left_reagent", RecipeIngredientRole.CATALYST);
                power_slotl.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(0));

                IRecipeSlotBuilder power_slotr = slot_manager.buildSlot(builder, "mid_right_reagent", RecipeIngredientRole.CATALYST);
                power_slotr.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(1));
            }
            case 3 -> {
                IRecipeSlotBuilder power_slotl = slot_manager.buildSlot(builder, "left_reagent", RecipeIngredientRole.CATALYST);
                power_slotl.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(0));

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
    public void draw(TransmuteRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        if(recipe.needs_electricity){
            drawElectricLabel(gui);
        }
        slot_manager.drawSlotBackgrounds(gui, List.of("reactant", "product"));
        switch (recipe.getReagents().size()) {
            case 2 -> slot_manager.drawSlotBackgrounds(gui, List.of("mid_left_reagent", "mid_right_reagent"));
            case 3 -> slot_manager.drawSlotBackgrounds(gui, List.of("left_reagent", "center_reagent", "right_reagent"));
            default -> slot_manager.drawSlotBackground(gui, "center_reagent");
        }
    }

    private void drawElectricLabel(GuiGraphics gui) {
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.font.width("Charge");
        int center = getBackground().getWidth() / 2;
        int x = center - (width / 2);
        int y = 11;
        gui.drawString(minecraft.font, "Charge",  x, y,0x0DA8A8);
    }
}
