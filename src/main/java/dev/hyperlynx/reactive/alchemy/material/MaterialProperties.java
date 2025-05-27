package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid= ReactiveMod.MODID, bus=EventBusSubscriber.Bus.MOD)
public class MaterialProperties {
    public static final ResourceKey<Registry<MaterialProperty<?>>> MATERIAL_PROPERTY_REGISTRY_KEY = ResourceKey.createRegistryKey(ReactiveMod.location( "material_properties"));
    public static final Registry<MaterialProperty<?>> PROPERTY_REGISTRY = new RegistryBuilder<>(MATERIAL_PROPERTY_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ReactiveMod.location( "nothing"))
            .create();
    public static final DeferredRegister<MaterialProperty<?>> PROPERTIES = DeferredRegister.create(PROPERTY_REGISTRY, ReactiveMod.MODID);

    public static final DeferredHolder<MaterialProperty<?>, FlagMaterialProperty> MAGMA_STEP = PROPERTIES.register("magma_step", FlagMaterialProperty::new);
    public static final DeferredHolder<MaterialProperty<?>, FlagMaterialProperty> FIRE_SOURCE = PROPERTIES.register("fire_source", FlagMaterialProperty::new);
    public static final DeferredHolder<MaterialProperty<?>, FloatMaterialProperty> BREAK_SPEED = PROPERTIES.register("break_speed", FloatMaterialProperty::new);
    public static final DeferredHolder<MaterialProperty<?>, FloatMaterialProperty> BLAST_RESISTANCE = PROPERTIES.register("blast_resistance", FloatMaterialProperty::new);
    public static final DeferredHolder<MaterialProperty<?>, FloatMaterialProperty> ENCHANT_POWER = PROPERTIES.register("enchant_power", FloatMaterialProperty::new);
    public static final DeferredHolder<MaterialProperty<?>, FloatMaterialProperty> FRICTION = PROPERTIES.register("friction", FloatMaterialProperty::new);
    public static final DeferredHolder<MaterialProperty<?>, IntMaterialProperty> FLAMMABILITY = PROPERTIES.register("flammability", IntMaterialProperty::new);
    public static final DeferredHolder<MaterialProperty<?>, IntMaterialProperty> LIGHT = PROPERTIES.register("light", IntMaterialProperty::new);
    public static final DeferredHolder<MaterialProperty<?>, IntMaterialProperty> REDSTONE = PROPERTIES.register("redstone", IntMaterialProperty::new);
    //public static final DeferredHolder<MaterialProperty<?>, SoundTypeMaterialProperty> SOUND_TYPE = PROPERTIES.register("redstone", SoundTypeMaterialProperty::new);

}
