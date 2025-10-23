package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;

import java.util.Map;

public class FlagMaterialProperty extends MaterialProperty<Unit> {
    @Override
    public Unit instance(Map<Power, Integer> formula) {
        return Unit.INSTANCE;
    }

    @Override
    public void save(CompoundTag tag, Object value) {
        tag.putBoolean(getTagName(), true);
    }

    @Override
    public Unit fromTag(CompoundTag tag) {
        return Unit.INSTANCE;
    }
}
