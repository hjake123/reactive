package com.hyperlynx.reactive;

import com.hyperlynx.reactive.alchemy.SpecialCaseMan;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(ReactiveMod.MODID)
public class ReactiveMod
{
    public static final ReactionMan REACTION_MAN = new ReactionMan();
    //public static final DataGenerationMan DATA_GENERATION_MAN = new DataGenerationMan();
    public static final WorldSpecificValue WORLD_SPECIFIC_VALUE = new WorldSpecificValue();
    public static final String MODID = "reactive";
    public static final Logger LOGGER = LogManager.getLogger();

    public ReactiveMod() {
        Registration.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientRegistration::init);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(REACTION_MAN);
        MinecraftForge.EVENT_BUS.register(WORLD_SPECIFIC_VALUE);
        //MinecraftForge.EVENT_BUS.register(DATA_GENERATION_MAN);
        MinecraftForge.EVENT_BUS.register(SpecialCaseMan.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigMan.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigMan.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigMan.clientSpec);
    }

}
