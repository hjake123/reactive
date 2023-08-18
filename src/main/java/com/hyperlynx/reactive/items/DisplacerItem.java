package com.hyperlynx.reactive.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hyperlynx.reactive.blocks.ChainDisplacingBlock;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DisplacerItem extends Item {
    public DisplacerItem(Properties props) {
        super(props);
    }
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        double ATTACK_DAMAGE = 3.5;
        double ATTACK_SPEED = -3;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> mainhand_modifier_builder = ImmutableMultimap.builder();
        mainhand_modifier_builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", ATTACK_DAMAGE, AttributeModifier.Operation.ADDITION));
        mainhand_modifier_builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", ATTACK_SPEED, AttributeModifier.Operation.ADDITION));
        return slot == EquipmentSlot.MAINHAND ? mainhand_modifier_builder.build() : super.getDefaultAttributeModifiers(slot);
    }
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

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
                    context.getItemInHand().hurtAndBreak(1, splayer, (LivingEntity) -> {});
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
                context.getItemInHand().hurtAndBreak(1, splayer, (LivingEntity) -> {});
            return InteractionResult.SUCCESS;
        }
        level.playSound(null, pos, state.getBlock().getSoundType(state, level, pos, null).getHitSound(),
                SoundSource.PLAYERS, 1.0F, 0.7F);
        return InteractionResult.FAIL;
    }

    private static boolean displace(Level level, BlockPos selected) {
        if(level.getBlockState(selected).getBlock() instanceof ChainDisplacingBlock cdb) {
            cdb.breadthFirstDisplace(level, selected, false);
            return true;
        }
        return DisplacedBlock.displace(level.getBlockState(selected), selected, level, 200);
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity wielder) {
        stack.hurtAndBreak(2, wielder, (living) -> {
            living.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

}
