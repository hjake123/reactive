package com.hyperlynx.reactive;

import com.hyperlynx.reactive.fx.particles.*;
import com.hyperlynx.reactive.fx.renderers.CrucibleRenderer;
import com.hyperlynx.reactive.fx.renderers.SymbolRenderer;
import com.hyperlynx.reactive.integration.create.ReactiveCreatePlugin;
//import com.simibubi.create.foundation.ponder.PonderRegistry;
//import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class ClientRegistration {
    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(ClientRegistration.class);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent evt) {
        Minecraft.getInstance().particleEngine.register(Registration.STARDUST_PARTICLE_TYPE.get(), StardustParticle.StardustParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(Registration.RUNE_PARTICLE_TYPE.get(), RuneParticle.RuneParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(Registration.SMALL_RUNE_PARTICLE_TYPE.get(), SmallRuneParticle.SmallRuneParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(Registration.SMALL_BLACK_RUNE_PARTICLE_TYPE.get(), SmallBlackRuneParticle.SmallBlackRuneParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(Registration.ACID_BUBBLE_PARTICLE_TYPE.get(), AcidBubbleParticle.AcidBubbleParticleProvider::new);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        evt.registerBlockEntityRenderer(Registration.CRUCIBLE_BE_TYPE.get(), CrucibleRenderer::new);
        evt.registerBlockEntityRenderer(Registration.SYMBOL_BE_TYPE.get(), SymbolRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent evt){
        if(ModList.get().isLoaded("create")){
            ReactiveCreatePlugin.initClient();
        }
    }

}
