package dev.hyperlynx.reactive;

import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ReactiveMod.MODID)
public class ReactiveMod
{
    public static final String MODID = "reactive";
    public static final ReactionMan REACTION_MAN = new ReactionMan();
    public ReactiveMod(ModContainer container) {
        IEventBus reactive_bus = container.getEventBus();
        Registration.init(reactive_bus);
        NeoForge.EVENT_BUS.register(REACTION_MAN);
        NeoForge.EVENT_BUS.addListener(WorldSpecificValue::worldLoad);
        container.registerConfig(ModConfig.Type.COMMON, ConfigMan.commonSpec);
        container.registerConfig(ModConfig.Type.SERVER, ConfigMan.serverSpec);
        container.registerConfig(ModConfig.Type.CLIENT, ConfigMan.clientSpec);
    }

    /** Creates a ResourceLocation with the mod id as the namespace. **/
    public static ResourceLocation location(String path){
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

}
