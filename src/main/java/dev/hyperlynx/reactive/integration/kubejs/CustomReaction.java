package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.client.renderers.rxn.ReactionRenderer;
import dev.hyperlynx.reactive.integration.kubejs.events.CustomReactionTickEventJS;
import dev.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.network.chat.MutableComponent;

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

    public static ReactionRenderer getRenderFunction(String alias) {
        return (reactor) -> {
            EventTransceiver.CUSTOM_REACTION_RENDER_EVENT.post(new CustomReactionTickEventJS(alias, reactor));
        };
    }

    @Override
    public Status conditionsMet(Reactor crucible){
        Status status = super.conditionsMet(crucible);
        if(!(status.equals(Status.REACTING))){
            return status;
        }
        var event = new CustomReactionTickEventJS(this.getAlias(), crucible);
        EventResult result;
        result = ReactiveKubeJSPlugin.REACTION_EFFECT_CACHE.processServerTestEvent(event);
        if(result.interruptFalse()){
            return Status.INHIBITED;
        }
        return status;
    }

    @Override
    public void run(Reactor crucible) {
        EventTransceiver.CUSTOM_REACTION_RUN_EVENT.post(ScriptType.SERVER, new CustomReactionTickEventJS(this.getAlias(), crucible));
        if(cost > 0){
            expendPower(crucible, cost);
        }
        output_power.ifPresent(power -> crucible.addPower(power, yield));
        super.run(crucible);
    }

    private void expendPower(Reactor crucible, int cost){
        for(Power p : this.getReagents().keySet()){
            crucible.expendPower(p, (int) ((double) cost/this.getReagents().size()) + 1);
            crucible.setDirty();
        }
    }
}
