package com.hyperlynx.reactive.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.blocks.ChainDisplacingBlock;
import com.hyperlynx.reactive.blocks.DisplacedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DisplacerItem extends Item {
    public static final int DISPLACER_DISPLACE_TIME = 200;

    public DisplacerItem(Properties props) {
        super(props.attributes(SwordItem.createAttributes(Tiers.STONE, 3, -3.0F)));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        EquipmentSlot slot = LivingEntity.getSlotForHand(context.getHand());

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
            int MAX_TUNNEL_DEPTH = 8;
            for(int i = 0; i < MAX_TUNNEL_DEPTH; i++){
                selected = selected.offset(context.getClickedFace().getOpposite().getNormal());
                if(level.getBlockState(selected).getBlock() instanceof DisplacedBlock)
                    continue;
                displace(level, selected);
                if(context.getPlayer() instanceof ServerPlayer splayer && !context.getPlayer().isCreative())
                    context.getItemInHand().hurtAndBreak(1, splayer, slot);
                break;
            }
            level.playSound(null, pos, state.getBlock().getSoundType(state, level, pos, null).getHitSound(),
                    SoundSource.PLAYERS, 1.0F, 1.1F);
            return InteractionResult.SUCCESS;
        }

        boolean displace_worked = displace(level, pos);
        if(displace_worked){
            level.playSound(null, pos, state.getBlock().getSoundType(state, level, pos, null).getHitSound(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);
            if(context.getPlayer() instanceof ServerPlayer splayer && !context.getPlayer().isCreative())
                context.getItemInHand().hurtAndBreak(1, splayer, slot);
            return InteractionResult.SUCCESS;
        }
        level.playSound(null, pos, state.getBlock().getSoundType(state, level, pos, null).getHitSound(),
                SoundSource.PLAYERS, 1.0F, 0.7F);
        return InteractionResult.FAIL;
    }

    private static boolean displace(Level level, BlockPos selected) {
        if(level.getBlockState(selected).getBlock() instanceof ChainDisplacingBlock cdb) {
            cdb.breadthFirstDisplace(level, selected, false, DISPLACER_DISPLACE_TIME);
            return true;
        }
        return DisplacedBlock.displace(level.getBlockState(selected), selected, level, DISPLACER_DISPLACE_TIME);
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
