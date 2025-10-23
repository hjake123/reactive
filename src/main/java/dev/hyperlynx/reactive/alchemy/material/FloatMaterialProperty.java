package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.material.formula.FloatFormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.FormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.MaterialFormulaMaps;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class FloatMaterialProperty extends MaterialProperty<Float>{
    @Override
    public Float instance(Map<Power, Integer> formula) {
        ResourceLocation id = MaterialProperties.PROPERTY_SUPPLIER.get().getKey(this);
        assert id != null;
        var outcomes = MaterialFormulaMaps.FORMULA_OUTCOME_MAP.get(id);
        if(outcomes == null || outcomes.isEmpty()) {
            ReactiveMod.LOGGER.error("No outcome map has been defined for {}, defaulting to 0.0", id);
            return 0.0F;
        }
        float value = 1.0F;
        for(FormulaOutcome outcome : outcomes) {
            if(!(outcome instanceof FloatFormulaOutcome float_outcome)) {
                ReactiveMod.LOGGER.error("Outcome map for {} has a non-applicable outcome type set, skipping.", id);
                continue;
            }
            value *= float_outcome.calculate(formula);
        }
        return value;
    }

    @Override
    public void save(CompoundTag tag, Object value) {
        if(value instanceof Float f) {
            tag.put(getTagName(), FloatTag.valueOf(f));
        }
    }

    @Override
    public Float fromTag(CompoundTag tag) {
        return tag.getFloat(getTagName());
    }
}
