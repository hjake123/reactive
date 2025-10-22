package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.integration.kubejs.KubeReactor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface KubeCrucibleEvent {
    KubeReactor getCrucible();

    default int getPowerLevel(String power_rl){
        var crucible = getCrucible().reactor;
        return crucible.getPowerLevel(Powers.POWER_SUPPLIER.get().getValue(ResourceLocation.parse(power_rl)));
    }

    default boolean hasPower(String power_rl){
        return getPowerLevel(power_rl) > 0;
    }

    default BlockPos getBlockPos(){
        return getCrucible().reactor.getBlockPos();
    }

    default Level getLevel(){
        return getCrucible().reactor.getLevel();
    }
}
