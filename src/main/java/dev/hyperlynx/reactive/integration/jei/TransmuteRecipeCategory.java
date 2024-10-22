//package dev.hyperlynx.reactive.integration.jei;
//
//import dev.hyperlynx.reactive.ReactiveMod;
//import dev.hyperlynx.reactive.Registration;
//import dev.hyperlynx.reactive.recipes.TransmuteRecipe;
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
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.List;
//
//public class TransmuteRecipeCategory implements IRecipeCategory<TransmuteRecipe> {
//
//    @Override
//    public @Nullable ResourceLocation getRegistryName(@Nullable TransmuteRecipe recipe) {
//        return ReactiveMod.location("transmute");
//    }
//
//    @Override
//    public @NotNull RecipeType<TransmuteRecipe> getRecipeType() {
//        return RecipeType.create(ReactiveMod.MODID, "transmute", TransmuteRecipe.class);
//    }
//
//    @Override
//    public @NotNull Component getTitle() {
//        return Component.translatable("title.reactive.transmute");
//    }
//
//    public IDrawable background() {
//        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawable(ReactiveMod.location("textures/gui/tf_jei.png"), 2, 2, 76, 38);
//    }
//
//    @Override
//    public int getWidth() {
//        return background().getWidth();
//    }
//
//    @Override
//    public int getHeight() {
//        return background().getHeight();
//    }
//
//    @Override
//    public IDrawable getIcon() {
//        return  ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, Registration.CRUCIBLE_ITEM.get().getDefaultInstance());
//    }
//
//    @Override
//    public void setRecipe(IRecipeLayoutBuilder builder, TransmuteRecipe recipe, @Nullable IFocusGroup focuses) {
//        IRecipeSlotBuilder input_slot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 1);
//        input_slot.setSlotName("reactant");
//        input_slot.addItemStacks(List.of(recipe.getReactant().getItems()));
//
//        IRecipeSlotBuilder output_slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 1);
//        output_slot.setSlotName("product");
//        assert Minecraft.getInstance().level != null;
//        output_slot.addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
//
//        switch (recipe.getReagents().size()) {
//            case 1 -> {
//                IRecipeSlotBuilder power_slot = builder.addSlot(RecipeIngredientRole.CATALYST, 28, 24);
//                power_slot.setSlotName("reagent_middle");
//                power_slot.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().getFirst());
//            }
//            case 2 -> {
//                IRecipeSlotBuilder power_slotl = builder.addSlot(RecipeIngredientRole.CATALYST, 20, 24);
//                power_slotl.setSlotName("reagent_left");
//                power_slotl.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().getFirst());
//
//                IRecipeSlotBuilder power_slotr = builder.addSlot(RecipeIngredientRole.CATALYST, 36, 24);
//                power_slotr.setSlotName("reagent_right");
//                power_slotr.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(1));
//            }
//            case 3 -> {
//                IRecipeSlotBuilder power_slotl = builder.addSlot(RecipeIngredientRole.CATALYST, 12, 24);
//                power_slotl.setSlotName("reagent_left");
//                power_slotl.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().getFirst());
//
//                IRecipeSlotBuilder power_slot = builder.addSlot(RecipeIngredientRole.CATALYST, 28, 24);
//                power_slot.setSlotName("reagent_middle");
//                power_slot.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(1));
//
//                IRecipeSlotBuilder power_slotr = builder.addSlot(RecipeIngredientRole.CATALYST, 44, 24);
//                power_slotr.setSlotName("reagent_right");
//                power_slotr.addIngredient(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents().get(2));
//            }
//            default -> {
//                IRecipeSlotBuilder power_slot = builder.addSlot(RecipeIngredientRole.CATALYST, 28, 24);
//                power_slot.setSlotName("reagents");
//                power_slot.addIngredients(ReactiveJEIPlugin.POWER_TYPE, recipe.getReagents());
//            }
//        }
//    }
//
//    @Override
//    public void draw(TransmuteRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics gui, double mouseX, double mouseY) {
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
