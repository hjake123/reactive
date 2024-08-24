package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.blocks.ChainDisplacingBlock;
import com.hyperlynx.reactive.blocks.DisplacedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class DisplacerItem extends Item {
    public static final int DISPLACER_BASE_DISPLACE_TIME = 200;

    public DisplacerItem(Properties props) {
        super(props.attributes(SwordItem.createAttributes(Tiers.STONE, 3, -3.0F)));
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 10;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        EquipmentSlot slot = LivingEntity.getSlotForHand(context.getHand());

        boolean hyper_mode = Registration.checkHasEnchant(context.getItemInHand(), Registration.WORLD_PIERCER);
        if (hyper_mode) {
            var displaced_center = perform(context, level, pos, state, slot, 32, DISPLACER_BASE_DISPLACE_TIME * 2);
            if(displaced_center.isPresent()){
                Direction.Axis perpendicular_axis = switch(context.getClickedFace().getAxis()){
                    case X, Z -> Direction.Axis.Y;
                    case Y -> Direction.Axis.X;
                };
                Direction initial_direction = context.getClickedFace().getClockWise(perpendicular_axis);
                int extra_delay = 0;
                for(BlockPos pos_within_square : BlockPos.spiralAround(displaced_center.get(), 1, context.getClickedFace().getClockWise(initial_direction.getAxis()), initial_direction)){
                    if(pos_within_square != displaced_center.get()) {
                        DisplacedBlock.displaceWithChain(level.getBlockState(pos_within_square), pos_within_square, level, 2 + extra_delay, 1, pos);
                        extra_delay++;
                    }
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        } else {
            if(perform(context, level, pos, state, slot, 8, DISPLACER_BASE_DISPLACE_TIME).isPresent()){
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
    }

    private Optional<BlockPos> perform(UseOnContext context, Level level, BlockPos pos, BlockState state, EquipmentSlot slot, int max_depth, int displace_time){
        if(state.getBlock() instanceof DisplacedBlock){
            // Allow the player to click on above and below blocks "though" the one they're facing if they're inside a block.
            if(context.isInside()) {
                if (context.getPlayer() != null && context.getPlayer().getXRot() > 20 && !context.getClickedFace().equals(Direction.UP)) {
                    pos = pos.below();
                }
                if (context.getPlayer() != null && context.getPlayer().getXRot() < -20 && !context.getClickedFace().equals(Direction.DOWN)) {
                    pos = pos.above();
                }
            }

            // Scan up to MAX_TUNNEL_DEPTH blocks forward and try to displace another.
            // Since this may be called repeatedly it can be used to make a pathway.
            BlockPos selected = pos;
            for(int i = 0; i < max_depth; i++){
                selected = selected.offset(context.getClickedFace().getOpposite().getNormal());
                if(level.getBlockState(selected).getBlock() instanceof DisplacedBlock)
                    continue;
                displace(level, selected, displace_time);
                if(context.getPlayer() instanceof ServerPlayer splayer && !context.getPlayer().isCreative())
                    context.getItemInHand().hurtAndBreak(1, splayer, slot);
                break;
            }
            level.playSound(null, pos, state.getBlock().getSoundType(state, level, pos, null).getHitSound(),
                    SoundSource.PLAYERS, 1.0F, 1.1F);
            return Optional.of(selected);
        }

        boolean displace_worked = displace(level, pos, displace_time);
        if(displace_worked){
            level.playSound(null, pos, state.getBlock().getSoundType(state, level, pos, null).getHitSound(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);
            if(context.getPlayer() instanceof ServerPlayer splayer && !context.getPlayer().isCreative())
                context.getItemInHand().hurtAndBreak(1, splayer, slot);
            return Optional.of(pos);
        }
        level.playSound(null, pos, state.getBlock().getSoundType(state, level, pos, null).getHitSound(),
                SoundSource.PLAYERS, 1.0F, 0.7F);
        return Optional.empty();
    }

    private static boolean displace(Level level, BlockPos selected, int displace_time) {
        if(level.getBlockState(selected).getBlock() instanceof ChainDisplacingBlock cdb) {
            cdb.breadthFirstDisplace(level, selected, false, displace_time);
            return true;
        }
        return DisplacedBlock.displace(level.getBlockState(selected), selected, level, displace_time);
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity wielder) {
        stack.hurtAndBreak(2, wielder, Objects.requireNonNull(stack.getEquipmentSlot()));
        return true;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return true;
    }

    // Check if the item being used to repair is the assigned repair bottle for this staff.
    @Override
    public boolean isValidRepairItem(ItemStack self, ItemStack repair_item_candidate) {
        return repair_item_candidate.is(Registration.MOTION_SALT.get());
    }
}
