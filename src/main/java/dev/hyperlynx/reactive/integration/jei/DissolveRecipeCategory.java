//package dev.hyperlynx.reactive.integration.jei;
//
//import dev.hyperlynx.reactive.ReactiveMod;
//import dev.hyperlynx.reactive.Registration;
//import dev.hyperlynx.reactive.alchemy.Power;
//import dev.hyperlynx.reactive.recipes.DissolveRecipe;
//import mezz.jei.api.constants.VanillaTypes;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
//import mezz.jei.api.gui.drawable.IDrawable;
//import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
//import mezz.jei.api.recipe.IFocusGroup;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import mezz.jei.api.recipe.RecipeType;
//import mezz.jei.api.recipe.category.IRecipeCategory;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import dev.hyperlynx.reactive.ConfigMan;
//
//import java.util.List;
//
//public class DissolveRecipeCategory implements IRecipeCategory<DissolveRecipe> {
//    @Override
//    public @Nullable ResourceLocation getRegistryName(@Nullable DissolveRecipe recipe) {
//        return ReactiveMod.location("dissolve");
//    }
//
//    @Override
//    public @NotNull RecipeType<DissolveRecipe> getRecipeType() {
//        return RecipeType.create(ReactiveMod.MODID, "dissolve", DissolveRecipe.class);
//    }
//
//    @Override
//    public @NotNull Component getTitle() {
//        return Component.translatable("title.reactive.dissolve");
//    }
//
//    public IDrawable background() {
//        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawable(ReactiveMod.location("textures/gui/tf_jei.png"), 2, 2, 76, 38);
//    }
//
//    @Override
//    public int getHeight() {
//        return background().getHeight();
//    }
//
//    @Override
//    public int getWidth() {
//        return background().getWidth();
//    }
//
//    @Override
//    public IDrawable getIcon() {
//        return  ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, Registration.CRUCIBLE_ITEM.get().getDefaultInstance());
//    }
//
//    @Override
//    public void setRecipe(IRecipeLayoutBuilder builder, DissolveRecipe recipe, @NotNull IFocusGroup focuses) {
//        IRecipeSlotBuilder input_slot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 1);
//        IRecipeSlotBuilder output_slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 1);
//        input_slot.setSlotName("reactant");
//        input_slot.addItemStacks(List.of(recipe.getReactant().getItems()));
//        output_slot.setSlotName("product");
//        output_slot.addItemStack(recipe.getProduct());
//
//        if(ConfigMan.CLIENT.showPowerSources.get()){
//            IRecipeSlotBuilder power_slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 20);
//            power_slot.setSlotName("power_result");
//            for (ItemStack input : recipe.getReactant().getItems()) {
//                power_slot.addIngredients(ReactiveJEIPlugin.POWER_TYPE, Power.getSourcePower(input));
//            }
//        }
//    }
//
//    @Override
//    public void draw(DissolveRecipe recipe, @Nullable IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics gui, double mouseX, double mouseY) {
//        background().draw(gui);
//        if(recipe.needs_electricity){
//            drawElectricLabel(gui);
//        }
//    }
//
//    private void drawElectricLabel(GuiGraphics gui) {
//        Minecraft minecraft = Minecraft.getInstance();
//        int width = minecraft.font.width("Charge");
//        int center = getWidth() / 2;
//        int x = center - (width / 2);
//        int y = 11;
//        gui.drawString(minecraft.font, "Charge",  x, y,0x0DA8A8);
//    }
//}
