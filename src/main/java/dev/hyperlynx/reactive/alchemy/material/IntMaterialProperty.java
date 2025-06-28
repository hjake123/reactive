package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.material.formula.IntegerFormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.IntegerZeroToMaxFormulaOutcome;
import dev.hyperlynx.reactive.registration.ReactiveDataMaps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class IntMaterialProperty extends MaterialProperty<Integer>{
    @Override
    public Integer instance(Map<Power, Integer> formula) {
        ResourceLocation id = MaterialProperties.PROPERTY_REGISTRY.getKey(this);
        assert id != null;
        var holder = MaterialProperties.PROPERTY_REGISTRY.getHolder(id);
        var outcome_map = holder.get().getData(ReactiveDataMaps.FORMULA_OUTCOME_MAP);
        if(outcome_map == null) {
            ReactiveMod.LOGGER.error("No outcome map has been defined for {}, defaulting to 0", id);
            return 0;
        }
        if(!(outcome_map instanceof IntegerFormulaOutcome outcome)) {
            ReactiveMod.LOGGER.error("Outcome map for {} has a non-applicable outcome type set, defaulting to 0", id);
            return 0;
        }
        return outcome.calculate(formula);
    }

    @Override
    public Codec<Integer> codec() {
        return Codec.INT;
    }
}
