package dev.hyperlynx.reactive.entites;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;

public class UnstuckMaterial extends FallingBlockEntity {
    public UnstuckMaterial(EntityType<? extends FallingBlockEntity> entityType, Level level) {
        super(entityType, level);
    }
}
