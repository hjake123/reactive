package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

/// A Data Map entry that describes what Powers are needed to obtain a particular `MaterialProperty`.
public record PropertyFormulaRequirement(List<Part> requirements) {
    public static final Codec<PropertyFormulaRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Part.CODEC.listOf().fieldOf("requirements").forGetter(PropertyFormulaRequirement::requirements)
    ).apply(instance, PropertyFormulaRequirement::new));

    /// Defines a particular Power and a pair of ranges, one for the low bound (least Power to get this property) and one for the high bound.
    /// The actual range is world specific within these constraints
    /// The high bound may be `Optional.empty()`, in which case there is no high bound.
    public record Part(ResourceLocation power_id, int low_bound_minimum, int low_bound_maximum, Optional<Integer> high_bound_minimum, Optional<Integer> high_bound_maximum) {
        public static final Codec<Part> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("power").forGetter(Part::power_id),
                Codec.INT.fieldOf("low_bound_minimum").forGetter(Part::low_bound_minimum),
                Codec.INT.fieldOf("low_bound_maximum").forGetter(Part::low_bound_maximum),
                Codec.INT.optionalFieldOf("high_bound_minimum").forGetter(Part::high_bound_minimum),
                Codec.INT.optionalFieldOf("high_bound_maximum").forGetter(Part::high_bound_maximum)
                ).apply(instance, Part::new));
    }
}
