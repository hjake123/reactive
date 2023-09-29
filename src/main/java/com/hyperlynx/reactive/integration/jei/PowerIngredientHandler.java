package com.hyperlynx.reactive.integration.jei;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class PowerIngredientHandler implements IIngredientHelper<Power> {
    @Override
    public IIngredientType getIngredientType() {
        return ReactiveJEIPlugin.POWER_TYPE;
    }

    @Override
    public String getDisplayName(Power ingredient) {
        return ingredient.getName();
    }

    @Override
    public String getUniqueId(Power ingredient, UidContext context) {
        return ingredient.getId();
    }

    @Override
    public ResourceLocation getResourceLocation(Power ingredient) {
        return Powers.POWER_SUPPLIER.get().getKey(ingredient);
    }

    @Override
    public Power copyIngredient(Power ingredient) {
        return new Power(ingredient.getId(), ingredient.getColor(), ingredient.getBottle().getItem(), ingredient.getRenderStack().getItem());
    }

    @Override
    public String getErrorInfo(@Nullable Power ingredient) {
        if(ingredient == null)
            return "null Power";
        return ingredient.getId();
    }

    @Override
    public boolean isValidIngredient(Power ingredient) {
        return IIngredientHelper.super.isValidIngredient(ingredient);
    }
}
