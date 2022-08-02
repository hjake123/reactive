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
        - Wizard's Wax: A substance that can be used in place of honeycomb to make candles.
        - Prism/Lens/Runestone: An object that can be used to determine the strength of the power an object exhibits.
        - Lightstrand: Golden 'strands' that can be used to strengthen leather armor and maybe do other crafting.
        - Crystal Iron: Crafting ingredient
        - Quickiron: Liquid iron that changes shape in response to redstone signals
        - Wyvern Snot: A substance that induces poison and hunger when held, but acts as an alternative to slimeballs for the sake of piston making.
        Can be made into evil-looking purple goo blocks that are subjected to gravity, induce poison and hunger, and are extremely flammable.
        - Verdflare Powder: A copper-based powder that can change fires into Green Fire. Green Fire hurts more but can't spread.

- Apparatus: The main utility of the mod
    - Device which can be customized to provide all sorts of useful effects.
    - The exact effects are semi-random, and require some ingenuity to discover.
    - Many parts can be added to it, made with ingredients from the mod.
    - Each part also renders in the block if at all possible.\

- Summoning:
    - Soul or Vital + Candles = Summoning
    - Various influences affect what happens: usually it's a vex or allay but other are possible
    - Carved Pumpkins may be useful

 */
