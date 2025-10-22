package dev.hyperlynx.reactive.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TheEndGatewayBlockEntity.class)
public interface EndGatewayExitViewerMixin {
    @Accessor
    BlockPos getExitPortal();
}
