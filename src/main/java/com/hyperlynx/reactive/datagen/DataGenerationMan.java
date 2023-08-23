//package com.hyperlynx.reactive.datagen;
//
//import com.hyperlynx.reactive.ReactiveMod;
//import net.minecraftforge.data.event.GatherDataEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
///*
//Looks like it's finally time for DataGeneratorMan to make an appearance!
//Manages Forge data generation by listening for GatherDataEvents.
//
//Currently, it can generate:
//- Reaction advancements
// */
//@Mod.EventBusSubscriber(modid= ReactiveMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
//public class DataGenerationMan {
//    @SubscribeEvent
//    public static void gatherData (GatherDataEvent event){
//        event.getGenerator().addProvider(
//                event.includeServer(),
//                new ReactiveAdvancementProvider(event.getGenerator(), event.getExistingFileHelper()));
//    }
//}
