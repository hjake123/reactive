package dev.hyperlynx.reactive.client.models;
// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.entites.HoverQuilt;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.neoforged.neoforge.client.entity.animation.json.AnimationHolder;

public class HoverQuiltModel extends HierarchicalModel<HoverQuilt> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ReactiveMod.location("hover_quilt"), "main");
	private final ModelPart root;

	public HoverQuiltModel(ModelPart root) {
		this.root = root.getChild("root");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(3, 0).addBox(-7.0F, -1.0F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 16);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
		root.render(poseStack, buffer, packedLight, packedOverlay, color);
	}

	@Override
	public ModelPart root() {
		return root;
	}

	public static final AnimationHolder HOVER = HierarchicalModel.getAnimation(ReactiveMod.location("hover_quilt"));

	@Override
	public void setupAnim(HoverQuilt entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		setupAnim(entity, ageInTicks);
	}

	public void setupAnim(HoverQuilt entity, float ageInTicks) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.hovering, HOVER, ageInTicks);
	}
}