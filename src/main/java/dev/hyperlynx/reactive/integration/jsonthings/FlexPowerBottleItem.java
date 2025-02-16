package dev.hyperlynx.reactive.integration.jsonthings;

import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import dev.gigaherz.jsonthings.things.items.FlexItem;
import dev.hyperlynx.reactive.blocks.CrucibleBlock;
import dev.hyperlynx.reactive.items.BasePowerBottle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

public class FlexPowerBottleItem extends FlexItem implements BasePowerBottle {
    public FlexPowerBottleItem(Properties props, ItemBuilder builder) {
        super(props, builder);
        DispenserBlock.registerBehavior(this, BasePowerBottle.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if(!(level.getBlockState(pos).getBlock() instanceof CrucibleBlock)){
            return super.useOn(context);
        }

        return tryAddToCrucible(context);
    }
}
