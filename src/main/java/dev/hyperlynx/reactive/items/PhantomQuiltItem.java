package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.entities.HoverQuilt;
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
        HoverQuilt quilt = Registration.HOVER_QUILT.get().create(level);
        if(quilt == null) {
            return InteractionResult.FAIL;
        }
        quilt.setPos(summon_pos);
        level.addFreshEntity(quilt);
        if(!player.getAbilities().instabuild)
            context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        HoverQuilt quilt = Registration.HOVER_QUILT.get().create(level);
        if(quilt == null) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        quilt.setPos(player.getEyePosition().add(player.getLookAngle().scale(1.5)));
        level.addFreshEntity(quilt);
        if(!player.getAbilities().instabuild)
            player.getItemInHand(hand).shrink(1);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
