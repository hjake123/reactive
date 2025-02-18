package dev.hyperlynx.reactive;

import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ReactiveMod.MODID)
public class ReactiveMod
{
    public static final String MODID = "reactive";
    public static final ReactionMan REACTION_MAN = new ReactionMan();
    public static final WorldSpecificValue WORLD_SPECIFIC_VALUE = new WorldSpecificValue();
    public static final Logger LOGGER = LogManager.getLogger("Reactive");

    @SuppressWarnings("removal")
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

    /** Creates a ResourceLocation with the mod id as the namespace. **/
    public static ResourceLocation location(String path){
        return new ResourceLocation(MODID, path);
    }

}
