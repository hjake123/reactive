package dev.hyperlynx.reactive.integration.jei;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.recipes.DissolveRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TransmuteRecipeCategory implements IRecipeCategory<RecipeHolder<TransmuteRecipe>> {
    @Override
    public @Nullable ResourceLocation getRegistryName(@Nullable RecipeHolder<TransmuteRecipe> holder) {
        return holder.id();
    }

    @Override
    public RecipeType<RecipeHolder<TransmuteRecipe>> getRecipeType() {
        return RecipeType.createFromVanilla(Registration.TRANS_RECIPE_TYPE.get());
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
        return  ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, Registration.CRUCIBLE_ITEM.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TransmuteRecipe> holder, @Nullable IFocusGroup focuses) {
        TransmuteRecipe recipe = holder.value();

        IRecipeSlotBuilder input_slot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 1);
        input_slot.setSlotName("reactant");
        input_slot.addItemStacks(List.of(recipe.getReactant().getItems()));
        input_slot.setStandardSlotBackground();

        IRecipeSlotBuilder output_slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 1);
        output_slot.setSlotName("product");
        assert Minecraft.getInstance().level != null;
        output_slot.addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
        output_slot.setStandardSlotBackground();

        switch (recipe.getReagents().size()) {
            case 1 -> {
                IRecipeSlotBuilder power_slot = builder.addSlot(RecipeIngredientRole.CATALYST, 28, 22);
                power_slot.setSlotName("reagent_middle");
                power_slot.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().getFirst());
                power_slot.setStandardSlotBackground();
            }
            case 2 -> {
                IRecipeSlotBuilder power_slotl = builder.addSlot(RecipeIngredientRole.CATALYST, 19, 22);
                power_slotl.setSlotName("reagent_left");
                power_slotl.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().getFirst());
                power_slotl.setStandardSlotBackground();

                IRecipeSlotBuilder power_slotr = builder.addSlot(RecipeIngredientRole.CATALYST, 37, 22);
                power_slotr.setSlotName("reagent_right");
                power_slotr.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(1));
                power_slotr.setStandardSlotBackground();
            }
            case 3 -> {
                IRecipeSlotBuilder power_slotl = builder.addSlot(RecipeIngredientRole.CATALYST, 10, 22);
                power_slotl.setSlotName("reagent_left");
                power_slotl.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().getFirst());
                power_slotl.setStandardSlotBackground();

                IRecipeSlotBuilder power_slot = builder.addSlot(RecipeIngredientRole.CATALYST, 28, 22);
                power_slot.setSlotName("reagent_middle");
                power_slot.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(1));
                power_slot.setStandardSlotBackground();

                IRecipeSlotBuilder power_slotr = builder.addSlot(RecipeIngredientRole.CATALYST, 46, 22);
                power_slotr.setSlotName("reagent_right");
                power_slotr.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(2));
                power_slotr.setStandardSlotBackground();
            }
            default -> {
                IRecipeSlotBuilder power_slot = builder.addSlot(RecipeIngredientRole.CATALYST, 28, 22);
                power_slot.setSlotName("reagents");
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
