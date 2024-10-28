package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.advancements.FlagCriterion;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class FlagTriggerBuilder extends BuilderBase<FlagCriterion> {
    public FlagTriggerBuilder(ResourceLocation id) {
        super(id);
    }

    @Override
    public RegistryInfo<?> getRegistryType() {
        // TODO Probably will break!
        return RegistryInfo.of(ResourceKey.createRegistryKey(new ResourceLocation("reeactive:fake_criterion_registry")));
    }

    @Override
    public FlagCriterion createObject() {
        return new FlagCriterion(id);
    }
}
