package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.client.gui.ScreenOpener;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Objects;

public class DeskBlock extends Block {
    public DeskBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
            ResourceLocation material_id = stack.get(ReactiveComponentTypes.MATERIAL_ID.get());
            Material material = MaterialMan.fetch(level, material_id);
            if (material.wasDiscovered() && !material.playerDiscoveredThis(player)) {
                player.displayClientMessage(Component.translatable("message.reactive.someone_else_discovered"), true);
                return ItemInteractionResult.FAIL;
            }
            if(level.isClientSide()) {
                ScreenOpener.materialRename(Objects.requireNonNull(material_id));
            }
            return ItemInteractionResult.SUCCESS;
        }
        player.displayClientMessage(Component.translatable("message.reactive.click_with_material"), true);
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        player.displayClientMessage(Component.translatable("message.reactive.click_with_material"), true);
        return InteractionResult.PASS;
    }
}
