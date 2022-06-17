package com.hyperlynx.reactive.datagen;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(DataGenerator gen, String locale) {
        super(gen, ReactiveMod.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(Registration.CRUCIBLE.get(), "Crucible");
    }
}
