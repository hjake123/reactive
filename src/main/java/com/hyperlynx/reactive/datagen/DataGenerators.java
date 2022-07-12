package com.hyperlynx.reactive.datagen;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ReactiveMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(event.includeServer(), new ModRecipes(generator));
            generator.addProvider(event.includeServer(), new ModLootTables(generator));
            generator.addProvider(event.includeServer(), new ModBlockTags(generator, event.getExistingFileHelper()));
        }
        if (event.includeClient()) {
            generator.addProvider(event.includeServer(), new ModBlockStates(generator, event.getExistingFileHelper()));
            generator.addProvider(event.includeServer(), new ModItemModels(generator, event.getExistingFileHelper()));
            generator.addProvider(event.includeServer(), new ModLanguageProvider(generator, "en_us"));
        }
    }
}
