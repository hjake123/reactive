package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.blocks.SymbolBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import static dev.hyperlynx.reactive.Registration.ITEMS;

public class SymbolItem extends BlockItem {
    public SymbolItem(Block block, Properties props) {
        super(block, props);
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    public static DeferredItem<SymbolItem> registerSimpleBlockItem(DeferredHolder<Block, SymbolBlock> block) {
        return ITEMS.register(block.getId().getPath(), () -> new SymbolItem(block.get(), new Item.Properties()));
    }

    private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        public ItemStack execute(BlockSource target_source, ItemStack stack) {
            Direction direction = target_source.state().getValue(DispenserBlock.FACING);
            BlockPos pos = target_source.pos().relative(direction);

            SymbolItem symbol = (SymbolItem) stack.getItem();
            symbol.place(new DirectionalPlaceContext(target_source.level(), pos, direction, stack, direction));

            return stack;
        }
    };

}
