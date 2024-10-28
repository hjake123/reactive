package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.integration.kubejs.KubeCrucible;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface KubeCrucibleEvent {
    KubeCrucible getCrucible();

    default int getPowerLevel(String power_rl){
        var crucible = getCrucible().crucible;
        return crucible.getPowerLevel(Powers.POWER_SUPPLIER.get().getValue(new ResourceLocation(power_rl)));
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
