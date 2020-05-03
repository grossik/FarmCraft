package cz.grossik.farmcraft.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import cz.grossik.farmcraft.entity.ExampleEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ExampleEntityRender extends MobRenderer<ExampleEntity, ExampleEntityModel> {
	   public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/example.png");

	   public ExampleEntityRender(EntityRendererManager renderManagerIn) {
	      super(renderManagerIn, new ExampleEntityModel(), 0.3F);
	   }

	   protected void preRenderCallback(ExampleEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
		      matrixStackIn.scale(0.35F, 0.35F, 0.35F);
		   }

		   protected void applyRotations(ExampleEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
		      if (entityLiving.getIsBatHanging()) {
		         matrixStackIn.translate(0.0D, (double)-0.1F, 0.0D);
		      } else {
		         matrixStackIn.translate(0.0D, (double)(MathHelper.cos(ageInTicks * 0.3F) * 0.1F), 0.0D);
		      }

		      super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
		   }

		@Override
		public ResourceLocation getEntityTexture(ExampleEntity entity) {
			return TEXTURE;
		}
}