package dev.hyperlynx.reactive.client;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

public class MaterialModels {
    public static final ModelResourceLocation SALT_MATERIAL = ModelResourceLocation.standalone(ReactiveMod.location("block/material/salt"));

    @SubscribeEvent
    public static void registerMaterialModels(ModelEvent.RegisterAdditional event) {
        event.register(SALT_MATERIAL);
    }
}
