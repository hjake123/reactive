package com.hyperlynx.reactive.integration.create;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.items.LitmusPaperItem;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.ValueListDisplaySource;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.IntAttached;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mutable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CrucibleDisplaySource extends ValueListDisplaySource {
    @Override
    protected Stream<IntAttached<MutableComponent>> provideEntries(DisplayLinkContext context, int maxRows) {
        if(!(context.getSourceBlockEntity() instanceof CrucibleBlockEntity crucible)){
            return Stream.empty();
        }
        Stream.Builder<IntAttached<MutableComponent>> builder = Stream.builder();
        for(Power p : crucible.getPowerMap().keySet()){
            if(crucible.getPowerLevel(p) > 0){
                builder.add(IntAttached.with(crucible.getPowerLevel(p), Component.literal(p.getName())));
            }
        }
        return builder.build();
    }

    @Override
    protected boolean valueFirst() {
        return false;
    }

    protected List<MutableComponent> createComponentsFromEntry(DisplayLinkContext context,
                                                               IntAttached<MutableComponent> entry) {
        int number = entry.getFirst();
        MutableComponent name = entry.getSecond()
                .append(WHITESPACE);

        if (shortenNumbers(context)) {
            String percent = LitmusPaperItem.getPercent(number);
            MutableComponent shortened = Components.literal(percent.equals("TRACE") ? "<1%" : percent);
            return Arrays.asList(name, shortened);
        }

        MutableComponent formattedNumber = Components.literal(String.valueOf(number)).append(WHITESPACE);
        return valueFirst() ? Arrays.asList(formattedNumber, name) : Arrays.asList(name, formattedNumber);
    }

}
