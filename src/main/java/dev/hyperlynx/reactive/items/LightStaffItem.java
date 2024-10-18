package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;

public class LightStaffItem extends StaffItem {
    private final static int LIGHT_BREAK_RANGE = 24;

    public LightStaffItem(Block block, Properties props, BiConsumer<Player, ItemStack> effect, boolean beam, Item repair_item) {
        super(block, props, effect, beam, 7, repair_item);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(onLastDurability(player.getItemInHand(hand)))
            return InteractionResultHolder.fail(player.getItemInHand(hand));

        if(!level.isClientSide) {
            effectFunction.accept(player, player.getItemInHand(hand));
            player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
        return super.use(level, player, hand);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        Level level = entity.level();
        if(entity instanceof Player){
            BlockPos light_to_break = BlockPos.findClosestMatch(entity.getOnPos(), LIGHT_BREAK_RANGE, LIGHT_BREAK_RANGE,
                    (BlockPos pos) -> level.getBlockState(pos).is(Registration.GLOWING_AIR.get())).orElseGet(() -> null);
            if(light_to_break != null){
                level.setBlock(light_to_break, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                ParticleScribe.drawParticleSphere(level, ParticleTypes.SMOKE, light_to_break, 0.5, 0.2, 5);
                level.playSound(null, light_to_break, SoundEvents.GENERIC_BURN, SoundSource.PLAYERS, 0.5F, 1F);
            }
        }
        return false;
    }
}