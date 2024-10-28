package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.integration.kubejs.ReactionFactory;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.stream.Stream;

public class ReactionConstructEventJS extends EventJS {
    ReactionMan.ReactionConstructEvent event;

    public ReactionConstructEventJS(ReactionMan.ReactionConstructEvent event){
        this.event = event;
    }

    public ReactionFactory builder(String alias, MutableComponent custom_name, String... reagent_locations){
        Stream<Power> reagents = Arrays.stream(reagent_locations).map((location) -> Powers.POWER_SUPPLIER.get().getValue(new ResourceLocation(location)));
        return new ReactionFactory(alias, custom_name, reagents.toList());
    }

}
