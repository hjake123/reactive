package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.registration.ReactiveBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class GatewayBlockEntity extends TheEndPortalBlockEntity {
    private int tick_count;
    private static final int STARTUP_DURATION = 50;
    private int startup_timer = 0;
    public GlobalPos target;
    private final String TARGET_POS_TAG = "Target";
    private final String TARGET_DIMENSION_TAG = "Dimension";

    public GatewayBlockEntity(BlockPos pos, BlockState blockState) {
        super(ReactiveBlockEntityTypes.GATEWAY.get(), pos, blockState);
        Random random = new Random(pos.hashCode());
        tick_count = random.nextInt(0, 12000);
    }

    @Override
    public boolean shouldRenderFace(Direction face) {
        return true;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos ignoredBlockPos, BlockState ignoredBlockState, T t) {
        if(level.isClientSide() && t instanceof GatewayBlockEntity gateway){
            gateway.tick_count++;
            if(gateway.startup_timer < STARTUP_DURATION) {
                gateway.startup_timer++;
            }
        }
    }

    public float totalTick(float partialTick) {
        return (float) tick_count + partialTick;
    }

    public float startupProportion(float partialTick) {
        if(startup_timer >= STARTUP_DURATION) {
            return 1.0F;
        }
        float x = (startup_timer + partialTick) / STARTUP_DURATION;
        float c1 = 1.70158F;
        float c3 = c1 + 1;
        return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2)); // https://easings.net/#easeOutBack
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(this.target == null)
            return;
        tag.put(TARGET_POS_TAG, NbtUtils.writeBlockPos(target.pos()));
        tag.put(TARGET_DIMENSION_TAG, StringTag.valueOf(String.valueOf(target.dimension().location())));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        Optional<BlockPos> pos = NbtUtils.readBlockPos(tag, TARGET_POS_TAG);
        if(pos.isEmpty())
            return;
        ResourceLocation location = ResourceLocation.parse(Objects.requireNonNull(tag.get(TARGET_DIMENSION_TAG)).getAsString());
        target = GlobalPos.of(ResourceKey.create(Registries.DIMENSION, location), pos.get());
    }
}
