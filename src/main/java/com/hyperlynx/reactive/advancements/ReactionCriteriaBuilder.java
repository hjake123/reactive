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
It must be populated before FMLCommonSetupEvent -- for example at class load in constructors.
 */
public class ReactionCriteriaBuilder {
    private final List<String> aliases = new ArrayList<>();
    public void add(String alias){
        aliases.add(alias);
    }

    public void register(FMLCommonSetupEvent evt){
        for(String alias : aliases){
            FlagCriterion criterion = new FlagCriterion(new ResourceLocation("reactive:reaction/" + alias + "_criterion"));
            evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(criterion));
            FlagCriterion perfect_criterion = new FlagCriterion(new ResourceLocation("reactive:reaction/" + alias + "_perfect_criterion"));
            evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(perfect_criterion));
        }
    }

    public List<String> getAliases(){
        return aliases;
    }
}
