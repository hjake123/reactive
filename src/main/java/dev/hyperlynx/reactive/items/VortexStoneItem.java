package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VortexStoneItem extends Item {
    private static final double STRENGTH = 0.8;
    private static final double TOP_SPEED = 1.6;

    public VortexStoneItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        Vec3 impulse = player.getLookAngle().scale(STRENGTH);
        var new_movement = player.getDeltaMovement().add(impulse);
        if(player.hasEffect(Registration.NULL_GRAVITY) && new_movement.length() > TOP_SPEED){
            return InteractionResult.FAIL;
        }
        player.setDeltaMovement(new_movement);
        player.resetFallDistance();
        level.playSound(null, player.blockPosition(), SoundEvents.BREEZE_CHARGE, SoundSource.PLAYERS, 1.0F, 0.95F + (level.random.nextFloat()*0.1F));
        ParticleScribe.drawParticle(level, ParticleTypes.GUST_EMITTER_SMALL, player.getX(), player.getY(), player.getZ());
        player.getCooldowns().addCooldown(Registration.VORTEX_STONE.get(), ConfigMan.SERVER.vortexStoneCooldown.get());

        if(!player.hasInfiniteMaterials()){
            var stack = player.getItemInHand(hand);
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            if(stack.getDamageValue() == stack.getMaxDamage() - 1){
                player.setItemInHand(hand, Items.PACKED_ICE.getDefaultInstance());
            }
        }
        return InteractionResult.SUCCESS;
    }
}
