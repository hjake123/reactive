package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.alchemy.Power;

import java.util.Map;

public class FloatMaterialProperty extends MaterialProperty<Float>{
    @Override
    public boolean requirementsMet(Map<Power, Integer> formula) {
        return false;
    }

    @Override
    public Instance<Float> instance(Map<Power, Integer> formula) {
        return null;
    }
}
