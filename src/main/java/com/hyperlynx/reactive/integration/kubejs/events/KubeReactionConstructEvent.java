package com.hyperlynx.reactive.integration.kubejs.events;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import com.hyperlynx.reactive.integration.kubejs.ReactionFactory;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.stream.Stream;

public class KubeReactionConstructEvent implements KubeEvent {
    ReactionMan.ReactionConstructEvent event;

    public KubeReactionConstructEvent(ReactionMan.ReactionConstructEvent event){
        this.event = event;
    }

    public ReactionFactory builder(String alias, MutableComponent custom_name, String... reagent_locations){
        Stream<Power> reagents = Arrays.stream(reagent_locations).map((location) -> Powers.POWER_REGISTRY.get(ResourceLocation.parse(location)));
        return new ReactionFactory(alias, custom_name, reagents.toList());
    }

}
