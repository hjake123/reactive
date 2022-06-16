package com.hyperlynx.reactive.datagen;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStates extends BlockStateProvider {
    public ModBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ReactiveMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

    }
}
