package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.util.Color;

import java.util.Map;

public class ColorMaterialProperty extends MaterialProperty<Color> {
    @Override
    public boolean requirementsMet(Map<Power, Integer> formula) {
        return false;
    }

    @Override
    public Instance<Color> instance(Map<Power, Integer> formula) {
        return null;
    }

    @Override
    public Codec<Color> codec() {
        return Color.CODEC;
    }
}
