package com.hyperlynx.reactive.fx.models;

// Made with Blockbench 4.6.4
// Exported for Minecraft version 1.17 or later with Mojang mappings

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import com.hyperlynx.reactive.entities.SoulCat;

public class SoulCatModel extends EntityModel<SoulCat> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("reactive", "soulcat"), "main");
	private final ModelPart body;

	public SoulCatModel(ModelPart root) {
		this.body = root.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 17.0F, 1.0F));

		PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(1, 1).addBox(-3.0F, -9.0F, -2.0F, 5.0F, 17.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(19, 18).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(10, 30).addBox(-2.5F, -1.0156F, -5.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(18, 39).addBox(-3.0F, -4.0F, -1.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(33, 13).addBox(0.0F, -4.0F, -1.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -13.0F));

		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 7.0F));

		PartDefinition tail1_r1 = tail1.addOrReplaceChild("tail1_r1", CubeListBuilder.create().texOffs(36, 0).addBox(-1.5F, -1.0F, 0.0F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1345F, 0.0F, 0.0F));

		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create(), PartPose.offset(0.0F, 4.875F, 6.35F));

		PartDefinition tail2_r1 = tail2.addOrReplaceChild("tail2_r1", CubeListBuilder.create().texOffs(26, 30).addBox(-1.49F, 0.875F, 1.25F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition backLegL = body.addOrReplaceChild("backLegL", CubeListBuilder.create().texOffs(7, 37).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.1F, 1.0F, 6.0F));

		PartDefinition backLegR = body.addOrReplaceChild("backLegR", CubeListBuilder.create().texOffs(36, 30).addBox(-2.0F, -1.0F, 0.0F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.1F, 1.0F, 6.0F));

		PartDefinition frontLegL = body.addOrReplaceChild("frontLegL", CubeListBuilder.create().texOffs(26, 0).addBox(-1.0F, 2.8F, -2.0F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.2F, -3.0F, -5.0F));

		PartDefinition frontLegR = body.addOrReplaceChild("frontLegR", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, 2.8F, -2.0F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.2F, -3.0F, -5.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(SoulCat p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {}
}