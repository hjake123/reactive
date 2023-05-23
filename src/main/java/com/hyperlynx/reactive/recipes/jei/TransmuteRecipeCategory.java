package com.hyperlynx.reactive.recipes.jei;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.recipes.TransmuteRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TransmuteRecipeCategory implements IRecipeCategory<TransmuteRecipe> {

    @Override
    public @Nullable ResourceLocation getRegistryName(TransmuteRecipe recipe) {
        return new ResourceLocation(ReactiveMod.MODID, "transmute");
    }

    @Override
    public RecipeType<TransmuteRecipe> getRecipeType() {
        return RecipeType.create(ReactiveMod.MODID, "transmute", TransmuteRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.reactive.transmute");
    }

    @Override
    public IDrawable getBackground() {
        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawable(new ResourceLocation(ReactiveMod.MODID, "textures/gui/tf_jei.png"), 2, 2, 76, 49);
    }

    @Override
    public IDrawable getIcon() {
        return  ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, Registration.CRUCIBLE_ITEM.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TransmuteRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder input_slot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 1);
        IRecipeSlotBuilder output_slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 1);
        input_slot.setSlotName("reactant");
        input_slot.addItemStacks(List.of(recipe.getReactant().getItems()));
        output_slot.setSlotName("product");
        output_slot.addItemStack(recipe.getResultItem());

    }

    @Override
    public void draw(TransmuteRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        List<String> reagent_list = new ArrayList<>();
        for(Power reagent : recipe.getReagents()){
            reagent_list.add(reagent.getName());
        }
        Minecraft minecraft = Minecraft.getInstance();
        drawReagentLabel(minecraft, stack, reagent_list.toString().substring(1, reagent_list.toString().length()-1));
        if(recipe.needs_electricity){
            drawElectricLabel(minecraft, stack);
        }
    }

    private void drawReagentLabel(Minecraft minecraft, PoseStack poseStack, String label) {
        int width = minecraft.font.width(label);
        int center = getBackground().getWidth() / 2;
        int x = center - (width / 2);
        int y = 41;
        minecraft.font.draw(poseStack, label, x, y,0xFF3838);
    }

    private void drawElectricLabel(Minecraft minecraft, PoseStack poseStack) {
        int width = minecraft.font.width("Needs Charge");
        int center = getBackground().getWidth() / 2;
        int x = center - (width / 2);
        int y = 29;
        minecraft.font.drawShadow(poseStack, "Needs Charge", x, y,0x0DA8A8);
    }
}
