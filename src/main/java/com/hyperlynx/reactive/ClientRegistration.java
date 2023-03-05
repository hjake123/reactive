package com.hyperlynx.reactive;

import com.hyperlynx.reactive.fx.models.SoulCatModel;
import com.hyperlynx.reactive.fx.particles.RuneParticle;
import com.hyperlynx.reactive.fx.particles.SmallBlackRuneParticle;
import com.hyperlynx.reactive.fx.particles.SmallRuneParticle;
import com.hyperlynx.reactive.fx.particles.StardustParticle;
import com.hyperlynx.reactive.fx.renderers.CrucibleRenderer;
import com.hyperlynx.reactive.fx.renderers.SoulCatRenderer;
import com.hyperlynx.reactive.fx.renderers.SymbolRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        evt.registerBlockEntityRenderer(Registration.CRUCIBLE_BE_TYPE.get(), CrucibleRenderer::new);
        evt.registerBlockEntityRenderer(Registration.SYMBOL_BE_TYPE.get(), SymbolRenderer::new);
        evt.registerEntityRenderer(Registration.SOULCAT_ENTITY_TYPE.get(), SoulCatRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions evt) {
        evt.registerLayerDefinition(SoulCatModel.LAYER_LOCATION, SoulCatModel::createBodyLayer);
    }
}
