package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;


@Mod.EventBusSubscriber
public class MaterialProperties {
    public static final DeferredRegister<MaterialProperty<?>> PROPERTIES = DeferredRegister.create(ReactiveMod.location("material_properties"), ReactiveMod.MODID);
    public static final Supplier<IForgeRegistry<MaterialProperty<?>>> POWER_SUPPLIER = PROPERTIES.makeRegistry(RegistryBuilder::new);
    
    public static final RegistryObject<StringMaterialProperty> MODEL_NAME = PROPERTIES.register("model_name", StringMaterialProperty::new);
    public static final RegistryObject<ColorMaterialProperty> COLOR = PROPERTIES.register("color", ColorMaterialProperty::new);
    public static final RegistryObject<FlagMaterialProperty> MAGMA_STEP = PROPERTIES.register("magma_step", FlagMaterialProperty::new);
    public static final RegistryObject<FlagMaterialProperty> FIRE_SOURCE = PROPERTIES.register("fire_source", FlagMaterialProperty::new);
    public static final RegistryObject<FloatMaterialProperty> BREAK_STRENGTH = PROPERTIES.register("break_strength", FloatMaterialProperty::new);
    public static final RegistryObject<FloatMaterialProperty> BLAST_RESISTANCE = PROPERTIES.register("blast_resistance", FloatMaterialProperty::new);
    public static final RegistryObject<FloatMaterialProperty> ENCHANT_POWER = PROPERTIES.register("enchant_power", FloatMaterialProperty::new);
    public static final RegistryObject<FloatMaterialProperty> FRICTION = PROPERTIES.register("friction", FloatMaterialProperty::new);
    public static final RegistryObject<IntMaterialProperty> FLAMMABILITY = PROPERTIES.register("flammability", IntMaterialProperty::new);
    public static final RegistryObject<IntMaterialProperty> LIGHT = PROPERTIES.register("light", IntMaterialProperty::new);
    public static final RegistryObject<IntMaterialProperty> REDSTONE = PROPERTIES.register("redstone", IntMaterialProperty::new);
    public static final RegistryObject<FloatMaterialProperty> SELF_DEFENSE = PROPERTIES.register("self_defense", FloatMaterialProperty::new);
    public static final RegistryObject<FlagMaterialProperty> WARPING = PROPERTIES.register("warping", FlagMaterialProperty::new);
    public static final RegistryObject<FlagMaterialProperty> REDSTONE_MELTING = PROPERTIES.register("redstone_melting", FlagMaterialProperty::new);
    public static final RegistryObject<FlagMaterialProperty> INTANGIBLE = PROPERTIES.register("intangible", FlagMaterialProperty::new);
    public static final RegistryObject<FlagMaterialProperty> SEMITANGIBLE = PROPERTIES.register("semitangible", FlagMaterialProperty::new);
}
