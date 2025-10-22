package dev.hyperlynx.reactive.alchemy.material.formula;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/// Defines a way that a Formula can produce an instance for a particular MaterialProperty
public abstract class FormulaOutcome {
    public abstract @Nullable ResourceLocation power();
}
