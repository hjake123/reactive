package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.blocks.DisplacedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DisplacerItem extends Item {
    final int MAX_TUNNEL_DEPTH = 8;
    public DisplacerItem(Properties props) {
        super(props);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if(state.getBlock() instanceof DisplacedBlock){
            if(context.isInside()){
                if(context.getPlayer() != null && context.getPlayer().getXRot() > 20 && !context.getClickedFace().equals(Direction.UP)){
                    pos = pos.below();
                }
                if(context.getPlayer() != null && context.getPlayer().getXRot() < -20 && !context.getClickedFace().equals(Direction.DOWN)){
                    pos = pos.above();
                }
            }

            // Scan up to MAX_TUNNEL_DEPTH blocks forward and try to displace another.
            // Since this may be called repeatedly it can be used to make a pathway.
            BlockPos selected = pos;
            for(int i = 0; i < MAX_TUNNEL_DEPTH; i++){
                selected = selected.offset(context.getClickedFace().getOpposite().getNormal());
                if(level.getBlockState(selected).getBlock() instanceof DisplacedBlock)
                    continue;
                DisplacedBlock.displace(level.getBlockState(selected), selected, level, 200);
                if(context.getPlayer() instanceof ServerPlayer splayer && !context.getPlayer().isCreative())
                    context.getItemInHand().hurtAndBreak(1, splayer, (LivingEntity) -> {});
                break;
            }
            level.playSound(null, pos, state.getBlock().getSoundType(state, level, pos, null).getHitSound(),
                    SoundSource.PLAYERS, 1.0F, 1.1F);
            return InteractionResult.SUCCESS;
        }

        boolean displace_worked = DisplacedBlock.displace(level.getBlockState(pos), pos, level, 200);
        if(displace_worked){
            level.playSound(null, pos, state.getBlock().getSoundType(state, level, pos, null).getHitSound(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);
            if(context.getPlayer() instanceof ServerPlayer splayer && !context.getPlayer().isCreative())
                context.getItemInHand().hurtAndBreak(1, splayer, (LivingEntity) -> {});
            return InteractionResult.SUCCESS;
        }
        level.playSound(null, pos, state.getBlock().getSoundType(state, level, pos, null).getHitSound(),
                SoundSource.PLAYERS, 1.0F, 0.7F);
        return InteractionResult.FAIL;
    }
}
