package com.hyperlynx.reactive.integration.kubejs;

import com.hyperlynx.reactive.alchemy.Powers;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public interface CrucibleKubeEvent extends KubeEvent {
    abstract KubeCrucible getCrucible();

    default int getPowerLevel(String power_rl){
        var crucible = getCrucible().crucible;
        return crucible.getPowerLevel(Powers.POWER_REGISTRY.get(ResourceLocation.parse(power_rl)));
    }

    default boolean hasPower(String power_rl){
        return getPowerLevel(power_rl) > 0;
    }

    default BlockPos getCruciblePos(){
        return getCrucible().crucible.getBlockPos();
    }
}
