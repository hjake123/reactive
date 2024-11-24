package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.integration.kubejs.events.CustomReactionTickEvent;
import dev.hyperlynx.reactive.integration.kubejs.events.EventHandlerCache;
import dev.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class CustomReaction extends Reaction {
    protected int cost = 0;
    protected int yield = 0;
    protected Optional<Power> output_power = Optional.empty();

    public CustomReaction(String alias, List<Power> required_powers, MutableComponent name_override){
        super(alias, 0);
        for(Power required_power : required_powers)
            reagents.put(required_power, WorldSpecificValue.get(alias+required_power+"required", 1, 400));
        this.name = name_override;
    }

    @Override
    public Status conditionsMet(Reactor crucible){
        Status status = super.conditionsMet(crucible);
        if(!(status.equals(Status.REACTING))){
            return status;
        }
        var event = new CustomReactionTickEvent(this, crucible);
        EventResult result;
        if(crucible.getLevel().isClientSide){
            result = ReactiveKubeJSPlugin.REACTIONS.processClientTestEvent(event);
        } else {
            result = ReactiveKubeJSPlugin.REACTIONS.processServerTestEvent(event);
        }
        if(result.interruptFalse()){
            return Status.INHIBITED;
        }
        return status;
    }

    @Override
    public void run(Reactor reactor) {
        ReactiveKubeJSPlugin.REACTIONS.processRunEvent(new CustomReactionTickEvent(this, reactor));
        if(cost > 0){
            expendPower(reactor, cost);
        }
        output_power.ifPresent(power -> reactor.addPower(power, yield));
        super.run(reactor);
    }

    @Override
    public void render(Level l, Reactor reactor) {
        ReactiveKubeJSPlugin.REACTIONS.processRenderEvent(new CustomReactionTickEvent(this, reactor));
    }

    private void expendPower(Reactor reactor, int cost){
        for(Power p : this.getReagents().keySet()){
            reactor.expendPower(p, (int) ((double) cost/this.getReagents().size()) + 1);
            reactor.setDirty();
        }
    }
}
