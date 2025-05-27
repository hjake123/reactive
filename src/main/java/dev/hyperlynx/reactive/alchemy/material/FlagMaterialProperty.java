package dev.hyperlynx.reactive.alchemy.material;

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
        if(requirementsMet(formula)) {
            return new Instance<>(this, Unit.INSTANCE, 1.0F); // TODO calculate stability
        }
        return null;
    }
}
