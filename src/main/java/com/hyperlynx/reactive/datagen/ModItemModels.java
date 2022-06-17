package com.hyperlynx.reactive.datagen;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModels extends ItemModelProvider {
    public ModItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ReactiveMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent("reactive:item/crucible", modLoc("block/crucible"));
    }
}
