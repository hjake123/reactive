//package dev.hyperlynx.reactive.integration.jei;
//
//import dev.hyperlynx.reactive.alchemy.Power;
//import mezz.jei.api.ingredients.IIngredientHelper;
//import mezz.jei.api.ingredients.IIngredientType;
//import mezz.jei.api.ingredients.subtypes.UidContext;
//import net.minecraft.resources.ResourceLocation;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//public class PowerIngredientHandler implements IIngredientHelper<Power> {
//    @Override
//    public @NotNull IIngredientType<Power> getIngredientType() {
//        return ReactiveJEIPlugin.POWER_TYPE;
//    }
//
//    @Override
//    public @NotNull String getDisplayName(Power ingredient) {
//        return ingredient.getName();
//    }
//
//    @SuppressWarnings("removal") // This method is necessary to implement the interface, so there's nothing to be done yet.
//    public @NotNull String getUniqueId(Power ingredient, @NotNull UidContext context) {
//        return ingredient.getId();
//    }
//
//    @Override
//    public @NotNull Object getUid(@NotNull Power ingredient, @NotNull UidContext context) {
//        return this.getUniqueId(ingredient, context);
//    }
//
//    @Override
//    public @NotNull ResourceLocation getResourceLocation(Power ingredient) {
//        return ingredient.getResourceLocation();
//    }
//
//    @Override
//    public @NotNull Power copyIngredient(Power ingredient) {
//        return new Power(ingredient.getResourceLocation(), ingredient.getColor(), ingredient.getWaterRenderBlock(), ingredient.getBottle().getItem(), ingredient.getRenderStack().getItem());
//    }
//
//    @Override
//    public @NotNull String getErrorInfo(@Nullable Power ingredient) {
//        if(ingredient == null)
//            return "null Power";
//        return ingredient.getId();
//    }
//
//    @Override
//    public boolean isValidIngredient(@NotNull Power ingredient) {
//        return IIngredientHelper.super.isValidIngredient(ingredient);
//    }
//}
