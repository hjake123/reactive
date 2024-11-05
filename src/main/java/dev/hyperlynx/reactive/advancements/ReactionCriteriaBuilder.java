package dev.hyperlynx.reactive.advancements;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
*This class creates a FlagCriterion for each Reaction alias string it is fed.
*It must be populated before RegisterEvent -- for example at class load in constructors.
 */
public class ReactionCriteriaBuilder {
    private final List<String> aliases = new ArrayList<>();
    private final Map<String, FlagTrigger> criteria = new HashMap<>();

    public void add(String alias){
        aliases.add(alias);
        FlagTrigger criterion = new FlagTrigger(ResourceLocation.parse("reactive:reaction/" + alias + "_criterion"));
        FlagTrigger existing = criteria.putIfAbsent(alias, criterion);
        if(existing != null){
            throw new RedundantAliasException("The reaction alias '" + alias + "' was added more then once!");
        }
        FlagTrigger perfect_criterion = new FlagTrigger(ResourceLocation.parse("reactive:reaction/" + alias + "_perfect_criterion"));
        criteria.putIfAbsent(alias+"_perfect", perfect_criterion);
    }

    @SubscribeEvent
    public void register(RegisterEvent event) {
        if(event.getRegistryKey().equals(BuiltInRegistries.TRIGGER_TYPES.key())) {
            for (String key : criteria.keySet()) {
                event.register(BuiltInRegistries.TRIGGER_TYPES.key(), ReactiveMod.location(criteria.get(key).path()), () -> criteria.get(key));
            }
        }
    }

    public List<String> getAliases(){
        return aliases;
    }

    public FlagTrigger get(String alias) {
        return criteria.get(alias);
    }

    public static class RedundantAliasException extends RuntimeException{
        public RedundantAliasException(String msg) {
            super(msg);
        }
    }
}
