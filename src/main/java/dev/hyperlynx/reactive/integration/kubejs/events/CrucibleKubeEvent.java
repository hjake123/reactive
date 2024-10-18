package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.integration.kubejs.KubeCrucible;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface CrucibleKubeEvent extends KubeEvent {
    abstract KubeCrucible getCrucible();

    default int getPowerLevel(String power_rl){
        var crucible = getCrucible().crucible;
        return crucible.getPowerLevel(Powers.POWER_REGISTRY.get(ResourceLocation.parse(power_rl)));
    }

    default boolean hasPower(String power_rl){
        return getPowerLevel(power_rl) > 0;
    }

    default BlockPos getBlockPos(){
        return getCrucible().crucible.getBlockPos();
    }

    default Level getLevel(){
        return getCrucible().crucible.getLevel();
    }
}
