package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.Objects;

public class AlchemyScroll extends Item {
    public AlchemyScroll(Properties props) {
        super(props);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        if(!PatchouliAPI.get().isStub()){
            player.addItem(PatchouliAPI.get().getBookStack(Objects.requireNonNull(ResourceLocation.tryParse("reactive:journal"))));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().getBlockState(context.getClickedPos()).is(Blocks.CAULDRON)){
            if(context.getLevel().isClientSide){
                for(int i = 0; i < 30; i++){
                    context.getLevel().addParticle(ParticleTypes.ENCHANT,
                            context.getClickedPos().getX() + context.getLevel().random.nextFloat()/1.2,
                            context.getClickedPos().getY() + context.getLevel().random.nextFloat()/1.4 + 1,
                            context.getClickedPos().getZ() + context.getLevel().random.nextFloat()/1.2,
                            context.getLevel().random.nextFloat() - 0.5,
                            context.getLevel().random.nextFloat() - 0.5,
                            context.getLevel().random.nextFloat() - 0.5);
                }
            }else{
                context.getLevel().setBlock(context.getClickedPos(), Registration.CRUCIBLE.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.PLAYERS, 1.0F, 0.8F);
            }
            context.getItemInHand().shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
