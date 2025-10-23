package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.material.formula.MaterialFormulaMaps;
import dev.hyperlynx.reactive.alchemy.material.formula.PropertyFormulaRequirements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/// One of the types of Property a Material may possess
/// Offers a way to acquire MaterialProperty objects of any type T.
public abstract class MaterialProperty<T> {
    public boolean requirementsMet(Map<Power, Integer> formula) {
        ResourceLocation id = MaterialProperties.PROPERTY_SUPPLIER.get().getKey(this);
        assert id != null;
        var requirement_map = MaterialFormulaMaps.PROPERTY_FORMULA_MAP.get(id);
        if(requirement_map == null) {
            ReactiveMod.LOGGER.error("No requirement map has been defined for {}", id);
            return false;
        }
        for(PropertyFormulaRequirements.Part requirement : requirement_map.requirements()) {
            Power power = Powers.POWER_SUPPLIER.get().getValue(requirement.power_id());
            if(power == null) {
                ReactiveMod.LOGGER.error("Invalid power {} in requirement map for {}", requirement.power_id(), id);
                return false;
            }

            if(!formula.containsKey(power) || formula.get(power) < requirement.lowBound(id)) {
                return false;
            }
        }
        return true;
    }

    public abstract T instance(Map<Power, Integer> formula);

    protected ResourceLocation getId() {
        return MaterialProperties.PROPERTY_SUPPLIER.get().getKey(this);
    }

    protected String getTagName() {
        ResourceLocation id = getId();
        return id.getNamespace() + "_" + id.getPath();
    }

    public abstract void save(CompoundTag tag, Object value);

    public abstract T fromTag(CompoundTag tag);
}
