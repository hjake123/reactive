package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.alchemy.Power;

import java.util.Map;

public class IntMaterialProperty extends MaterialProperty<Integer>{
    @Override
    public boolean requirementsMet(Map<Power, Integer> formula) {
        return false;
    }

    @Override
    public Instance<Integer> instance(Map<Power, Integer> formula) {
        return null;
    }
}
