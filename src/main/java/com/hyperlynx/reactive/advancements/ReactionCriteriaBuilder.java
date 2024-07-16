package com.hyperlynx.reactive.advancements;

import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
*This class creates a FlagCriterion for each Reaction alias string it is fed.
*It must be populated before FMLCommonSetupEvent -- for example at class load in constructors.
 */
public class ReactionCriteriaBuilder {
    private final List<String> aliases = new ArrayList<>();
    private final Map<String, FlagTrigger> criteria = new HashMap<>();

    public void add(String alias){
        aliases.add(alias);
        FlagTrigger criterion = new FlagTrigger(new ResourceLocation("reactive:reaction/" + alias + "_criterion"));
        criteria.put(alias, criterion);
        FlagTrigger perfect_criterion = new FlagTrigger(new ResourceLocation("reactive:reaction/" + alias + "_perfect_criterion"));
        criteria.put(alias+"_perfect", perfect_criterion);
    }

    public void register(FMLCommonSetupEvent evt){
        for(String key : criteria.keySet()){
            evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(key, criteria.get(key)));
        }
    }

    public List<String> getAliases(){
        return aliases;
    }

    public FlagTrigger get(String alias) {
        return criteria.get(alias);
    }
}
