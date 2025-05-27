package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
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

    @Override
    public Codec<Integer> codec() {
        return Codec.INT;
    }
}
