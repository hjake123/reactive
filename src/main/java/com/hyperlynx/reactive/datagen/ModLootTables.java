package com.hyperlynx.reactive.datagen;

import com.hyperlynx.reactive.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModLootTables extends BaseLootTableProvider{
    public ModLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(Registration.CRUCIBLE.get(), createSimpleTable("crucible", Registration.CRUCIBLE.get()));
    }
}
