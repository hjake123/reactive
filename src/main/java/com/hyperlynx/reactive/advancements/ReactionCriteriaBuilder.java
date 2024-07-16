package com.hyperlynx.reactive.advancements;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.Nullable;

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
    private final Map<String, FlagCriterion> criteria = new HashMap<>();

    public void add(String alias){
        aliases.add(alias);
        FlagCriterion criterion = new FlagCriterion(new ResourceLocation("reactive:reaction/" + alias + "_criterion"));
        criteria.put(alias, criterion);
        FlagCriterion perfect_criterion = new FlagCriterion(new ResourceLocation("reactive:reaction/" + alias + "_perfect_criterion"));
        criteria.put(alias+"_perfect", perfect_criterion);
    }

    public void register(FMLCommonSetupEvent evt){
        for(String key : criteria.keySet()){
            evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(criteria.get(key)));
        }
    }

    public List<String> getAliases(){
        return aliases;
    }

    public FlagCriterion get(String alias) {
        return criteria.get(alias);
    }
}
