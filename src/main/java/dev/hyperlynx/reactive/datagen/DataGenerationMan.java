package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.ReactiveMod;
//import com.hyperlynx.reactive.integration.pehkui.ReactivePehkuiPlugin;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import java.util.List;

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
                        List.of(new ReactiveAdvancementGenerator())
                )
        );
    }
}
