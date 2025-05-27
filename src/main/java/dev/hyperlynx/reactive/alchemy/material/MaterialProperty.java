package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.util.Unit;

import java.util.Map;

/// One of the types of Property a Material may possess
/// Offers a way to acquire MaterialProperty objects of any type T.
public abstract class MaterialProperty<T> {
    public abstract boolean requirementsMet(Map<Power, Integer> formula);
    public abstract Instance<T> instance(Map<Power, Integer> formula);
    public abstract Codec<T> codec();

    /// A specific instance of a Property that a certain Material has.
    /// Wrapper for a data object of type T, along with some extra data.
    public record Instance<T>(MaterialProperty<T> type, T value, float stability_multiplier){}
}
