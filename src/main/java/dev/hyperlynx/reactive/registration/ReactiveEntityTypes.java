package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.entites.HoverQuilt;
import dev.hyperlynx.reactive.entites.ReactorEntity;
import dev.hyperlynx.reactive.entites.ThrownReactionFlask;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ReactiveEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ReactiveMod.MODID);

    public static final Supplier<EntityType<ThrownReactionFlask>> THROWN_REACTION_FLASK = ENTITY_TYPES.register("thrown_reaction_flask", () ->
            EntityType.Builder.of(ThrownReactionFlask::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .fireImmune()
                    .build("thrown_reaction_flask"));

    public static final Supplier<EntityType<ReactorEntity>> REACTOR = ENTITY_TYPES.register("reactor", () ->
            EntityType.Builder.of(ReactorEntity::new, MobCategory.MISC)
                    .sized(0.2F, 0.2F)
                    .fireImmune()
                    .build("reactor"));

    public static final Supplier<EntityType<HoverQuilt>> HOVER_QUILT = ENTITY_TYPES.register("hover_quilt", () ->
            EntityType.Builder.of(HoverQuilt::new, MobCategory.MISC)
                    .sized(1.0F, 0.1F)
                    .fireImmune()
                    .updateInterval(1)
                    .build("hover_quilt"));
}
