package com.hyperlynx.reactive.datagen;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Ref;

public class ModBlockTags extends BlockTagsProvider{

    public ModBlockTags(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, ReactiveMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {

    }


    @NotNull
    @Override
    public String getName() {
        return "Reactive Mod Tags";
    }
}
