package com.hyperlynx.reactive.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Helper {
    // Taken from getPOVPlayerLook()
    public static BlockHitResult playerRayTrace(Level pLevel, Player pPlayer, ClipContext.Fluid pFluidMode, ClipContext.Block pBlockMode, double range) {
        float f = pPlayer.getXRot();
        float f1 = pPlayer.getYRot();
        Vec3 vec3 = pPlayer.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vec31 = vec3.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
        return pLevel.clip(new ClipContext(vec3, vec31, pBlockMode, pFluidMode, pPlayer));
    }

    // Stolen from Botania's repository here: (https://github.com/VazkiiMods/Botania/blob/9d468aadc9293ea8652092bc4caf804b61fc04c9/Xplat/src/main/java/vazkii/botania/client/render/tile/RenderTileAltar.java)
    public static void renderIcon(PoseStack ms, VertexConsumer builder, TextureAtlasSprite sprite, int color, float alpha, int overlay, int light) {
        int red = ((color >> 16) & 0xFF);
        int green = ((color >> 8) & 0xFF);
        int blue = (color & 0xFF);
        Matrix4f mat = ms.last().pose();
        builder.vertex(mat, 0, 1, 0).color(red, green, blue, (int) (alpha * 255F)).uv(sprite.getU0(), sprite.getV1()).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        builder.vertex(mat, 1, 1, 0).color(red, green, blue, (int) (alpha * 255F)).uv(sprite.getU1(), sprite.getV1()).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        builder.vertex(mat, 1, 0, 0).color(red, green, blue, (int) (alpha * 255F)).uv(sprite.getU1(), sprite.getV0()).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        builder.vertex(mat, 0, 0, 0).color(red, green, blue, (int) (alpha * 255F)).uv(sprite.getU0(), sprite.getV0()).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
    }
}
