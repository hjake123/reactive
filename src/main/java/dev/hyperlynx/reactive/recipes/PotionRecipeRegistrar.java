package dev.hyperlynx.reactive.recipes;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.registration.ReactivePotions;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid= ReactiveMod.MODID, bus=EventBusSubscriber.Bus.GAME)
public class PotionRecipeRegistrar {
    @SubscribeEvent
    public static void registerPotions(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(
                Potions.THICK,
                ReactiveItems.SECRET_SCALE.get(),
                ReactivePotions.NULL_GRAVITY
        );

        builder.addMix(
                ReactivePotions.NULL_GRAVITY,
                Items.REDSTONE,
                ReactivePotions.LONG_NULL_GRAVITY
        );
    }
}
