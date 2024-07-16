package com.hyperlynx.reactive;

import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ReactiveMod.MODID)
public class ReactiveMod
{
    public static final String MODID = "reactive";
    public static final ReactionMan REACTION_MAN = new ReactionMan();
    public static final WorldSpecificValue WORLD_SPECIFIC_VALUE = new WorldSpecificValue();
    public ReactiveMod() {
        Registration.init();
        if(Dist.CLIENT.isClient())
            ClientRegistration.init();
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(REACTION_MAN);
        NeoForge.EVENT_BUS.register(WORLD_SPECIFIC_VALUE);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigMan.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigMan.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigMan.clientSpec);
    }

}
