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
        slot_manager.builder("one_reagent").addSlot(1, 1).addSlot(55, 1).addSlot(28, 22).build();
        slot_manager.builder("two_reagents").addSlot(1, 1).addSlot(55, 1)
                .addSlot(19, 22).addSlot(37, 22).build();
        slot_manager.builder("three_reagents").addSlot(1, 1).addSlot(55, 1)
                .addSlot(10, 22).addSlot(28, 22).addSlot(46, 22).build();
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
        return  ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, Registration.CRUCIBLE_ITEM.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TransmuteRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder input_slot = slot_manager.buildSlot(builder, "one_reagent", 0, RecipeIngredientRole.INPUT);
        input_slot.setSlotName("reactant");
        input_slot.addItemStacks(List.of(recipe.getReactant().getItems()));

        IRecipeSlotBuilder output_slot = slot_manager.buildSlot(builder, "one_reagent", 1, RecipeIngredientRole.OUTPUT);
        output_slot.setSlotName("product");
        output_slot.addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));

        switch (recipe.getReagents().size()) {
            case 1 -> {
                IRecipeSlotBuilder power_slot = slot_manager.buildSlot(builder, "one_reagent", 3, RecipeIngredientRole.CATALYST);
                power_slot.setSlotName("reagent_middle");
                power_slot.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(0));
            }
            case 2 -> {
                IRecipeSlotBuilder power_slotl = slot_manager.buildSlot(builder, "two_reagents", 3, RecipeIngredientRole.CATALYST);
                power_slotl.setSlotName("reagent_left");
                power_slotl.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(0));

                IRecipeSlotBuilder power_slotr = slot_manager.buildSlot(builder, "two_reagents", 4, RecipeIngredientRole.CATALYST);
                power_slotr.setSlotName("reagent_right");
                power_slotr.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(1));
            }
            case 3 -> {
                IRecipeSlotBuilder power_slotl = slot_manager.buildSlot(builder, "three_reagents", 3, RecipeIngredientRole.CATALYST);
                power_slotl.setSlotName("reagent_left");
                power_slotl.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(0));

                IRecipeSlotBuilder power_slot = slot_manager.buildSlot(builder, "three_reagents", 4, RecipeIngredientRole.CATALYST);
                power_slot.setSlotName("reagent_middle");
                power_slot.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(1));

                IRecipeSlotBuilder power_slotr = slot_manager.buildSlot(builder, "three_reagents", 5, RecipeIngredientRole.CATALYST);
                power_slotr.setSlotName("reagent_right");
                power_slotr.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(2));
            }
            default -> {
                IRecipeSlotBuilder power_slot = slot_manager.buildSlot(builder, "one_reagent", 3, RecipeIngredientRole.CATALYST);
                power_slot.setSlotName("reagents");
                power_slot.addIngredients(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents());
            }
        }
    }

    @Override
    public void draw(TransmuteRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        if(recipe.needs_electricity){
            drawElectricLabel(gui);
        }
        switch (recipe.getReagents().size()) {
            case 2 -> slot_manager.drawSlotBackgrounds(gui, "two_reagents");
            case 3 -> slot_manager.drawSlotBackgrounds(gui, "three_reagents");
            default -> slot_manager.drawSlotBackgrounds(gui, "one_reagent");
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
