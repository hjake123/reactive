package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.material.formula.FormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.IntegerFormulaOutcome;
import dev.hyperlynx.reactive.registration.ReactiveDataMaps;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class IntMaterialProperty extends MaterialProperty<Integer>{
    @Override
    public Integer instance(Map<Power, Integer> formula) {
        ResourceLocation id = MaterialProperties.PROPERTY_REGISTRY.getKey(this);
        assert id != null;
        var holder = MaterialProperties.PROPERTY_REGISTRY.getHolder(id);
        if(holder.isEmpty()) {
            throw new RuntimeException("Can't retrieve the material property registry from location " + id);
        }
        List<FormulaOutcome> outcomes = holder.get().getData(ReactiveDataMaps.FORMULA_OUTCOME_MAP);
        if(outcomes == null || outcomes.isEmpty()) {
            ReactiveMod.LOGGER.error("No outcome map has been defined for {}, defaulting to 0", id);
            return 0;
        }
        if(outcomes.size() > 1) {
            ReactiveMod.LOGGER.error("Integer outcome maps do not support multiple outcomes in the list, ignoring all but the first.");
        }
        if(!(outcomes.getFirst() instanceof IntegerFormulaOutcome outcome)) {
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
