package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;

public abstract class Reaction {

    protected HashMap<Power, Integer> reagents = new HashMap<>();
    protected Stimulus stimulus = Stimulus.NONE;

    // Creates the reaction with a random set of reagents.
    public Reaction(String alias, int max_reagent_count){
        int reagent_count;
        if(max_reagent_count < 3){
            reagent_count = max_reagent_count;
        }else{
            reagent_count = WorldSpecificValue.get(alias+"reagent_count", 2, max_reagent_count);
        }
        int i = 0;

        while(reagents.size() < reagent_count){
            Power chosen_power = WorldSpecificValue.getFromCollection(alias+"r"+i, ReactionMan.BASE_POWER_LIST);
            int min = WorldSpecificValue.get(alias+"r"+i, 1, 400);
            reagents.put(chosen_power, min);
            i++;
        }
    }

    // Creates the reaction with preset powers, but random minimum requirements.
    public Reaction(String alias, Power... powers){
        for(Power p : powers){
            reagents.put(p, WorldSpecificValue.get(alias+p.getName(), 1, 400));
        }
    }

    public Reaction setStimulus(Stimulus rxs){
        this.stimulus = rxs;
        return this;
    }

    public boolean conditionsMet(CrucibleBlockEntity crucible){
        if(WorldSpecificValue.get("body_inhibition_threshold", 200, 500) < crucible.getPowerLevel(Powers.BODY_POWER.get()))
            return false;

        for(Power p : reagents.keySet()){
            if(!p.checkReactivity(crucible.getLevel(), crucible.getPowerLevel(p), reagents.get(p))){
                return false;
            }
        }
        return checkStimulus(crucible);
    }

    private boolean checkStimulus(CrucibleBlockEntity crucible){
        return switch (stimulus) {
            case END_CRYSTAL -> checkEndCrystal(crucible);
            case GOLD_SYMBOL -> crucible.areaMemory.exists(crucible.getLevel(), ConfigMan.COMMON.crucibleRange.get(), Registration.GOLD_SYMBOL.get());
            case ELECTRIC -> crucible.electricCharge > 0;
            case NO_ELECTRIC -> crucible.electricCharge == 0;
            case SACRIFICE -> crucible.sacrificeCount >= 10;
            default -> true;
        };
    }

    private boolean checkEndCrystal(CrucibleBlockEntity crucible){
        Level level = crucible.getLevel();
        if(crucible.linked_crystal != null && !crucible.linked_crystal.isRemoved()) {
            crucible.used_crystal_this_cycle = true;
            return true;
        }
        if(level.isClientSide) {
            return false;
        }
        if(((ServerLevel) level).dragonFight() != null) {
            return false;
        }

        int range = ConfigMan.COMMON.crucibleRange.get();
        AABB aoe = new AABB(crucible.getBlockPos().offset(-range, -range, -range), crucible.getBlockPos().offset(range, range, range));
        List<EndCrystal> end_crystals = level.getEntitiesOfClass(EndCrystal.class, aoe);
        if(end_crystals.isEmpty())
            return false;
        end_crystals.get(0).setBeamTarget(crucible.getBlockPos().below(2)); // For some strange reason, it shoots at the block 2 above the set position.
        crucible.linked_crystal = end_crystals.get(0);
        crucible.used_crystal_this_cycle = true;
        return true;
    }

    public abstract void run(CrucibleBlockEntity crucible);

    public abstract void render(final ClientLevel l, final CrucibleBlockEntity crucible);

    public enum Stimulus {
        NONE,
        GOLD_SYMBOL,
        ELECTRIC,
        NO_ELECTRIC,
        SACRIFICE,
        END_CRYSTAL,
        NO_END_CRYSTAL
    }

    @Override
    public String toString(){
        return reagents.toString();
    }

}
