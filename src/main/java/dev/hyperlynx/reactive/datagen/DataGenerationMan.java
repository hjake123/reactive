package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

/*
Looks like it's finally time for DataGeneratorMan to make an appearance!
Manages Forge data generation by listening for GatherDataEvents.

Currently, it can generate:
- Reaction advancements
 */
@EventBusSubscriber(modid= ReactiveMod.MODID)
public class DataGenerationMan {
    @SubscribeEvent
    public static void gatherData (GatherDataEvent event){
        var lookup = event.getLookupProvider();
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<AdvancementProvider>) output -> new AdvancementProvider(
                        output,
                        lookup,
                        event.getExistingFileHelper(),
                        List.of(new ReactionAdvancementGenerator())
                )
        );

        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
                        output,
                        lookup,
                        BuiltInMaterialGenerator.get(),
                        Set.of(ReactiveMod.MODID)
                )
        );

        event.getGenerator().addProvider(
                event.includeServer(),
                new MaterialFormulaRequirementGenerator(event.getGenerator().getPackOutput(), lookup)
        );
    }
}
