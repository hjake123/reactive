package dev.hyperlynx.reactive.client;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.client.models.HoverQuiltModel;
import dev.hyperlynx.reactive.client.particles.*;
import dev.hyperlynx.reactive.client.renderers.be.CrucibleRenderer;
import dev.hyperlynx.reactive.client.renderers.be.GatewayRenderer;
import dev.hyperlynx.reactive.client.renderers.entities.HoverQuiltRenderer;
import dev.hyperlynx.reactive.client.renderers.entities.ReactorEntityRenderer;
import dev.hyperlynx.reactive.client.renderers.be.SymbolRenderer;
import dev.hyperlynx.reactive.integration.iris.IrisGatewayRenderer;
import dev.hyperlynx.reactive.integration.ponder.ReactivePonderPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
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
        evt.registerSpriteSet(Registration.ENERGY_PARTICLE_TYPE.get(), EnergyParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.CRUCIBLE_BE.get(), CrucibleRenderer::new);
        event.registerBlockEntityRenderer(Registration.SYMBOL_BE.get(), SymbolRenderer::new);
        if(IRIS_MODE && ConfigMan.CLIENT.irisCompat.get()){
            event.registerBlockEntityRenderer(Registration.GATEWAY_BE.get(), IrisGatewayRenderer::new);
        } else {
            event.registerBlockEntityRenderer(Registration.GATEWAY_BE.get(), GatewayRenderer::new);
        }
        event.registerEntityRenderer(Registration.REACTOR_ENTITY_TYPE.get(), ReactorEntityRenderer::new);
        event.registerEntityRenderer(Registration.THROWN_REACTION_FLASK.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Registration.HOVER_QUILT.get(), HoverQuiltRenderer::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HoverQuiltModel.LAYER_LOCATION, HoverQuiltModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent evt){
        if(ModList.get().isLoaded("ponder")){
            ReactivePonderPlugin.clientInit();
        }
    }

}
