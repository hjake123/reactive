package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.world.level.block.SoundType;

import java.util.Map;

public class SoundTypeProperty extends MaterialProperty<SoundType> {
    @Override
    public boolean requirementsMet(Map<Power, Integer> formula) {
        return false;
    }

    @Override
    public Instance<SoundType> instance(Map<Power, Integer> formula) {
        return null;
    }
}
