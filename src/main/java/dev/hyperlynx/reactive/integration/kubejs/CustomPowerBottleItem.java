package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.blocks.CrucibleBlock;
import dev.hyperlynx.reactive.items.BasePowerBottle;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

public class CustomPowerBottleItem extends Item implements BasePowerBottle {
    public CustomPowerBottleItem(Properties properties) {
        super(properties);
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

    public static class Builder extends ItemBuilder {
        public Builder(ResourceLocation id) {
            super(id);
        }

        @Override
        public CustomPowerBottleItem createObject() {
            return new CustomPowerBottleItem(new Item.Properties());
        }
    }
}
