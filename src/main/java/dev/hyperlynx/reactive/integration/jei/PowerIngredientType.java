package dev.hyperlynx.reactive.integration.jei;

import dev.hyperlynx.reactive.alchemy.Power;
import mezz.jei.api.ingredients.IIngredientType;
import org.jetbrains.annotations.NotNull;

public class PowerIngredientType implements IIngredientType<Power> {
    @Override
    public @NotNull Class getIngredientClass() {
        return Power.class;
    }
}
