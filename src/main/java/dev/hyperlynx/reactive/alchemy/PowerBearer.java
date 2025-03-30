package dev.hyperlynx.reactive.alchemy;

import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface PowerBearer {
    default boolean addPower(Power p, int amount) {
        if(p == null){
            return false;
        }
        if(getPowerLevel(p) == CrucibleBlockEntity.CRUCIBLE_MAX_POWER){
            return false;
        }
        if(getTotalPowerLevel() + amount > CrucibleBlockEntity.CRUCIBLE_MAX_POWER) {
            int excess = getTotalPowerLevel() + amount - CrucibleBlockEntity.CRUCIBLE_MAX_POWER;
            expendAnyPowerExcept(p, excess); // Replace other powers if needed.
            excess = getTotalPowerLevel() + amount - CrucibleBlockEntity.CRUCIBLE_MAX_POWER;
            if(excess > 0) {
                amount -= excess;
            }
        }

        int prev = getPowerMap().getOrDefault(p, 0);
        if(prev > 0)
            getPowerMap().replace(p, amount + prev);
        else
            getPowerMap().put(p, amount);

//        if(this.getLevel() != null && !this.getLevel().isClientSide)
//            System.out.println("Tried to add " + amount + " " + p.getName() + ".");

        return true;
    }
    default int getPowerLevel(Power t){
        if(getPowerMap().isEmpty() || getPowerMap().get(t) == null){
            return 0;
        }
        return getPowerMap().get(t);
    }

    default int getTotalPowerLevel(){
        int totalpp = 0;
        for (Power p : getPowerMap().keySet()) {
            totalpp += getPowerMap().get(p);
        }
        return totalpp;
    }

    default boolean expendPower(Power t, int amount) {
        if(getPowerMap().isEmpty() || !getPowerMap().containsKey(t)){
            return false;
        }
        int level = getPowerMap().get(t);
        if(level > amount){
            getPowerMap().put(t, level-amount);
            return true;
        }
        if (level == amount) {
            getPowerMap().put(t, 0);
            return true;
        }

        // This implies that all power t wasn't enough to meet amount.
        getPowerMap().put(t, 0);
        return false;
    }

    default void expendAnyPowerExcept(Power immune_power, int amount) {
        boolean expended = false;
        for(Power p : getPowerMap().keySet()){
            if(p != immune_power && p != Powers.CURSE_POWER.get()){
                expended = expendPower(p, amount);
            }
            if(expended) return;
        }
    }
    default void expendPower(){
        getPowerMap().clear();
    }

    int maxPower();

    @NotNull Map<Power, Integer> getPowerMap();

    default int getPowerCount(){
        return getPowerMap().keySet().size();
    }
}
