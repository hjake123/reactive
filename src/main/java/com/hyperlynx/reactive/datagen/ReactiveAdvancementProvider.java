package com.hyperlynx.reactive.datagen;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/*
Automatically generates advancements for each reaction with a defined FlagCriterion!
This class won't work in 1.19.3 or later due to changes, but I don't plan to need it then...
 */
public class ReactiveAdvancementProvider extends AdvancementProvider {
    public ReactiveAdvancementProvider(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
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
                    .createInstance(EntityPredicate.Composite.ANY));
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
                    .createInstance(EntityPredicate.Composite.ANY));
            perfect_builder.requirements(RequirementsStrategy.AND);
            perfect_builder.rewards(AdvancementRewards.EMPTY);
            perfect_builder.save(consumer, ReactiveMod.MODID +REACTION_ADVANCEMENT_PREFIX + alias +"_perfect");
        }
    }
}
