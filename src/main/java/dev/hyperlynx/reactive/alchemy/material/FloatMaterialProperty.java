package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.material.formula.FloatFormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.FloatZeroToMaxFormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.FormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.IntegerZeroToMaxFormulaOutcome;
import dev.hyperlynx.reactive.registration.ReactiveDataMaps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class FloatMaterialProperty extends MaterialProperty<Float>{
    @Override
    public Float instance(Map<Power, Integer> formula) {
        ResourceLocation id = MaterialProperties.PROPERTY_REGISTRY.getKey(this);
        assert id != null;
        var holder = MaterialProperties.PROPERTY_REGISTRY.getHolder(id);
        var outcomes = holder.get().getData(ReactiveDataMaps.FORMULA_OUTCOME_MAP);
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
    public Codec<Float> codec() {
        return Codec.FLOAT;
    }
}
