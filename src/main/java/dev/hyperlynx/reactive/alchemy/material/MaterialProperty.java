package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.material.formula.PropertyFormulaRequirements;
import dev.hyperlynx.reactive.registration.ReactiveDataMaps;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/// One of the types of Property a Material may possess
/// Offers a way to acquire MaterialProperty objects of any type T.
public abstract class MaterialProperty<T> {
    public boolean requirementsMet(Map<Power, Integer> formula) {
        ResourceLocation id = MaterialProperties.PROPERTY_REGISTRY.getKey(this);
        assert id != null;
        var holder = MaterialProperties.PROPERTY_REGISTRY.getHolder(id);
        if(holder.isEmpty()) {
            throw new RuntimeException("Can't retrieve the material property registry from location " + id);
        }
        var requirement_map = holder.get().getData(ReactiveDataMaps.PROPERTY_FORMULA_MAP);
        if(requirement_map == null) {
            ReactiveMod.LOGGER.error("No requirement map has been defined for {}", id);
            return false;
        }
        for(PropertyFormulaRequirements.Part requirement : requirement_map.requirements()) {
            Power power = Powers.POWER_REGISTRY.get(requirement.power_id());
            if(power == null) {
                ReactiveMod.LOGGER.error("Invalid power {} in requirement map for {}", requirement.power_id(), id);
                return false;
            }

            int low_bound = WorldSpecificValue.get(id + "REQL" + requirement.power_id().toString(),
                    requirement.low_bound_minimum(), requirement.low_bound_maximum());
            if(!formula.containsKey(power) || formula.get(power) < low_bound) {
                return false;
            }

            if(requirement.high_bound_minimum().isPresent() && requirement.high_bound_maximum().isPresent()) {
                int high_bound = WorldSpecificValue.get(id + "REQH" + requirement.power_id(),
                        requirement.high_bound_minimum().get(), requirement.high_bound_maximum().get());
                if(formula.get(power) > high_bound) {
                    return false;
                }
            }
        }
        return true;
    }

    public abstract T instance(Map<Power, Integer> formula);
    public abstract Codec<T> codec();
}
