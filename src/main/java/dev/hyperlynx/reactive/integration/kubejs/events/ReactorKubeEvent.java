package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.integration.kubejs.KubeWrapped;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface ReactorKubeEvent extends KubeEvent {
    abstract KubeWrapped<Reactor> getReactor();

    default int getPowerLevel(String power_rl){
        var reactor = getReactor().get();
        return reactor.getPowerLevel(Powers.POWER_REGISTRY.get(ResourceLocation.parse(power_rl)));
    }

    default boolean hasPower(String power_rl){
        return getPowerLevel(power_rl) > 0;
    }

    default BlockPos getBlockPos(){
        return getReactor().get().getBlockPos();
    }

    default Level getLevel(){
        return getReactor().get().getLevel();
    }
}
