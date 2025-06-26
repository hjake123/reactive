package dev.hyperlynx.reactive.alchemy.material.formula;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.function.Function;

/// Defines a way that a Formula can produce an instance for a particular MaterialProperty
public abstract class FormulaOutcome {
    public abstract MapCodec<? extends FormulaOutcome> type();
    public static final Codec<FormulaOutcome> CODEC = FormulaOutcomeTypes.TYPE_REGISTRY.byNameCodec().dispatch(FormulaOutcome::type, Function.identity());
}
