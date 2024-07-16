package com.hyperlynx.reactive.datagen;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.advancements.FlagTrigger;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/*
Automatically generates advancements for each reaction with a defined FlagCriterion!
 */
public class ReactiveAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
        // Generate and save the reaction advancements based on the list given to ReactionMan.CRITERIA_BUILDER
        String REACTION_ADVANCEMENT_PREFIX = ":reactions/";
        List<String> aliases = ReactionMan.CRITERIA_BUILDER.getAliases();
        for(String alias : aliases){
            if(ReactionMan.CRITERIA_BUILDER.get(alias) == null) {
                System.err.println("Reaction " + alias + " doesn't have a criterion, so no advancement will be generated.");
                continue;
            }
            Advancement.Builder builder = Advancement.Builder.advancement();
            builder.addCriterion("criterion", new Criterion<>(ReactionMan.CRITERIA_BUILDER.get(alias),
                    ReactionMan.CRITERIA_BUILDER.get(alias).createInstance())); // TODO: Possibly broken!
            builder.requirements(AdvancementRequirements.Strategy.AND);
            builder.rewards(AdvancementRewards.EMPTY);
            builder.save(consumer, ReactiveMod.MODID + REACTION_ADVANCEMENT_PREFIX + alias);

            // Generate the "perfection" advancements for getting the requirements exactly right
            if(ReactionMan.CRITERIA_BUILDER.get(alias+"_perfect") == null) {
                System.err.println("Reaction " + alias + " doesn't have a perfection criterion, so no perfection advancement will be generated.");
                continue;
            }
            Advancement.Builder perfect_builder = Advancement.Builder.advancement();
            perfect_builder.addCriterion("criterion", new Criterion<>(ReactionMan.CRITERIA_BUILDER.get(alias),
                    ReactionMan.CRITERIA_BUILDER.get(alias).createInstance()));
            perfect_builder.requirements(AdvancementRequirements.Strategy.AND);
            perfect_builder.rewards(AdvancementRewards.EMPTY);
            perfect_builder.save(consumer, ReactiveMod.MODID +REACTION_ADVANCEMENT_PREFIX + alias +"_perfect");
        }
    }
}
