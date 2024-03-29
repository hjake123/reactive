package com.hyperlynx.reactive.integration.jei;

import com.hyperlynx.reactive.alchemy.Power;
import mezz.jei.api.ingredients.IIngredientType;

public class PowerIngredientType implements IIngredientType<Power> {
    @Override
    public Class getIngredientClass() {
        return Power.class;
    }
}
