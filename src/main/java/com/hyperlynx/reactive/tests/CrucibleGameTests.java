package com.hyperlynx.reactive.tests;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.Collection;

@GameTestHolder(ReactiveMod.MODID)
public class CrucibleGameTests {
    @PrefixGameTestTemplate(false)
    @GameTest(template = "crucible_test")
    public static void dissolveItemForPower(GameTestHelper helper){
        BlockPos crucible_absolute_pos = helper.absolutePos(new BlockPos(0, 2, 0));
        ItemEntity shard = new ItemEntity(helper.getLevel(), crucible_absolute_pos.getX() + 0.5, crucible_absolute_pos.getY() + 0.5265, crucible_absolute_pos.getZ() + 0.5, Items.AMETHYST_SHARD.getDefaultInstance());
        shard.setPickUpDelay(50);
        shard.setDeltaMovement(0, 0, 0);
        helper.runAfterDelay(5, () -> helper.getLevel().addFreshEntity(shard));
        helper.runAfterDelay(20, () -> {
            if(!(helper.getBlockEntity(new BlockPos(0, 2, 0)) instanceof CrucibleBlockEntity crucible)){
                throw new GameTestAssertException("Crucible has wrong block entity");
            }
            helper.assertTrue(crucible.getPowerLevel(Powers.MIND_POWER.get()) > 0, "No power was added");
            helper.succeed();
        });
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = "crucible_test")
    public static void dissolveItemForRecipe(GameTestHelper helper){
        BlockPos crucible_absolute_pos = helper.absolutePos(new BlockPos(0, 2, 0));
        ItemEntity shard = new ItemEntity(helper.getLevel(), crucible_absolute_pos.getX() + 0.5, crucible_absolute_pos.getY() + 0.5265, crucible_absolute_pos.getZ() + 0.5, Items.AMETHYST_SHARD.getDefaultInstance());
        shard.setPickUpDelay(50);
        shard.setDeltaMovement(0, 0, 0);
        helper.runAfterDelay(5, () -> helper.getLevel().addFreshEntity(shard));
        helper.runAfterDelay(20, () -> {
            if(!(helper.getBlockEntity(new BlockPos(0, 2, 0)) instanceof CrucibleBlockEntity crucible)){
                throw new GameTestAssertException("Crucible has wrong block entity");
            }
            helper.assertTrue(!helper.getEntities(EntityType.ITEM).isEmpty(), "No residual item");
            helper.succeed();
        });
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = "crucible_test")
    public static void transmuteRecipe(GameTestHelper helper){
        BlockPos crucible_absolute_pos = helper.absolutePos(new BlockPos(0, 2, 0));
        ItemStack r = Items.REDSTONE.getDefaultInstance();
        r.setCount(64);
        ItemEntity redstone = new ItemEntity(helper.getLevel(), crucible_absolute_pos.getX() + 0.5, crucible_absolute_pos.getY() + 0.5265, crucible_absolute_pos.getZ() + 0.5, r);
        redstone.setPickUpDelay(50);
        redstone.setDeltaMovement(0, 0, 0);
        ItemStack input = Items.PAPER.getDefaultInstance();
        ItemEntity input_entity = new ItemEntity(helper.getLevel(), crucible_absolute_pos.getX() + 0.5, crucible_absolute_pos.getY() + 0.5265, crucible_absolute_pos.getZ() + 0.5, input);
        input_entity.setPickUpDelay(50);
        input_entity.setDeltaMovement(0, 0, 0);
        helper.runAfterDelay(5, () -> helper.getLevel().addFreshEntity(redstone));
        helper.runAfterDelay(25, () -> helper.getLevel().addFreshEntity(input_entity));
        helper.runAfterDelay(50, () -> {
            if(!(helper.getBlockEntity(new BlockPos(0, 2, 0)) instanceof CrucibleBlockEntity crucible)){
                throw new GameTestAssertException("Crucible has wrong block entity");
            }
            helper.assertTrue(!helper.getEntities(EntityType.ITEM).isEmpty(), "No residual item");
            boolean could_fail = true;
            for(ItemEntity entity : helper.getEntities(EntityType.ITEM)){
                if(entity.getItem().is(Registration.LITMUS_PAPER.get())){
                    helper.succeed();
                    could_fail = false;
                }
            }
            if(could_fail)
                helper.fail("Transmutation did not occur");
        });
    }

}
