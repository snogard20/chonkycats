package com.chonkycats.client;

import com.chonkycats.entity.ChonkyCatEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class ChonkyCatModel extends HierarchicalModel<ChonkyCatEntity> {
    private final ModelPart root;
    final ModelPart body;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart collar;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightBackLeg;

    public ChonkyCatModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.tail = root.getChild("tail");
        this.tail2 = this.tail.getChild("tail2");
        this.collar = this.head.getChild("collar");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftBackLeg = root.getChild("left_back_leg");
        this.rightBackLeg = root.getChild("right_back_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Chonky cat — shorter, fatter body than vanilla
        // Body: texOffs(20,0), 4w x 10h x 6d — shorter than vanilla 16h, rotated 90deg
        // 10 units long when rotated = compact chonky boi
        partDefinition.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(20, 0)
                        .addBox(-2.0f, -5.0f, -3.0f, 4.0f, 10.0f, 6.0f),
                PartPose.offsetAndRotation(0.0f, 19.0f, 0.0f, Mth.HALF_PI, 0.0f, 0.0f));

        // Head: texOffs(0,0), 5w x 4h x 5d — offset forward to clear body at chonky scale
        PartDefinition headDef = partDefinition.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.5f, -2.0f, -3.0f, 5.0f, 4.0f, 5.0f),
                PartPose.offset(0.0f, 16.5f, -6.0f));

        // Front legs: texOffs(8,13), 2w x 3h x 2d — stubby chonky legs
        partDefinition.addOrReplaceChild("left_front_leg",
                CubeListBuilder.create()
                        .texOffs(8, 13)
                        .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f),
                PartPose.offset(-1.5f, 21.0f, -4.0f));

        partDefinition.addOrReplaceChild("right_front_leg",
                CubeListBuilder.create()
                        .texOffs(8, 13)
                        .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f),
                PartPose.offset(1.5f, 21.0f, -4.0f));

        // Back legs: texOffs(40,0), 2w x 3h x 2d
        partDefinition.addOrReplaceChild("left_back_leg",
                CubeListBuilder.create()
                        .texOffs(40, 0)
                        .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f),
                PartPose.offset(-1.5f, 21.0f, 3.5f));

        partDefinition.addOrReplaceChild("right_back_leg",
                CubeListBuilder.create()
                        .texOffs(40, 0)
                        .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f),
                PartPose.offset(1.5f, 21.0f, 3.5f));

        // Tail — two segments like vanilla cat
        PartDefinition tailDef = partDefinition.addOrReplaceChild("tail",
                CubeListBuilder.create()
                        .texOffs(0, 15)
                        .addBox(-0.5f, 0.0f, -0.5f, 1.0f, 4.0f, 1.0f),
                PartPose.offsetAndRotation(0.0f, 17.0f, 5.0f, 0.9f, 0.0f, 0.0f));

        // Tip segment: child of tail base
        tailDef.addOrReplaceChild("tail2",
                CubeListBuilder.create()
                        .texOffs(0, 15)
                        .addBox(-0.5f, 0.0f, -0.5f, 1.0f, 4.0f, 1.0f),
                PartPose.offset(0.0f, 4.0f, 0.0f));

        // Collar — child of head, follows head rotation
        headDef.addOrReplaceChild("collar",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.5f, 1.5f, -3.0f, 5.0f, 1.0f, 5.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f));

        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    public static LayerDefinition createArmorLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Armor body — same geometry as base body, inflated 0.25 units outward
        partDefinition.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(20, 0)
                        .addBox(-2.0f, -5.0f, -3.0f, 4.0f, 10.0f, 6.0f,
                                new CubeDeformation(0.25f)),
                PartPose.offsetAndRotation(0.0f, 19.0f, 0.0f, Mth.HALF_PI, 0.0f, 0.0f));

        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(ChonkyCatEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);

        this.head.yRot = netHeadYaw * ((float) Math.PI / 180.0f);
        this.head.xRot = headPitch * ((float) Math.PI / 180.0f);

        float legSwing = Mth.cos(limbSwing * 0.6662f) * 0.8f * limbSwingAmount;
        this.leftFrontLeg.xRot = legSwing;
        this.rightFrontLeg.xRot = -legSwing;
        this.leftBackLeg.xRot = -legSwing;
        this.rightBackLeg.xRot = legSwing;

        // Tail sway — base segment
        this.tail.yRot = Mth.cos(ageInTicks * 0.15f) * 0.3f;
        // Tail tip — slight delayed curl
        this.tail2.xRot = -0.2f + Mth.cos(ageInTicks * 0.15f + 1.0f) * 0.15f;

        if (entity.isInSittingPose()) {
            // When sitting: body stays mostly flat, just lower everything
            // Don't change body xRot — keep it at HALF_PI (horizontal)
            this.body.y = 21.0f; // lower body

            this.head.y = 18.0f;
            this.head.z = -4.5f;

            // Front legs extend forward slightly
            this.leftFrontLeg.xRot = -0.4f;
            this.rightFrontLeg.xRot = -0.4f;
            this.leftFrontLeg.y = 22.0f;
            this.rightFrontLeg.y = 22.0f;

            // Back legs tuck flat
            this.leftBackLeg.xRot = -Mth.HALF_PI;
            this.rightBackLeg.xRot = -Mth.HALF_PI;
            this.leftBackLeg.y = 23.5f;
            this.rightBackLeg.y = 23.5f;
            this.leftBackLeg.z = 2.0f;
            this.rightBackLeg.z = 2.0f;

            this.tail.y = 20.0f;
            this.tail.z = 3.5f;
            this.tail.xRot = Mth.HALF_PI; // tail lies flat along the ground
            this.tail.yRot = Mth.cos(ageInTicks * 0.1f) * 0.4f; // lazy side-to-side sway
            this.tail2.xRot = 0.0f; // tip stays flat too
        }

        this.collar.visible = entity.isTame();
    }
}
