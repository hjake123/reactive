package com.hyperlynx.reactive.cmd;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.alchemy.Powers;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class PowerArgumentType implements ArgumentType<ResourceLocation> {
    public static PowerArgumentType power() {
        return new PowerArgumentType();
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readString();
        input = input.replace(".", ":");
        if(!input.contains(":")){
            input = "reactive:" + input;
        }
        return ResourceLocation.parse(input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Powers.POWER_REGISTRY.stream().forEach((power) -> {
            if(power.getResourceLocation().getNamespace().equals(ReactiveMod.MODID)){
                builder.suggest(power.getId());
            }else{
                builder.suggest(power.getResourceLocation().getNamespace() + "." + power.getId());
            }
        });
        return builder.buildFuture();
    }
}
