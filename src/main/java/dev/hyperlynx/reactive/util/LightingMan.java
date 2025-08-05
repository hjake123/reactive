package dev.hyperlynx.reactive.util;

import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LightingMan implements AuxiliaryLightManager {
    private final Map<BlockPos, Byte> lights = new ConcurrentHashMap<>();

    @Override
    public void setLightAt(BlockPos pos, int value) {
        if(value == 0) {
            lights.remove(pos);
        } else {
            lights.put(pos, (byte) value);
        }
    }

    @Override
    public int getLightAt(BlockPos pos) {
        if(lights.containsKey(pos))
            return lights.get(pos);
        return 0;
    }
}
