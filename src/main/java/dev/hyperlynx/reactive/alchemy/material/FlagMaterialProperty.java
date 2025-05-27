package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.util.Unit;

import java.util.Map;

public class FlagMaterialProperty extends MaterialProperty<Unit> {
    @Override
    public boolean requirementsMet(Map<Power, Integer> formula) {
        return false;
    }

    @Override
    public Instance<Unit> instance(Map<Power, Integer> formula) {
        return null;
    }

    @Override
    public Codec<Unit> codec() {
        return Unit.CODEC;
    }
}
