package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.blocks.SymbolBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.neoforge.registries.RegistryObject;

import static com.hyperlynx.reactive.Registration.ITEMS;

public class SymbolItem extends BlockItem {
    public SymbolItem(Block block, Properties props) {
        super(block, props);
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    public static RegistryObject<Item> fromBlock(RegistryObject<Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new SymbolItem(block.get(), new Item.Properties()));
    }

    private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        public ItemStack execute(BlockSource target_source, ItemStack stack) {
            Direction direction = target_source.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos pos = target_source.getPos().relative(direction);

            SymbolItem symbol = (SymbolItem) stack.getItem();
            symbol.place(new DirectionalPlaceContext(target_source.getLevel(), pos, direction, stack, direction));

            return stack;
        }
    };

}
