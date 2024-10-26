package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.be.ActiveFoamBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ActiveGoldFoamBlock extends GoldFoamBlock implements EntityBlock {
    public ActiveGoldFoamBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ActiveFoamBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <ActiveFoamBlockEntity extends BlockEntity> BlockEntityTicker<ActiveFoamBlockEntity> getTicker(Level level, BlockState state, BlockEntityType<ActiveFoamBlockEntity> t) {
        if(t == Registration.ACTIVE_GOLD_FOAM_BE.get()){
            return (l, p, s, a) -> dev.hyperlynx.reactive.be.ActiveFoamBlockEntity.tick(l, p, s, (dev.hyperlynx.reactive.be.ActiveFoamBlockEntity) a);
        }
        return null;
    }
}
