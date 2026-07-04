package com.chonkycats.client;

import com.chonkycats.ChonkyCatsClient;
import com.chonkycats.ChonkyCatsMod;
import com.chonkycats.entity.ChonkyCatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ChonkyCatRenderer extends MobRenderer<ChonkyCatEntity, ChonkyCatModel> {
    private static final String[] VARIANT_NAMES = {
            "tabby", "tuxedo", "red", "siamese", "british", "calico",
            "persian", "ragdoll", "white", "jellie", "black"
    };

    private static final ResourceLocation[] TEXTURES;
    private static final ResourceLocation COLLAR_TEXTURE;

    static {
        TEXTURES = new ResourceLocation[VARIANT_NAMES.length];
        for (int i = 0; i < VARIANT_NAMES.length; i++) {
            TEXTURES[i] = ResourceLocation.fromNamespaceAndPath(
                    ChonkyCatsMod.MOD_ID, "textures/entity/chonky_cat_" + VARIANT_NAMES[i] + ".png");
        }
        COLLAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(
                ChonkyCatsMod.MOD_ID, "textures/entity/chonky_cat_collar.png");
    }

    public ChonkyCatRenderer(EntityRendererProvider.Context context) {
        super(context, new ChonkyCatModel(context.bakeLayer(ChonkyCatsClient.CHONKY_CAT_LAYER)), 0.5f);
        this.addLayer(new CollarLayer(this));
        this.addLayer(new ArmorLayer(this,
                context.bakeLayer(ChonkyCatsClient.CHONKY_CAT_ARMOR_LAYER)));
    }

    @Override
    protected void scale(ChonkyCatEntity entity, PoseStack poseStack, float partialTick) {
        float s = entity.getSizeScale();
        poseStack.scale(s * 1.5f, s, s * 1.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ChonkyCatEntity entity) {
        int variant = entity.getVariant();
        if (variant < 0 || variant >= TEXTURES.length) variant = 0;
        return TEXTURES[variant];
    }

    private static class CollarLayer extends RenderLayer<ChonkyCatEntity, ChonkyCatModel> {
        public CollarLayer(ChonkyCatRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                           ChonkyCatEntity entity, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.isTame() && !entity.isInvisible()) {
                VertexConsumer vertexConsumer = buffer.getBuffer(
                        RenderType.entityCutoutNoCull(COLLAR_TEXTURE));
                this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight,
                        OverlayTexture.NO_OVERLAY);
            }
        }
    }

    private static class ArmorLayer extends RenderLayer<ChonkyCatEntity, ChonkyCatModel> {
        private static final ResourceLocation[] ARMOR_TEXTURES = {
                null, // 0 = none
                ResourceLocation.fromNamespaceAndPath(ChonkyCatsMod.MOD_ID, "textures/entity/armor_leather.png"),
                ResourceLocation.fromNamespaceAndPath(ChonkyCatsMod.MOD_ID, "textures/entity/armor_iron.png"),
                ResourceLocation.fromNamespaceAndPath(ChonkyCatsMod.MOD_ID, "textures/entity/armor_gold.png"),
                ResourceLocation.fromNamespaceAndPath(ChonkyCatsMod.MOD_ID, "textures/entity/armor_diamond.png"),
                ResourceLocation.fromNamespaceAndPath(ChonkyCatsMod.MOD_ID, "textures/entity/armor_netherite.png"),
        };

        private final ModelPart armorBody;

        public ArmorLayer(ChonkyCatRenderer renderer, ModelPart armorRoot) {
            super(renderer);
            this.armorBody = armorRoot.getChild("body");
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                           ChonkyCatEntity entity, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            int armorType = entity.getArmorType();
            if (armorType > 0 && armorType < ARMOR_TEXTURES.length && !entity.isInvisible()) {
                ResourceLocation armorTex = ARMOR_TEXTURES[armorType];
                if (armorTex != null) {
                    // Sync armor body pose with base body (tracks sitting, etc.)
                    ModelPart baseBody = this.getParentModel().body;
                    this.armorBody.x = baseBody.x;
                    this.armorBody.y = baseBody.y;
                    this.armorBody.z = baseBody.z;
                    this.armorBody.xRot = baseBody.xRot;
                    this.armorBody.yRot = baseBody.yRot;
                    this.armorBody.zRot = baseBody.zRot;

                    VertexConsumer vertexConsumer = buffer.getBuffer(
                            RenderType.entityTranslucent(armorTex));
                    this.armorBody.render(poseStack, vertexConsumer, packedLight,
                            OverlayTexture.NO_OVERLAY);
                }
            }
        }
    }
}
