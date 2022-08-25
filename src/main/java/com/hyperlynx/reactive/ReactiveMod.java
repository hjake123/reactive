package com.hyperlynx.reactive;

import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import com.hyperlynx.reactive.util.ConfigMan;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(ReactiveMod.MODID)
public class ReactiveMod
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ReactionMan REACTION_MAN = new ReactionMan();
    public static final String MODID = "reactive";

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MODID)
    {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registration.CRUCIBLE_ITEM.get());
        }
    };

    public ReactiveMod() {
        Registration.init();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(REACTION_MAN);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigMan.commonSpec);

    }

}
