package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.advancements.FlagTrigger;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.ConfigMan;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.dedicated.Settings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Reaction {

    protected HashMap<Power, Integer> reagents = new HashMap<>();
    protected Stimulus stimulus = Stimulus.NONE;
    public FlagTrigger observe_trigger;
    public FlagTrigger perfect_trigger;
    protected MutableComponent name = Component.literal("Error!");

    public boolean always_perfect = false; // Set to true if this one always registers as perfect.

    String alias;

    // Creates the reaction with a random set of reagents.
    public Reaction(String alias, int max_reagent_count){
        this.alias = alias;
        this.name = Component.translatable("reaction.reactive." + alias);

        observe_trigger = ReactionMan.CRITERIA_BUILDER.get(alias);
        perfect_trigger = ReactionMan.CRITERIA_BUILDER.get(alias+"_perfect");

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
        this.alias = alias;
        this.name = Component.translatable("reaction.reactive." + alias);

        observe_trigger = ReactionMan.CRITERIA_BUILDER.get(alias);
        perfect_trigger = ReactionMan.CRITERIA_BUILDER.get(alias+"_perfect");
        for(Power p : powers){
            reagents.put(p, WorldSpecificValue.get(alias+p.getId(), 1, 400));
        }
    }

    public MutableComponent getName(){
        return name.copy();
    }

    public Reaction setStimulus(Stimulus rxs){
        this.stimulus = rxs;
        return this;
    }

    public Reaction.Stimulus getStimulus(){
        return stimulus;
    }

    public Map<Power, Integer> getReagents(){
        return reagents;
    }

    public Reaction setReagentCost(Power reagent, int cost){
        reagents.put(reagent, cost);
        return this;
    }

    public Reaction markAlwaysPerfect(){
        this.always_perfect = true;
        return this;
    }

    public void cloneReagentsOf(Reaction other){
        this.reagents = other.reagents;
    }

    public String getAlias(){
        return alias;
    }

    // Note that this also sets the reaction status, so all overrides should do that too.
    public Status conditionsMet(CrucibleBlockEntity crucible){
        for(Power p : reagents.keySet()){
            if(!p.checkReactivity(crucible.getPowerLevel(p), reagents.get(p))){
                if(crucible.getPowerLevel(p) > 0)
                    return Status.POWER_TOO_WEAK;
                return Status.STABLE;
            }
        }
        boolean met_conditions = checkStimulus(crucible);
        if(met_conditions) {
            if(crucible.getPowerLevel(Powers.BODY_POWER.get()) > WorldSpecificValue.get("body_inhibition_threshold", 20, 200)
            && !(reagents.containsKey(Powers.BODY_POWER.get()))) {
                return Status.INHIBITED;
            }
            return Status.REACTING;
        }
        if(stimulus == Stimulus.NO_ELECTRIC){
            return Status.INHIBITED;
        }
        if(reagents.size() == 1) {
            return Status.VOLATILE;
        }
        return Status.MISSING_STIMULUS;
    }

    private boolean checkStimulus(CrucibleBlockEntity crucible){
        return switch (stimulus) {
            case END_CRYSTAL -> checkEndCrystal(crucible);
            case GOLD_SYMBOL -> crucible.areaMemory.exists(crucible.getLevel(), Registration.GOLD_SYMBOL.get());
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
        if(((ServerLevel) level).getDragonFight() != null) {
            return false;
        }

        int range = ConfigMan.COMMON.crucibleRange.get();
        AABB aoe = new AABB(crucible.getBlockPos().offset(-range, -range, -range).getCenter(), crucible.getBlockPos().offset(range, range, range).getCenter());
        List<EndCrystal> end_crystals = level.getEntitiesOfClass(EndCrystal.class, aoe);
        if(end_crystals.isEmpty())
            return false;
        end_crystals.get(0).setBeamTarget(crucible.getBlockPos().below(2)); // For some strange reason, it shoots at the block 2 above the set position.
        crucible.linked_crystal = end_crystals.get(0);
        crucible.used_crystal_this_cycle = true;
        return true;
    }

    public void run(CrucibleBlockEntity crucible){
        if(!(crucible.getLevel() instanceof ServerLevel server))
            return;
        if(observe_trigger != null) {
            // Award the completion criteria.
            FlagTrigger.triggerForNearbyPlayers(server, observe_trigger, crucible.getBlockPos(), 6);
        }
        if(perfect_trigger != null){
            if(always_perfect || isPerfect(crucible)){
                // Award the perfect criterion.
                FlagTrigger.triggerForNearbyPlayers(server, perfect_trigger, crucible.getBlockPos(), 6);
            }
        }
    }

    public boolean isPerfect(CrucibleBlockEntity crucible){
        // If crucible only has the same number of powers as the reagents, and the reaction could run, then it would be running with nothing extra.
        // Therefore, it is running 'perfectly'.
        return crucible.getPowerCount() == reagents.size();
    }

    public abstract void render(final Level l, final CrucibleBlockEntity crucible);

    public enum Stimulus {
        NONE,
        GOLD_SYMBOL,
        ELECTRIC,
        NO_ELECTRIC,
        SACRIFICE,
        END_CRYSTAL,
        NO_END_CRYSTAL
    }

    public enum Status {
        STABLE,
        VOLATILE,
        MISSING_CATALYST,
        INHIBITED,
        POWER_TOO_WEAK,
        MISSING_STIMULUS,
        REACTING
    }

    @Override
    public String toString(){
        return reagents.toString();
    }

}
