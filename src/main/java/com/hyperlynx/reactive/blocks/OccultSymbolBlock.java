package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.fx.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

import java.util.List;
import java.util.Objects;

public class OccultSymbolBlock extends SymbolBlock{
    public static BooleanProperty ACTIVE = BlockStateProperties.ENABLED;

    public OccultSymbolBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP).setValue(ACTIVE, false));
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
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if(!state.getValue(ACTIVE))
            return;
        double d0 = random.nextDouble() * 8 - 4;
        double d1 = random.nextDouble() * 8 - 4;
        double d2 = random.nextDouble() * 8 - 4;
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
        // If the player doesn't have enough xp, we can't roll 6, so Mind won't be extracted.
        // (Remember, nextInt is exclusive at the top end!)
        int roll = level.random.nextInt(0, player.experienceLevel < 3 ? 5 : 6);

        switch(roll){
            case 0 -> {
                // A Bottle of Acid was extracted.
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1500, 1));
                player.hurt(DamageSource.MAGIC, 14);
                player.displayClientMessage(Component.translatable("message.reactive.extract_acid"), true);
            }
            case 1 -> {
                // A Bottle of Stock was extracted.
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 1000, 1));
                player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - 10);
                player.displayClientMessage(Component.translatable("message.reactive.extract_body"), true);
            }
            case 2 -> {
                // A Bottle of Blaze was extracted.
                player.setTicksFrozen(600);
                player.setRemainingFireTicks(0);
                player.hurt(DamageSource.FREEZE, 5);
                player.displayClientMessage(Component.translatable("message.reactive.extract_blaze"), true);
            }
            case 3 -> {
                // A Bottle of Nature was extracted.
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 800, 2));
                player.hurt(DamageSource.MAGIC, 2);
                player.displayClientMessage(Component.translatable("message.reactive.extract_verdant"), true);
            }
            case 4 -> {
                // A Bottle of Light was extracted.
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1500, 0));
                player.displayClientMessage(Component.translatable("message.reactive.extract_light"), true);
            }
            case 5 -> {
                // A Bottle of Mind was extracted.
                player.giveExperienceLevels(-3);
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 2));
                player.displayClientMessage(Component.translatable("message.reactive.extract_mind"), true);
                player.playSound(SoundEvents.BELL_RESONATE, 0.8F, 0.8F);
            }
        }

        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 0.8F);
        ParticleScribe.drawParticleZigZag(level, Registration.SMALL_BLACK_RUNE_PARTICLE, pos, player.blockPosition().above(), 5, 4, 0.7);

        ItemStack bottled_power = bottle_list.get(roll).getDefaultInstance();
        player.getItemInHand(hand).shrink(1);
        player.addItem(bottled_power);

        player.getCooldowns().addCooldown(Registration.QUARTZ_BOTTLE.get(), 120);

        return InteractionResult.SUCCESS;
    }
}