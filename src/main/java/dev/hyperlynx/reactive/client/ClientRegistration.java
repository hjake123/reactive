package dev.hyperlynx.reactive.client;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.client.particles.*;
import dev.hyperlynx.reactive.client.renderers.CrucibleRenderer;
import dev.hyperlynx.reactive.client.renderers.GatewayRenderer;
import dev.hyperlynx.reactive.client.renderers.SymbolRenderer;
import dev.hyperlynx.reactive.integration.iris.IrisGatewayRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class ClientRegistration {
    public static boolean IRIS_MODE = false;

    public static void init(IEventBus bus) {
        bus.register(ClientRegistration.class);
        if(ModList.get().isLoaded("iris") || ModList.get().isLoaded("oculus")){
            // Enable special handling for Iris shaders to draw the Gateway block correctly.
            IRIS_MODE = true;
        }
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
        evt.registerBlockEntityRenderer(Registration.CRUCIBLE_BE.get(), CrucibleRenderer::new);
        evt.registerBlockEntityRenderer(Registration.SYMBOL_BE.get(), SymbolRenderer::new);
        if(IRIS_MODE && ConfigMan.CLIENT.irisCompat.get()){
            evt.registerBlockEntityRenderer(Registration.GATEWAY_BE.get(), IrisGatewayRenderer::new);
        } else {
            evt.registerBlockEntityRenderer(Registration.GATEWAY_BE.get(), GatewayRenderer::new);
        }
    }

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent evt){
//        if(ModList.get().isLoaded("create")){
//            ReactiveCreatePlugin.initClient();
//        }
    }

}
