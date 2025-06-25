package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public class IntMaterialProperty extends MaterialProperty<Integer>{
    @Override
    public Integer instance(Map<Power, Integer> formula) {
        return null;
    }

    @Override
    public Codec<Integer> codec() {
        return Codec.INT;
    }
}
