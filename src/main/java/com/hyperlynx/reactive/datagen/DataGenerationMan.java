package com.hyperlynx.reactive.datagen;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.data.DataProvider;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/*
Looks like it's finally time for DataGeneratorMan to make an appearance!
Manages Forge data generation by listening for GatherDataEvents.

Currently, it can generate:
- Reaction advancements
 */
@Mod.EventBusSubscriber(modid= ReactiveMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerationMan {
    @SubscribeEvent
    public static void gatherData (GatherDataEvent event){
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<ForgeAdvancementProvider>) output -> new ForgeAdvancementProvider(
                        output,
                        event.getLookupProvider(),
                        event.getExistingFileHelper(),
                        List.of(new ReactiveAdvancementGenerator())
                )
        );
    }
}
