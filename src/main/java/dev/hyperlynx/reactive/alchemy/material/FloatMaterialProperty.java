package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public class FloatMaterialProperty extends MaterialProperty<Float>{
    @Override
    public Float instance(Map<Power, Integer> formula) {
        return null;
    }

    @Override
    public Codec<Float> codec() {
        return Codec.FLOAT;
    }
}
