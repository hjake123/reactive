package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.hyperlynx.reactive.advancements.CriteriaTriggers.HARVEST_TRIGGER;

public class OccultSymbolBlock extends SymbolBlock{
    public static BooleanProperty ACTIVE = BlockStateProperties.ENABLED;

    public OccultSymbolBlock(Properties props) {
        super(props);
        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP).setValue(ACTIVE, false));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if(entity instanceof ServerPlayer){
            CriteriaTriggers.PLACE_OCCULT_TRIGGER.trigger((ServerPlayer) entity);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(ACTIVE, false);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource source) {
        if(!state.getValue(ACTIVE))
            return;
        double d0 = source.nextDouble() * 8 - 4;
        double d1 = source.nextDouble() * 8 - 4;
        double d2 = source.nextDouble() * 8 - 4;
        level.addParticle(Registration.SMALL_BLACK_RUNE_PARTICLE, pos.getX()+ d0,pos.getY()+d1, pos.getZ()+d2,0,0,0);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if(level.isClientSide)
            return InteractionResult.PASS;

        if(!state.getValue(ACTIVE))
            return InteractionResult.PASS;

        if(!player.getItemInHand(hand).is(Registration.QUARTZ_BOTTLE.get()))
            return InteractionResult.PASS;

        if(player.getCooldowns().isOnCooldown(Registration.QUARTZ_BOTTLE.get()))
            return InteractionResult.PASS;

        List<Item> bottle_list = List.of(Registration.ACID_BOTTLE.get(), Registration.BODY_BOTTLE.get(), Registration.BLAZE_BOTTLE.get(),
                Registration.VERDANT_BOTTLE.get(), Registration.LIGHT_BOTTLE.get(), Registration.MIND_BOTTLE.get());

        // Which bottle will be extracted, from the list above.
        // Rolls beyond the table shatter the bottle.
        // (Remember, nextInt is exclusive at the top end!)
        int roll = level.random.nextInt(0, 7);
        boolean bottle_broke = false;

        switch(roll){
            case 0 -> {
                // A Bottle of Acid was extracted.
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1500, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1500, 0));
                player.hurt(level.damageSources().magic(), Math.min(12, player.getHealth() - 1));
                player.displayClientMessage(Component.translatable("message.reactive.extract_acid"), true);
            }
            case 1 -> {
                // A Bottle of Stock was extracted.
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 1000, 1));
                player.getFoodData().setFoodLevel(Math.min(0, player.getFoodData().getFoodLevel() - 16));
                player.displayClientMessage(Component.translatable("message.reactive.extract_body"), true);
            }
            case 2 -> {
                // A Bottle of Blaze was extracted.
                player.setTicksFrozen(800);
                player.setRemainingFireTicks(0);
                player.hurt(level.damageSources().freeze(), Math.min(5, player.getHealth() - 1));
                player.displayClientMessage(Component.translatable("message.reactive.extract_blaze"), true);
            }
            case 3 -> {
                // A Bottle of Nature was extracted.
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 800, 2));
                player.hurt(level.damageSources().magic(), 2);
                player.displayClientMessage(Component.translatable("message.reactive.extract_verdant"), true);
            }
            case 4 -> {
                // A Bottle of Light was extracted.
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1500, 0));
                player.displayClientMessage(Component.translatable("message.reactive.extract_light"), true);
            }
            case 5 -> {
                // A Bottle of Mind was extracted.
                if(player.experienceLevel > 4){
                    if(player instanceof ServerPlayer splayer){
                        HARVEST_TRIGGER.trigger(splayer);
                    }
                    player.giveExperienceLevels(-4);
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 150, 2));
                    player.displayClientMessage(Component.translatable("message.reactive.extract_mind"), true);
                    player.playSound(SoundEvents.BELL_RESONATE, 0.8F, 0.8F);
                }else{
                    // If they can't afford the Mind bottle, break the bottle.
                    bottle_broke = true;
                }
            }
            default -> bottle_broke = true;
        }

        ParticleScribe.drawParticleZigZag(level, Registration.SMALL_BLACK_RUNE_PARTICLE, pos, player.blockPosition().above(), 5, 4, 0.7);
        player.getItemInHand(hand).shrink(1);

        if(bottle_broke) {
            player.displayClientMessage(Component.translatable("message.reactive.bottle_broke"), true);
            player.hurt(level.damageSources().generic(), 1);
            level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
        else{
            ItemStack bottled_power = bottle_list.get(roll).getDefaultInstance();
            player.addItem(bottled_power);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 0.8F);
        }

        if(!player.getAbilities().instabuild)
            player.getCooldowns().addCooldown(Registration.QUARTZ_BOTTLE.get(), 80);

        return InteractionResult.SUCCESS;
    }
}
