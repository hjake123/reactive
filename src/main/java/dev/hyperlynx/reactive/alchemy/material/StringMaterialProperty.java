package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public class StringMaterialProperty extends MaterialProperty<String>{
    @Override
    public boolean requirementsMet(Map<Power, Integer> formula) {
        // String properties should never be automatically applied.
        return false;
    }

    @Override
    public String instance(Map<Power, Integer> formula) {
        return "";
    }

    @Override
    public Codec<String> codec() {
        return Codec.STRING;
    }
}
