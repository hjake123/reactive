package com.hyperlynx.reactive.util;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid= ReactiveMod.MODID, bus=EventBusSubscriber.Bus.GAME)
public class PotionRegistrar {
    @SubscribeEvent
    public static void registerPotions(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(
                Potions.THICK,
                Registration.SECRET_SCALE.get(),
                Registration.NULL_GRAVITY_POTION
        );

        builder.addMix(
                Registration.NULL_GRAVITY_POTION,
                Items.REDSTONE,
                Registration.LONG_NULL_GRAVITY_POTION
        );
    }
}
