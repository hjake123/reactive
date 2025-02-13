package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/*
Automatically generates advancements for each reaction using the reaction and perfect_reaction criteria
 */
public class ReactionAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
    private static final List<String> aliases = new ArrayList<>();

    public static void add(String alias) {
        aliases.add(alias);
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
        // Generate and save the reaction advancements based on the list given to this class;
        String REACTION_ADVANCEMENT_PREFIX = ":reactions/";
        for(String alias : aliases){
            Advancement.Builder builder = Advancement.Builder.advancement();
            builder.addCriterion("criterion", Registration.REACTION_TRIGGER.get().instance(alias));
            builder.requirements(AdvancementRequirements.Strategy.AND);
            builder.rewards(AdvancementRewards.EMPTY);
            builder.save(consumer, ReactiveMod.MODID + REACTION_ADVANCEMENT_PREFIX + alias);

            // Generate the "perfection" advancements for getting the requirements exactly right
            Advancement.Builder perfect_builder = Advancement.Builder.advancement();
            perfect_builder.addCriterion("criterion", Registration.PERFECT_REACTION_TRIGGER.get().instance(alias));
            perfect_builder.requirements(AdvancementRequirements.Strategy.AND);
            perfect_builder.rewards(AdvancementRewards.EMPTY);
            perfect_builder.save(consumer, ReactiveMod.MODID +REACTION_ADVANCEMENT_PREFIX + alias +"_perfect");
        }
    }
}
