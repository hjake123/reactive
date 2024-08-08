package com.hyperlynx.reactive.alchemy;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface PowerBearer {
    boolean addPower(Power p, int amount);
    int getPowerLevel(Power t);
    int getTotalPowerLevel();
    boolean expendPower(Power t, int amount);
    void expendAnyPowerExcept(Power immune_power, int amount);
    void expendPower();

    @NotNull Map<Power, Integer> getPowerMap();
}
