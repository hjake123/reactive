package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public class ReactiveCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReactiveMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> REACTIVE_TAB = CREATIVE_TABS.register("reactive_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> ReactiveItems.CRUCIBLE.get().getDefaultInstance())
                    .title(Component.translatable("reactive.tab"))
                    .displayItems((params, output) -> {
                        for(DeferredHolder<Item, ? extends Item> item_reg : ReactiveItems.ITEMS.getEntries()){
                            output.accept(item_reg.get());
                        }
                    })
                    .build());
}
