package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.client.renderers.rxn.ReactionRenderer;
import dev.hyperlynx.reactive.integration.kubejs.events.CustomReactionTickEvent;
import dev.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import dev.latvian.mods.kubejs.event.EventResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.*;

public class CustomReaction extends Reaction {
    protected int cost = 0;
    protected int yield = 0;
    protected Optional<Power> output_power = Optional.empty();

    public static final StreamCodec<RegistryFriendlyByteBuf, CustomReaction> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CustomReaction::getAlias,
            ByteBufCodecs.map(HashMap::new, Power.STREAM_CODEC, ByteBufCodecs.INT), CustomReaction::getReagents,
            ByteBufCodecs.INT, CustomReaction::cost,
            ByteBufCodecs.INT, CustomReaction::yield,
            Power.STREAM_CODEC.apply(ByteBufCodecs::optional), CustomReaction::outputPower,
            StreamCodec.of(CustomReaction::serializeName, CustomReaction::deserializeName), Reaction::getName,
            CustomReaction::new
    );

    protected static void serializeName(RegistryFriendlyByteBuf buffer, MutableComponent name) {
        if(name == null){
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).encode(buffer, Optional.empty());
        }
        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).encode(buffer, Optional.of(Component.Serializer.toJson(name, buffer.registryAccess())));
    }

    protected static MutableComponent deserializeName(RegistryFriendlyByteBuf buffer) {
        var json = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).decode(buffer);
        return json.map(s -> Component.Serializer.fromJson(s, buffer.registryAccess())).orElse(null);
    }

    public CustomReaction(String alias, List<Power> required_powers, MutableComponent name_override){
        super(alias, 0);
        for(Power required_power : required_powers)
            reagents.put(required_power, WorldSpecificValue.get(alias+required_power+"required", 1, 400));
        this.name = name_override;
    }

    private CustomReaction(String alias, Map<Power, Integer> reagents, int cost, int yield, Optional<Power> output, MutableComponent name){
        super(alias);
        this.reagents = reagents;
        this.cost = cost;
        this.yield = yield;
        this.output_power = output;
        this.name = name;
    }

    protected int cost() { return cost; }
    protected int yield() { return yield; }
    protected Optional<Power> outputPower() { return output_power; }

    @Override
    public Status conditionsMet(Reactor crucible){
        Status status = super.conditionsMet(crucible);
        if(!(status.equals(Status.REACTING))){
            return status;
        }
        var event = new CustomReactionTickEvent(this, crucible);
        EventResult result;
        result = ReactiveKubeJSPlugin.REACTION_EFFECT_CACHE.processServerTestEvent(event);
        if(result.interruptFalse()){
            return Status.INHIBITED;
        }
        return status;
    }

    @Override
    public void run(Reactor reactor) {
        ReactiveKubeJSPlugin.REACTION_EFFECT_CACHE.processRunEvent(new CustomReactionTickEvent(this, reactor));
        if(cost > 0){
            expendPower(reactor, cost);
        }
        output_power.ifPresent(power -> reactor.addPower(power, yield));
        super.run(reactor);
    }

    public static ReactionRenderer getRenderFunction(String alias) {
        return (reactor) -> {
            EventTransceiver.CUSTOM_REACTION_RENDER_EVENT.post(new CustomReactionTickEvent(alias, reactor));
        };
    }

    private void expendPower(Reactor reactor, int cost){
        for(Power p : this.getReagents().keySet()){
            reactor.expendPower(p, (int) ((double) cost/this.getReagents().size()) + 1);
            reactor.setDirty();
        }
    }
}
