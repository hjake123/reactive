package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PhantomQuiltItem extends BlockItem {
    public PhantomQuiltItem(Properties pProperties) {
        super(Registration.PHANTOM_QUILT.get(), pProperties);
    }

    private static final int ACTIVATE_HEIGHT = 20;

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected){
        if (entity.fallDistance > ACTIVATE_HEIGHT) {
            if(entity instanceof ServerPlayer player){
                if(player.hasInfiniteMaterials()){
                    return; // Don't open the parachute in creative mode.
                }
            }
            BlockPos underfoot = entity.blockPosition().below();
            if (level.isEmptyBlock(underfoot)) {
                level.setBlock(underfoot, Registration.PHANTOM_QUILT.get().defaultBlockState(), 2);
                stack.shrink(1);
            }
        }
    }
}
