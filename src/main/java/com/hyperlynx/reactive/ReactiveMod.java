package com.hyperlynx.reactive;

import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(ReactiveMod.MODID)
public class ReactiveMod
{
    public static final String MODID = "reactive";
    public static final ReactionMan REACTION_MAN = new ReactionMan();
    public static final WorldSpecificValue WORLD_SPECIFIC_VALUE = new WorldSpecificValue();
    public ReactiveMod() {
        Registration.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientRegistration::init);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(REACTION_MAN);
        MinecraftForge.EVENT_BUS.register(WORLD_SPECIFIC_VALUE);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigMan.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigMan.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigMan.clientSpec);
    }

}
