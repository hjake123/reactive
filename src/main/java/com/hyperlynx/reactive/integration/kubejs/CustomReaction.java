package com.hyperlynx.reactive.integration.kubejs;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.integration.kubejs.events.CustomReactionTickEvent;
import com.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.world.level.Level;

import java.util.List;

public class CustomReaction extends Reaction {
    public CustomReaction(String alias, List<Power> required_powers){
        super(alias, 0);
        for(Power required_power : required_powers)
            reagents.put(required_power, WorldSpecificValue.get(alias+required_power+"required", 1, 400));
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        EventTransceiver.CUSTOM_REACTION_RUN_EVENT.post(ScriptType.SERVER, new CustomReactionTickEvent(this, crucible));
        super.run(crucible);
    }

    @Override
    public void render(Level l, CrucibleBlockEntity crucible) {
        EventTransceiver.CUSTOM_REACTION_RENDER_EVENT.post(ScriptType.CLIENT, new CustomReactionTickEvent(this, crucible));
    }
}
