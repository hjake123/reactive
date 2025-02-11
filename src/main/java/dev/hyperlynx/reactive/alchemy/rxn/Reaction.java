package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.advancements.FlagCriterion;
import dev.hyperlynx.reactive.advancements.ReactionTrigger;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class Reaction {

    protected HashMap<Power, Integer> reagents = new HashMap<>();
    protected Stimulus stimulus = Stimulus.NONE;
    protected MutableComponent name;

    public boolean always_perfect = false; // Set to true if this one always registers as perfect.

    String alias;

    // Creates the reaction with a random set of reagents.
    public Reaction(String alias, int max_reagent_count){
        this.alias = alias;
        criterion = ReactionMan.CRITERIA_BUILDER.get(alias);
        perfect_criterion = ReactionMan.CRITERIA_BUILDER.get(alias+"_perfect");
        this.name = Component.translatable("reaction.reactive." + alias);

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

        for(Power p : powers){
            reagents.put(p, WorldSpecificValue.get(alias+p.getId(), 1, 400));
        }
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

    public Reaction markAlwaysPerfect(){
        this.always_perfect = true;
        return this;
    }

    public String getAlias(){
        return alias;
    }

    // Note that this also sets the reaction status, so all overrides should do that too.
    public Status conditionsMet(CrucibleBlockEntity crucible){
        boolean missing_a_power = false;
        boolean too_little_power = false;
        for(Power p : reagents.keySet()){
            if(!p.checkReactivity(crucible.getPowerLevel(p), reagents.get(p))){
                too_little_power = true;
                if(crucible.getPowerLevel(p) == 0){
                    missing_a_power = true;
                    break;
                }
            }
        }
        if(too_little_power){
            if(missing_a_power){
                return Status.STABLE;
            }
            return Status.POWER_TOO_WEAK;
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
        AABB aoe = new AABB(crucible.getBlockPos().offset(-range, -range, -range), crucible.getBlockPos().offset(range, range, range));
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
        crucible.getLevel().gameEvent(GameEvent.BLOCK_ACTIVATE, crucible.getBlockPos(), GameEvent.Context.of(crucible.getBlockState()));
        // Award the completion criteria.
        ReactionTrigger.triggerForNearbyPlayers(server, alias, reactor.getBlockPos(), 6);

        if(always_perfect || isPerfect(reactor)){
            // Award the perfect criterion.
            ReactionTrigger.triggerPerfectForNearbyPlayers(server, alias, reactor.getBlockPos(), 6);
        }
    }

    public boolean isPerfect(CrucibleBlockEntity crucible){
        // If crucible only has the same number of powers as the reagents, and the reaction could run, then it would be running with nothing extra.
        // Therefore, it is running 'perfectly'.
        return crucible.getPowerMap().keySet().size() == reagents.size();
    }

    public abstract void render(final Level l, final CrucibleBlockEntity crucible);

    public MutableComponent getName() {
        return name.copy();
    }

    public enum Stimulus {
        NONE,
        GOLD_SYMBOL,
        ELECTRIC,
        NO_ELECTRIC,
        SACRIFICE,
        END_CRYSTAL,
        NO_END_CRYSTAL
    }

    // The order of this enum declaration determines priority; lower on the list are more important.
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
