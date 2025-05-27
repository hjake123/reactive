package dev.hyperlynx.reactive.alchemy.material;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Map;

/// A particular Material that MaterialBlocks can have the properties of.
///
/// Each Material has a table of MaterialProperties that define its characteristics.
/// When a MaterialBlock queries its material, it can ask it for various block properties.
public class Material {
    Reference2ObjectMap<MaterialProperty<?>, Object> properties = new Reference2ObjectArrayMap<>();

    public boolean has(MaterialProperty<?> type) {
        return properties.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    // Requires suspicious cast to get around generic weirdness.
    // In practice only objects of T will associate with ResourceKeys involving T.
    public <T> T get(MaterialProperty<T> type) {
        return (T) properties.get(type);
    }

    public <T> T getOrDefault(MaterialProperty<T> key, T default_value) {
        if(has(key)) {
            return get(key);
        }
        return default_value;
    }
}
