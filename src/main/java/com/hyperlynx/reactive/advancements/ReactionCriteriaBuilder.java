package com.hyperlynx.reactive.advancements;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
This class creates a FlagCriterion for each Reaction alias string it is fed.
 */
public class ReactionCriteriaBuilder {
    private final List<String> aliases = new ArrayList<>();
    private final Map<String, FlagCriterion> criteria = new HashMap<>();
    public void add(String alias){
        aliases.add(alias);
    }
    public void build(){
        for(String alias : aliases){
            FlagCriterion criterion = new FlagCriterion(new ResourceLocation("reactive:reaction/" + alias + "_criterion"));
            criteria.put(alias, criterion);
            FlagCriterion perfect_criterion = new FlagCriterion(new ResourceLocation("reactive:reaction/" + alias + "_perfect_criterion"));
            criteria.put(alias+"_perfect", perfect_criterion);
        }
    }

    public void register(FMLCommonSetupEvent evt){
        for(String key : criteria.keySet()){
            evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(criteria.get(key)));
        }
    }

    public @Nullable FlagCriterion get(String alias){
        if(criteria.containsKey(alias))
            return criteria.get(alias);
        return null;
    }

    public List<String> getAliases(){
        return aliases;
    }
}
