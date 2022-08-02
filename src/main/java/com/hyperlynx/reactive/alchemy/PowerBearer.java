package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.util.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface PowerBearer extends INBTSerializable<CompoundTag> {
    boolean addPower(Power p, int amount);
    int getPowerLevel(Power t);
    int getTotalPowerLevel();
    boolean expendPower(Power t, int amount);
    void expendAnyPowerExcept(Power immune_power, int amount);
    void expendPower();
    Color getCombinedColor(int base);
    void setDirty();
    @NotNull Map<Power, Integer> getPowerMap();
}
