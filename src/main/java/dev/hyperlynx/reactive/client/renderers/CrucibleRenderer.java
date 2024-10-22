package dev.hyperlynx.reactive.client.renderers;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.Reaction;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.blocks.CrucibleBlock;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.util.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import dev.hyperlynx.reactive.ConfigMan;

import java.util.Objects;

public class CrucibleRenderer implements BlockEntityRenderer<CrucibleBlockEntity> {

    private final BlockRenderDispatcher blockRenderDispatcher;

    public CrucibleRenderer(BlockEntityRendererProvider.Context context){
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    private TextureAtlasSprite getSprite(CrucibleBlockEntity crucible){
        if(ConfigMan.CLIENT.doNotChangeWaterTexture.get())
            return this.blockRenderDispatcher.getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon(ModelData.EMPTY);

        if(crucible.integrity < 20 && crucible.integrity > 8){
            return this.blockRenderDispatcher.getBlockModel(Registration.DUMMY_NOISE_WATER.get().defaultBlockState()).getParticleIcon(ModelData.EMPTY);
        }

        int threshold = CrucibleBlockEntity.CRUCIBLE_MAX_POWER/2;

        if(crucible.getTotalPowerLevel() > threshold){
            Power max_power = null;
            int max_level = 0;

            for(Power power: crucible.getPowerMap().keySet()){
                if(crucible.getPowerLevel(power) > max_level){
                    max_power = power;
                    max_level = crucible.getPowerLevel(power);
                }
            }

            if(max_level > threshold){
                return this.blockRenderDispatcher.getBlockModel(max_power.getWaterRenderBlock().defaultBlockState()).getParticleIcon(ModelData.EMPTY);
            }
        }
        return this.blockRenderDispatcher.getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon(ModelData.EMPTY);
    }

    private void checkReactions(CrucibleBlockEntity crucible){
        crucible.reactions_to_render.clear();
        for(Reaction r : ReactiveMod.REACTION_MAN.getReactions()){
            if(r.conditionsMet(crucible) == Reaction.Status.REACTING){
                crucible.reactions_to_render.add(r);
            }
        }
    }

    private void renderReactions(CrucibleBlockEntity crucible){
        for(Reaction r : crucible.reactions_to_render){
            r.render(crucible.getLevel(), crucible);
        }
    }

    private void renderElectricity(CrucibleBlockEntity crucible){
        float speedDivisor = 27F;
        if(crucible.electricCharge > 0){
            ParticleScribe.drawParticleCrucibleTop(Objects.requireNonNull(crucible.getLevel()), ParticleTypes.ELECTRIC_SPARK, crucible.getBlockPos(),
                    0.05F,
                    (crucible.getLevel().random.nextFloat()-0.5)/speedDivisor,
                    (crucible.getLevel().random.nextFloat()-0.5)/speedDivisor,
                    (crucible.getLevel().random.nextFloat()-0.5)/speedDivisor
            );
        }
    }

    private void renderSculkCharge(CrucibleBlockEntity crucible){
        float speedDivisor = 27F;
        if(!crucible.sculkSpreader.getCursors().isEmpty()){
            ParticleScribe.drawParticleCrucibleTop(Objects.requireNonNull(crucible.getLevel()), ParticleTypes.SCULK_SOUL, crucible.getBlockPos(),
                    0.05F,
                    (crucible.getLevel().random.nextFloat()-0.5)/speedDivisor,
                    (crucible.getLevel().random.nextFloat()-0.5)/speedDivisor,
                    (crucible.getLevel().random.nextFloat()-0.5)/speedDivisor
            );
        }
    }

    @Override
    public void render(@NotNull CrucibleBlockEntity crucible, float partialTicks, PoseStack pose_stack, @NotNull MultiBufferSource buffer_source, int light, int overlay) {
        pose_stack.pushPose();
        // 0.5625 is the 'full' water level.
        pose_stack.translate(0, 0.5625, 0);
        pose_stack.mulPose(Axis.XP.rotationDegrees(90f));
        if(crucible.getBlockState().getValue(CrucibleBlock.FULL)) {
            TextureAtlasSprite sprite = getSprite(crucible);
            Color color = crucible.getCombinedColor(BiomeColors.getAverageWaterColor(Objects.requireNonNull(crucible.getLevel()), crucible.getBlockPos()));
            VertexConsumer consumer = buffer_source.getBuffer(RenderType.TRANSLUCENT);
            renderIcon(pose_stack, consumer, sprite, color, crucible.getOpacity(), overlay, light);

            if(crucible.getPowerLevel(Powers.ASTRAL_POWER.get()) > 0){
                pose_stack.translate(0, 0, 0.05);
                consumer = buffer_source.getBuffer(RenderType.endPortal());
                renderEndPortalWater(pose_stack, consumer, overlay, light);
            }
        }
        pose_stack.popPose();

        if(Minecraft.getInstance().isPaused()){
            return;
        }

        // Every 30 frames, check which reactions to render.
        crucible.render_tick_counter++;
        if(crucible.render_tick_counter > 30){
            crucible.render_tick_counter = 0;
            checkReactions(crucible);
        }

        renderReactions(crucible);
        renderElectricity(crucible);
        renderSculkCharge(crucible);
    }

    // From Botania's repository here: (https://github.com/VazkiiMods/Botania/blob/9d468aadc9293ea8652092bc4caf804b61fc04c9/Xplat/src/main/java/vazkii/botania/client/render/tile/RenderTileAltar.java)
    public static void renderIcon(PoseStack ms, VertexConsumer builder, TextureAtlasSprite sprite, Color color, float alpha, int overlay, int light) {
        Matrix4f mat = ms.last().pose();
        // Due to previous rotation, Y and Z are switched.
        builder.addVertex(mat, 0.19f, 0.81f, 0).setColor(color.red, color.green, color.blue, (int) (alpha * 255F)).setUv(sprite.getU0(), sprite.getV1()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        builder.addVertex(mat, 0.81f, 0.81f, 0).setColor(color.red, color.green, color.blue, (int) (alpha * 255F)).setUv(sprite.getU1(), sprite.getV1()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        builder.addVertex(mat, 0.81f, 0.19f, 0).setColor(color.red, color.green, color.blue, (int) (alpha * 255F)).setUv(sprite.getU1(), sprite.getV0()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        builder.addVertex(mat, 0.19f, 0.19f, 0).setColor(color.red, color.green, color.blue, (int) (alpha * 255F)).setUv(sprite.getU0(), sprite.getV0()).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
    }

    public static void renderEndPortalWater(PoseStack pose_stack, VertexConsumer consumer, int overlay, int light) {
        Matrix4f pose = pose_stack.last().pose();
        // Due to previous rotation, Y and Z are switched.
        consumer.addVertex(pose, 0.19f, 0.81f, 0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        consumer.addVertex(pose, 0.81f, 0.81f, 0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        consumer.addVertex(pose, 0.81f, 0.19f, 0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
        consumer.addVertex(pose, 0.19f, 0.19f, 0).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
    }
}
