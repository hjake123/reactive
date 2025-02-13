package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
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
@EventBusSubscriber(modid= ReactiveMod.MODID, bus=EventBusSubscriber.Bus.MOD)
public class DataGenerationMan {
    @SubscribeEvent
    public static void gatherData (GatherDataEvent event){
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<AdvancementProvider>) output -> new AdvancementProvider(
                        output,
                        event.getLookupProvider(),
                        event.getExistingFileHelper(),
                        List.of(new ReactionAdvancementGenerator())
                )
        );

        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
                        output,
                        event.getLookupProvider(),
                        BuiltInPowerGenerator.POWER_SET_BUILDER,
                        Set.of(ReactiveMod.MODID)
                )
        );
    }
}
