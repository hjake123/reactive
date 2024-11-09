package dev.hyperlynx.reactive.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

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
        FlagCriterion existing = criteria.putIfAbsent(alias, criterion);
        if(existing != null){
            throw new RedundantAliasException("The reaction alias '" + alias + "' was added more then once!");
        }
        FlagCriterion perfect_criterion = new FlagCriterion(new ResourceLocation("reactive:reaction/" + alias + "_perfect_criterion"));
        criteria.putIfAbsent(alias+"_perfect", perfect_criterion);
    }

    public void lateAdd(String alias){
        add(alias);
        FlagCriterion added = get(alias);
        FlagCriterion perfect_added = get(alias);
        net.minecraft.advancements.CriteriaTriggers.register(added);
        net.minecraft.advancements.CriteriaTriggers.register(perfect_added);
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

    public static class RedundantAliasException extends RuntimeException{
        public RedundantAliasException(String msg) {
            super(msg);
        }
    }
}
