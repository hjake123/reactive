package com.hyperlynx.reactive;

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
    public static final String MODID = "reactive";

    public ReactiveMod() {
        Registration.init();

        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigMan.commonSpec);

    }

}

/*
TODO LIST

- Reactions!
    - Reactions (rnxs) will be stored in another custom registry, and then can be made like new block subtypes.
    The Crucible will be able to search the whole registry for matching reactions so no problem here.

    - Catalyst reactions occur when certain items are dissolved and provide a different output for each item.
        - Requires items for different rxn outcomes.
        - Most common crafting rxn
        - Proportions should be random per world.
            - If you do it with wrong contents, do something weird.
        - Some effects summon custom entities to do crazy things.

    - Ambient rxns have a chance to occur for mixtures of multiple Powers.
        - Should cause effects on the world.
        - Proportions AND effects be random per world.
        - Some should be able to be mitigated i.e. with candles, runes, etc.

- Useful results
    - Should be generally materials/items with bizarre properties. Ideas:
        - Stardust: Glowing powder that floats in a clump in the air. Under moonlight it glows brighter. In the End it scatters.
        - Archmetal: A malleable metal that makes for slow but long-lasting tools.
        - Wizard's Wax: A substance that can be used in place of honeycomb to make candles.
        -

 */
