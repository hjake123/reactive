package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.integration.kubejs.KubeScriptException;
import dev.hyperlynx.reactive.integration.kubejs.ReactionFactory;
import dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventResult;
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
        Stream<Power> reagents = Arrays.stream(reagent_locations).map((location) -> getPower(ResourceLocation.parse(location)));
        return new ReactionFactory(alias, custom_name, reagents.toList());
    }

    private Power getPower(ResourceLocation location){
        try{
            return Powers.get(location);
        } catch (NullPointerException e) {
            throw new KubeScriptException("Power " + location + " does not exist!");
        }
    }


    @Override
    public void afterPosted(EventResult result) {
        ReactiveKubeJSPlugin.REACTIONS.ingestReactionHandlers();
    }
}
