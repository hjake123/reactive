package com.hyperlynx.reactive.util;

import net.minecraft.world.level.Level;

// This is a wrapper for making a WSV where everything except the world is predetermined by class fields.
public class PrimedWSV {
    private final String alias;
    private final int max;
    private final int min;

    public PrimedWSV(String alias, int min, int max){
        this.alias = alias;
        this.max = max;
        this.min = min;
    }

    public int get(Level l){
        return WorldSpecificValue.get(l, alias, min, max);
    }
}
