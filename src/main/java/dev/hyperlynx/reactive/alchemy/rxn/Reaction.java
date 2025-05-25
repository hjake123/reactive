package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.advancements.ReactionTrigger;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.registration.ReactiveBlocks;
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

public abstract class Reaction {

    protected Map<Power, Integer> reagents = new HashMap<>();
    protected Stimulus stimulus = Stimulus.NONE;
    protected MutableComponent name = Component.literal("Error!");

    public boolean always_perfect = false; // Set to true if this one always registers as perfect.

    String alias;

    protected Reaction(String alias){
        this.alias = alias;
    }

    // Creates the reaction with a random set of reagents.
    public Reaction(String alias, int max_reagent_count){
        this.alias = alias;
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
    public Status conditionsMet(Reactor reactor){
        boolean missing_a_power = false;
        boolean too_little_power = false;
        for(Power p : reagents.keySet()){
            if(!p.checkReactivity(reactor.getPowerLevel(p), reagents.get(p))){
                too_little_power = true;
                if(reactor.getPowerLevel(p) == 0){
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
        boolean met_conditions = checkStimulus(reactor);
        if(met_conditions) {
            if(reactor.areReactionsPaused()){
                return Status.INHIBITED;
            }
            if(reactor.getPowerLevel(Powers.BODY_POWER.get()) > WorldSpecificValue.get("body_inhibition_threshold", 20, 200)
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

    private boolean checkStimulus(Reactor reactor){
        return switch (stimulus) {
            case END_CRYSTAL -> checkEndCrystal(reactor);
            case GOLD_SYMBOL -> reactor.checkGoldSymbol();
            case ELECTRIC -> reactor.getElectricCharge() > 0;
            case NO_ELECTRIC -> reactor.getElectricCharge() == 0;
            case NO_IRON_SYMBOL -> !reactor.getAreaMemory().exists(reactor.getLevel(), ReactiveBlocks.IRON_SYMBOL.get());
            default -> true;
        };
    }

    private boolean checkEndCrystal(Reactor reactor){
        Level level = reactor.getLevel();
        if(reactor.getLinkedCrystal() != null && !reactor.getLinkedCrystal().isRemoved()) {
            reactor.setUsedCrystalThisCycle(true);
            return true;
        }
        if(level.isClientSide) {
            return false;
        }
        if(((ServerLevel) level).getDragonFight() != null) {
            return false;
        }

        int range = ConfigMan.COMMON.crucibleRange.get();
        AABB aoe = new AABB(reactor.getBlockPos().offset(-range, -range, -range).getCenter(), reactor.getBlockPos().offset(range, range, range).getCenter());
        List<EndCrystal> end_crystals = level.getEntitiesOfClass(EndCrystal.class, aoe);
        if(end_crystals.isEmpty())
            return false;
        end_crystals.get(0).setBeamTarget(reactor.getBlockPos().below(2)); // For some strange reason, it shoots at the block 2 above the set position.
        reactor.setLinkedCrystal(end_crystals.get(0));
        reactor.setUsedCrystalThisCycle(true);
        return true;
    }

    public void run(Reactor reactor){
        if(!(reactor.getLevel() instanceof ServerLevel server))
            return;
        reactor.getLevel().gameEvent(GameEvent.BLOCK_ACTIVATE, reactor.getBlockPos(), GameEvent.Context.of(reactor.getBlockState()));
        // Award the completion criteria.
        ReactionTrigger.triggerForNearbyPlayers(server, alias, reactor.getBlockPos(), 6);

        if(always_perfect || isPerfect(reactor)){
            // Award the perfect criterion.
            ReactionTrigger.triggerPerfectForNearbyPlayers(server, alias, reactor.getBlockPos(), 6);
        }
    }

    public boolean isPerfect(Reactor reactor){
        // If crucible only has the same number of powers as the reagents, and the reaction could run, then it would be running with nothing extra.
        // Therefore, it is running 'perfectly'.
        return reactor.getPowerCount() == reagents.size();
    }

    public enum Stimulus {
        NONE,
        GOLD_SYMBOL,
        ELECTRIC,
        NO_ELECTRIC,
        END_CRYSTAL,
        NO_END_CRYSTAL,
        NO_IRON_SYMBOL
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
        return alias;
    }

}
