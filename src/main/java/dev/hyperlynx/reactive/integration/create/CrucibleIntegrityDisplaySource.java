package dev.hyperlynx.reactive.integration.create;

import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import org.jetbrains.annotations.Nullable;

public class CrucibleIntegrityDisplaySource extends PercentOrProgressBarDisplaySource {
    @Nullable
    @Override
    protected Float getProgress(DisplayLinkContext context) {
        return ((CrucibleBlockEntity) context.getSourceBlockEntity()).integrity / 100.0F;
    }

    @Override
    protected boolean progressBarActive(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

}
