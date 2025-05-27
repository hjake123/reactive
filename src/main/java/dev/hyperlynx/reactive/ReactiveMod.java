package dev.hyperlynx.reactive;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.alchemy.special.SpecialCaseMan;
import dev.hyperlynx.reactive.integration.create.ReactiveCreatePlugin;
import dev.hyperlynx.reactive.integration.jsonthings.ReactiveJsonThingsPlugin;
import dev.hyperlynx.reactive.integration.kubejs.events.EventTransceiver;
import dev.hyperlynx.reactive.registration.*;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ReactiveMod.MODID)
public class ReactiveMod
{
    public static final String MODID = "reactive";
    public static final ReactionMan REACTION_MAN = new ReactionMan();
    public static final Logger LOGGER = LogManager.getLogger("Reactive");
    public ReactiveMod(ModContainer container) {
        IEventBus reactive_bus = container.getEventBus();
        if(reactive_bus == null) {
            throw new RuntimeException("Reactive mod event bus was not provided to its container!");
        }
        initRegistries(reactive_bus);
        reactive_bus.addListener(ReactiveMod::commonSetupHandler);
        NeoForge.EVENT_BUS.register(REACTION_MAN);
        NeoForge.EVENT_BUS.addListener(WorldSpecificValue::worldLoad);
        container.registerConfig(ModConfig.Type.COMMON, ConfigMan.commonSpec);
        container.registerConfig(ModConfig.Type.SERVER, ConfigMan.serverSpec);
        container.registerConfig(ModConfig.Type.CLIENT, ConfigMan.clientSpec);
    }

    /** Creates a ResourceLocation with the mod id as the namespace. **/
    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static void commonSetupHandler(FMLCommonSetupEvent evt){
        SpecialCaseMan.bootstrap();
        ReactiveBlocks.COPPER_SYMBOL.get().setSymbolItem(ReactiveItems.COPPER_SYMBOL.get());
        ReactiveBlocks.IRON_SYMBOL.get().setSymbolItem(ReactiveItems.IRON_SYMBOL.get());
        ReactiveBlocks.GOLD_SYMBOL.get().setSymbolItem(ReactiveItems.GOLD_SYMBOL.get());
        ReactiveBlocks.OCCULT_SYMBOL.get().setSymbolItem(ReactiveItems.OCCULT_SYMBOL.get());
        ReactiveBlocks.DIVINE_SYMBOL.get().setSymbolItem(ReactiveItems.DIVINE_SYMBOL.get());
        if(ModList.get().isLoaded("kubejs")){
            NeoForge.EVENT_BUS.register(EventTransceiver.class);
        }
    }

    private static void initRegistries(IEventBus bus) {
        ReactiveBlocks.BLOCKS.register(bus);
        ReactiveItems.ITEMS.register(bus);
        ReactiveCreativeModeTabs.CREATIVE_TABS.register(bus);
        ReactiveEntityTypes.ENTITY_TYPES.register(bus);
        ReactiveMobEffects.MOB_EFFECTS.register(bus);
        ReactivePotions.POTIONS.register(bus);
        ReactiveParticles.PARTICLES.register(bus);
        ReactiveBlockEntityTypes.BLOCK_ENTITY_TYPES.register(bus);
        Powers.POWERS.register(bus);
        MaterialProperties.PROPERTIES.register(bus);
        ReactiveComponentTypes.COMPONENT_TYPES.register(bus);
        ReactiveComponentTypes.ENCHANTMENT_COMPONENT_TYPES.register(bus);
        ReactiveCriterionTriggers.CRITERIA_TRIGGERS.register(bus);
        ReactiveRecipes.RECIPE_TYPES.register(bus);
        ReactiveRecipes.RECIPE_SERIALIZERS.register(bus);
        ReactiveSoundEvents.SOUND_EVENTS.register(bus);
        ReactiveCommandArguments.COMMAND_ARGUMENTS.register(bus);
        ReactiveEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(bus);
        if(ModList.get().isLoaded("jsonthings")){
            ReactiveJsonThingsPlugin.registerParser(bus);
        }
        if(ModList.get().isLoaded("create")){
            ReactiveCreatePlugin.init(bus);
        }
    }
}
