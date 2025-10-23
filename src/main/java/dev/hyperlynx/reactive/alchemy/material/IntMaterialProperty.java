package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.material.formula.IntegerFormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.MaterialFormulaMaps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class IntMaterialProperty extends MaterialProperty<Integer>{
    @Override
    public Integer instance(Map<Power, Integer> formula) {
        ResourceLocation id = MaterialProperties.PROPERTY_SUPPLIER.get().getKey(this);
        assert id != null;
        var outcomes = MaterialFormulaMaps.FORMULA_OUTCOME_MAP.get(id);
        if(outcomes == null || outcomes.isEmpty()) {
            ReactiveMod.LOGGER.error("No outcome map has been defined for {}, defaulting to 0", id);
            return 0;
        }
        if(outcomes.size() > 1) {
            ReactiveMod.LOGGER.error("Integer outcome maps do not support multiple outcomes in the list, ignoring all but the first.");
        }
        if(!(outcomes.get(0) instanceof IntegerFormulaOutcome outcome)) {
            ReactiveMod.LOGGER.error("Outcome map for {} has a non-applicable outcome type set, defaulting to 0", id);
            return 0;
        }
        return outcome.calculate(formula);
    }

    @Override
    public void save(CompoundTag tag, Object value) {
        if(value instanceof Integer i) {
            tag.put(getTagName(), IntTag.valueOf(i));
        }
    }

    @Override
    public Integer fromTag(CompoundTag tag) {
        return tag.getInt(getTagName());
    }
}
