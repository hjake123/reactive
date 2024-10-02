package com.hyperlynx.reactive.integration.kubejs;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.integration.kubejs.events.CustomReactionTickEvent;
import com.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
import com.hyperlynx.reactive.util.WorldSpecificValue;
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
        ReactionMan.REACTION_NAMES.put(alias, name_override);
    }

    @Override
    public Status conditionsMet(CrucibleBlockEntity crucible){
        Status status = super.conditionsMet(crucible);
        if(!(status.equals(Status.REACTING))){
            return status;
        }
        var event = new CustomReactionTickEvent(this, crucible);
        EventResult result;
        if(crucible.getLevel().isClientSide){
            result = EventTransceiver.CUSTOM_REACTION_TEST_CONDITIONS_EVENT.post(ScriptType.CLIENT, event);
        } else {
            result = EventTransceiver.CUSTOM_REACTION_TEST_CONDITIONS_EVENT.post(ScriptType.SERVER, event);
        }
        if(result.interruptFalse()){
            return Status.INHIBITED;
        }
        return status;
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        EventTransceiver.CUSTOM_REACTION_RUN_EVENT.post(ScriptType.SERVER, new CustomReactionTickEvent(this, crucible));
        if(cost > 0){
            expendPower(crucible, cost);
        }
        output_power.ifPresent(power -> crucible.addPower(power, yield));
        super.run(crucible);
    }

    @Override
    public void render(Level l, CrucibleBlockEntity crucible) {
        EventTransceiver.CUSTOM_REACTION_RENDER_EVENT.post(ScriptType.CLIENT, new CustomReactionTickEvent(this, crucible));
    }

    private void expendPower(CrucibleBlockEntity crucible, int cost){
        for(Power p : this.getReagents().keySet()){
            crucible.expendPower(p, (int) ((double) cost/this.getReagents().size()) + 1);
            crucible.setDirty();
        }
    }
}
