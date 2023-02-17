package com.hyperlynx.reactive;

import com.hyperlynx.reactive.fx.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientRegistration {
    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(ClientRegistration.class);
    }

    @SubscribeEvent
    public static void registerParticles(ParticleFactoryRegisterEvent evt) {
        Minecraft.getInstance().particleEngine.register(Registration.STARDUST_PARTICLE_TYPE.get(), StardustParticle.StardustParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(Registration.RUNE_PARTICLE_TYPE.get(), RuneParticle.RuneParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(Registration.SMALL_RUNE_PARTICLE_TYPE.get(), SmallRuneParticle.SmallRuneParticleProvider::new);
        Minecraft.getInstance().particleEngine.register(Registration.SMALL_BLACK_RUNE_PARTICLE_TYPE.get(), SmallBlackRuneParticle.SmallBlackRuneParticleProvider::new);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        evt.registerBlockEntityRenderer(Registration.CRUCIBLE_BE_TYPE.get(), CrucibleRenderer::new);
        evt.registerBlockEntityRenderer(Registration.SYMBOL_BE_TYPE.get(), SymbolRenderer::new);
    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(Registration.PURE_QUARTZ_BLOCK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(Registration.INCOMPLETE_STAFF.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(Registration.STAFF_OF_SOUL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(Registration.STAFF_OF_LIGHT.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(Registration.STAFF_OF_BLAZE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(Registration.STAFF_OF_LIFE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(Registration.STAFF_OF_MIND.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(Registration.STAFF_OF_WARP.get(), RenderType.translucent());

    }
}
