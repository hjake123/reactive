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

public class HoverQuiltModel extends HierarchicalModel<HoverQuilt> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ReactiveMod.location("hover_quilt"), "main");
	private final ModelPart bone;

	public HoverQuiltModel(ModelPart root) {
		this.bone = root.getChild("bone");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(3, 0).addBox(-7.0F, -1.0F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 16);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
		bone.render(poseStack, buffer, packedLight, packedOverlay, color);
	}

	@Override
	public ModelPart root() {
		return bone;
	}

	public static final AnimationDefinition HOVER = AnimationDefinition.Builder.withLength(11.4286F).looping()
			.addAnimation("bone", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.6667F, KeyframeAnimations.degreeVec(0.667F, -3.5F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.8571F, KeyframeAnimations.degreeVec(0.6424F, -1.0173F, 1.5294F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(4.2857F, KeyframeAnimations.degreeVec(-2.8311F, 0.9997F, 0.0526F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(6.0119F, KeyframeAnimations.degreeVec(1.6821F, 4.5407F, 0.0699F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(8.0952F, KeyframeAnimations.degreeVec(-5.2663F, 1.3009F, 2.9552F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(9.881F, KeyframeAnimations.degreeVec(1.6821F, 4.5407F, 0.0699F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(11.4286F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("bone", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.4286F, KeyframeAnimations.posVec(0.0F, 0.0F, -0.25F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.2738F, KeyframeAnimations.posVec(0.25F, 0.0F, -0.25F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(4.5833F, KeyframeAnimations.posVec(0.35F, 0.0F, 0.05F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(6.4286F, KeyframeAnimations.posVec(0.12F, 0.0F, -0.38F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(7.619F, KeyframeAnimations.posVec(-0.48F, -0.2F, -0.58F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(8.9286F, KeyframeAnimations.posVec(0.12F, 0.0F, -0.38F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(11.4286F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();

	@Override
	public void setupAnim(HoverQuilt entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		setupAnim(entity, ageInTicks);
	}

	public void setupAnim(HoverQuilt entity, float ageInTicks) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.hovering, HOVER, ageInTicks);
	}
}