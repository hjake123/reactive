package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.advancements.CriteriaTriggers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/*
Automatically generates advancements for each reaction with a defined FlagCriterion!
 */
public class ReactionAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {
    private static final List<String> aliases = new ArrayList<>();

    public static void add(String alias) {
        aliases.add(alias);
    }
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper) {
        // Generate and save the reaction advancements based on the list given to ReactionMan.CRITERIA_BUILDER
        String REACTION_ADVANCEMENT_PREFIX = ":reactions/";
        for(String alias : aliases){
            Advancement.Builder builder = Advancement.Builder.advancement();
            builder.addCriterion("criterion", CriteriaTriggers.REACTION_TRIGGER.createInstance(alias));
            builder.requirements(RequirementsStrategy.AND);
            builder.rewards(AdvancementRewards.EMPTY);
            builder.save(consumer, ReactiveMod.MODID + REACTION_ADVANCEMENT_PREFIX + alias);

            // Generate the "perfection" advancements for getting the requirements exactly right
            Advancement.Builder perfect_builder = Advancement.Builder.advancement();
            perfect_builder.addCriterion("criterion", CriteriaTriggers.PERFECT_REACTION_TRIGGER.createInstance(alias));
            perfect_builder.requirements(RequirementsStrategy.AND);
            perfect_builder.rewards(AdvancementRewards.EMPTY);
            perfect_builder.save(consumer, ReactiveMod.MODID +REACTION_ADVANCEMENT_PREFIX + alias +"_perfect");
        }
    }
}
