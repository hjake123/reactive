package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.util.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPowerBearer extends INBTSerializable<CompoundTag> {
    boolean addPower(Power p, int amount);
    int getPowerLevel(Power t);
    boolean expendPower(Power t, int amount);
    Color getCombinedColor(int base);
}
