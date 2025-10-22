package dev.hyperlynx.reactive.client;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.alchemy.material.MaterialData;
import dev.hyperlynx.reactive.client.models.HoverQuiltModel;
import dev.hyperlynx.reactive.client.particles.*;
import dev.hyperlynx.reactive.client.renderers.*;
import dev.hyperlynx.reactive.client.renderers.rxn.ReactionRenderers;
import dev.hyperlynx.reactive.integration.ponder.ReactivePonderPlugin;
import dev.hyperlynx.reactive.items.MaterialItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import dev.hyperlynx.reactive.integration.iris.IrisGatewayRenderer;

import java.util.concurrent.atomic.AtomicReference;

public class ClientRegistration {
    public static final ReactionRenderers REACTION_RENDERERS = new ReactionRenderers();
    public static boolean IRIS_MODE = false;
    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(ClientRegistration.class);
        if(ModList.get().isLoaded("iris") || ModList.get().isLoaded("oculus")){
            // Enable special handling for Iris shaders to draw the Gateway block correctly.
            IRIS_MODE = true;
        }
    }

    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HoverQuiltModel.LAYER_LOCATION, HoverQuiltModel::createBodyLayer);
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
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        evt.registerBlockEntityRenderer(Registration.CRUCIBLE_BE.get(), CrucibleRenderer::new);
        evt.registerBlockEntityRenderer(Registration.SYMBOL_BE.get(), SymbolRenderer::new);
        if(IRIS_MODE && ConfigMan.CLIENT.irisCompat.get()) {
            evt.registerBlockEntityRenderer(Registration.GATEWAY_BE.get(), IrisGatewayRenderer::new);
        } else {
            evt.registerBlockEntityRenderer(Registration.GATEWAY_BE.get(), GatewayRenderer::new);
        }

        evt.registerEntityRenderer(Registration.REACTOR.get(), ReactorEntityRenderer::new);
        evt.registerEntityRenderer(Registration.HOVER_QUILT.get(), HoverQuiltRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent evt){
        if(ModList.get().isLoaded("create")){
            ReactivePonderPlugin.clientInit();
        }

        ItemProperties.register(
                Registration.MATERIAL_ITEM.get(),
                ReactiveMod.location("material_model_index"),
                MaterialItem::getModelOverrideValue
        );

        ClientMaterialMan.clientside_data = new AtomicReference<>(MaterialData.empty());
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register(MaterialItem::getItemColor, Registration.MATERIAL_ITEM.get());
    }

}
