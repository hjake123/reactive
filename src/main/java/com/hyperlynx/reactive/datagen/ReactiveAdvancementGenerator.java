package com.hyperlynx.reactive.datagen;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/*
Automatically generates advancements for each reaction with a defined FlagCriterion!
 */
public class ReactiveAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper) {
        // Generate and save the reaction advancements based on the list given to ReactionMan.CRITERIA_BUILDER
        String REACTION_ADVANCEMENT_PREFIX = ":reactions/";
        List<String> aliases = ReactionMan.CRITERIA_BUILDER.getAliases();
        for(String alias : aliases){
            if(ReactionMan.CRITERIA_BUILDER.get(alias) == null) {
                System.err.println("Reaction " + alias + " doesn't have a criterion, so no advancement will be generated.");
                continue;
            }
            Advancement.Builder builder = Advancement.Builder.advancement();
            builder.addCriterion("criterion", Objects.requireNonNull(ReactionMan.CRITERIA_BUILDER.get(alias))
                    .createInstance(ContextAwarePredicate.ANY));
            builder.requirements(RequirementsStrategy.AND);
            builder.rewards(AdvancementRewards.EMPTY);
            builder.save(consumer, ReactiveMod.MODID + REACTION_ADVANCEMENT_PREFIX + alias);

            // Generate the "perfection" advancements for getting the requirements exactly right
            if(ReactionMan.CRITERIA_BUILDER.get(alias+"_perfect") == null) {
                System.err.println("Reaction " + alias + " doesn't have a perfection criterion, so no perfection advancement will be generated.");
                continue;
            }
            Advancement.Builder perfect_builder = Advancement.Builder.advancement();
            perfect_builder.addCriterion("criterion", Objects.requireNonNull(ReactionMan.CRITERIA_BUILDER.get(alias + "_perfect"))
                    .createInstance(ContextAwarePredicate.ANY));
            perfect_builder.requirements(RequirementsStrategy.AND);
            perfect_builder.rewards(AdvancementRewards.EMPTY);
            perfect_builder.save(consumer, ReactiveMod.MODID +REACTION_ADVANCEMENT_PREFIX + alias +"_perfect");
        }
    }
}
