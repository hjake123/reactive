package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.material.formula.FormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.MaterialFormulaMaps;
import dev.hyperlynx.reactive.alchemy.material.formula.StringFormulaOutcome;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/// Chooses the first from the list of options that matches the formula, in the order they're in the file
public class StringMaterialProperty extends MaterialProperty<String>{
    @Override
    public String instance(Map<Power, Integer> formula) {
        ResourceLocation id = MaterialProperties.PROPERTY_SUPPLIER.get().getKey(this);
        assert id != null;
        var outcomes = MaterialFormulaMaps.FORMULA_OUTCOME_MAP.get(id);
        if(outcomes == null || outcomes.isEmpty()) {
            ReactiveMod.LOGGER.error("No outcome map has been defined for {}, defaulting to empty string", id);
            return "";
        }
        for(FormulaOutcome outcome : outcomes) {
            if(!(outcome instanceof StringFormulaOutcome string_outcome)) {
                ReactiveMod.LOGGER.error("Outcome map for {} has a non-applicable outcome type set, skipping.", id);
                continue;
            }
            String result = string_outcome.calculate(formula);
            if(!result.isEmpty()) {
                return result;
            }
        }
        return "";
    }
}
