package com.hyperlynx.reactive;

import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import com.hyperlynx.reactive.util.ConfigMan;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ReactiveMod.MODID)
public class ReactiveMod
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ReactionMan REACTION_MAN = new ReactionMan();
    public static final String MODID = "reactive";

    public ReactiveMod() {
        Registration.init();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(REACTION_MAN);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigMan.commonSpec);

    }

}

/*
TODO LIST

- Useful results
    - Should be generally materials/items with bizarre properties. Ideas:
        - Stardust: Glowing powder that floats in a clump in the air. Under moonlight it glows brighter. In the End it scatters.
        - Archmetal: A malleable metal that makes for slow but long-lasting tools.
        - Wizard's Wax: A substance that can be used in place of honeycomb to make candles.
        -

 */
