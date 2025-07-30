package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.entites.HoverQuilt;
import dev.hyperlynx.reactive.registration.ReactiveEntityTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PhantomQuiltItem extends Item {
    public PhantomQuiltItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        Vec3 summon_pos = context.getClickedPos().offset(context.getClickedFace().getNormal()).getCenter();
        HoverQuilt quilt = ReactiveEntityTypes.HOVER_QUILT.get().create(level);
        if(quilt == null) {
            return InteractionResult.FAIL;
        }
        quilt.setPos(summon_pos);
        level.addFreshEntity(quilt);
        context.getItemInHand().consume(1, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        HoverQuilt quilt = ReactiveEntityTypes.HOVER_QUILT.get().create(level);
        if(quilt == null) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        quilt.setPos(player.getEyePosition().add(player.getLookAngle().scale(1.5)));
        level.addFreshEntity(quilt);
        player.getItemInHand(hand).consume(1, player);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
