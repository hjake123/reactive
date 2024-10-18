package dev.hyperlynx.reactive.tests;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReactiveMod.MODID)
public class DisplaceGameTests {
    // Template Location at 'reactive:displace'
    @PrefixGameTestTemplate(false)
    @GameTest(template = "displace_test")
    public static void displace(GameTestHelper helper){
        helper.pressButton(2, 2, 0);
        helper.runAfterDelay(14,  () ->
                helper.succeedWhen(() ->
                helper.assertBlock(
                new BlockPos(0, 3, 1),
                (block -> block.equals(Registration.DISPLACED_BLOCK.get())),
                "Not displaced!")));
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = "chain_displace_test")
    public static void chainDisplace(GameTestHelper helper){
        helper.pressButton(4, 2, 1);
        helper.runAfterDelay(10, () ->
                helper.assertBlock(
                        new BlockPos(4, 5, 4),
                        (block -> block.equals(Registration.DISPLACED_BLOCK.get())),
                        "Not displaced!"));
        helper.runAfterDelay(14, () ->
                helper.succeedWhen(() ->
                helper.assertBlock(
                        new BlockPos(4, 5, 4),
                        (block -> block.equals(Registration.FRAMED_MOTION_SALT_BLOCK.get())),
                        "Did not return!")));
    }
}
