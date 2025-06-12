package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.alchemy.Power;

import java.util.Map;

public class StringMaterialProperty extends MaterialProperty<String>{
    @Override
    public boolean requirementsMet(Map<Power, Integer> formula) {
        return false;
    }

    @Override
    public Instance<String> instance(Map<Power, Integer> formula) {
        return null;
    }

    @Override
    public Codec<String> codec() {
        return Codec.STRING;
    }
}
