package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.util.BeamHelper;
import com.hyperlynx.reactive.util.ConfigMan;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class LightStaffItem extends StaffItem {
    private final static int LIGHT_BREAK_RANGE = 24;

    public LightStaffItem(Block block, Properties props, Function<Player, Player> effect, boolean beam, Item repair_item) {
        super(block, props, effect, beam, 7, repair_item);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(onLastDurability(player.getItemInHand(hand)))
            return InteractionResultHolder.fail(player.getItemInHand(hand));

        if(!level.isClientSide) {
            effectFunction.apply((Player) player);
            player.getItemInHand(hand).hurtAndBreak(1, player, (LivingEntity l) -> {});
        }
        return super.use(level, player, hand);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        Level level = entity.level;
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