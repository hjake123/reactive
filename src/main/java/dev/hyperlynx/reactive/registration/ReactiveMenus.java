package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.menu.DeskMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReactiveMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, ReactiveMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<DeskMenu>> DESK_MENU = MENUS.register("desk", () ->
            new MenuType<>(DeskMenu::new, FeatureFlags.DEFAULT_FLAGS));


}
