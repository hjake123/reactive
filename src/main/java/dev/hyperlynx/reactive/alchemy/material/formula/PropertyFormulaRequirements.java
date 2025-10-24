package dev.hyperlynx.reactive.alchemy.material.formula;

import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/// A Data Map entry that describes what Powers are needed to obtain a particular `MaterialProperty`.
public record PropertyFormulaRequirements(List<Part> requirements) {

    /// Defines a particular Power and a pair of ranges, one for the low bound (the least Power to get this property) and one for the high bound.
    /// The actual range is world specific within these constraints
    /// The high bound may be `Optional.empty()`, in which case there is no high bound.
    public record Part(ResourceLocation power_id, int low_bound_minimum, int low_bound_maximum) {
        public int lowBound(ResourceLocation id) {
            return WorldSpecificValue.get(id + "REQL" + power_id().toString(),
                    low_bound_minimum(),low_bound_maximum());
        }
    }
}
