package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.entites.HoverQuilt;
import dev.hyperlynx.reactive.registration.ReactiveEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PhantomQuiltItem extends Item {
    public PhantomQuiltItem(Properties pProperties) {
        super(pProperties);
    }

    private static final int ACTIVATE_HEIGHT = 20;

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected){
        if (entity.fallDistance > ACTIVATE_HEIGHT) {
            // TODO: Maybe summon the entity below you?
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        Vec3 summon_pos = context.getClickedPos().offset(context.getClickedFace().getNormal()).getCenter();
        HoverQuilt quilt = ReactiveEntityTypes.HOVER_QUILT.get().create(level);
        quilt.setPos(summon_pos);
        level.addFreshEntity(quilt);
        context.getItemInHand().consume(1, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        HoverQuilt quilt = ReactiveEntityTypes.HOVER_QUILT.get().create(level);
        quilt.setPos(player.getEyePosition().add(player.getLookAngle().scale(1.5)));
        level.addFreshEntity(quilt);
        player.getItemInHand(hand).consume(1, player);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
