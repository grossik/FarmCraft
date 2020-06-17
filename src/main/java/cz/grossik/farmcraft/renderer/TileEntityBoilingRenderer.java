package cz.grossik.farmcraft.renderer;

import java.awt.Color;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import cz.grossik.farmcraft.Main;
import cz.grossik.farmcraft.block.BoilingBlock;
import cz.grossik.farmcraft.block.JuicerBlock;
import cz.grossik.farmcraft.event.ModelHandle;
import cz.grossik.farmcraft.init.BlockInit;
import cz.grossik.farmcraft.multiblock.BoilingPartIndex;
import cz.grossik.farmcraft.tileentity.JuicerTileEntity;
import cz.grossik.farmcraft.tileentity.TileEntityBoiling;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityBoilingRenderer extends TileEntityRenderer<TileEntityBoiling> {

	public ModelHandle model;
	
	public TileEntityBoilingRenderer(TileEntityRendererDispatcher p_i226006_1_) {
		super(p_i226006_1_);
		
		model = new ModelHandle(new ResourceLocation(Main.MOD_ID, "models/block/boiling.obj"));
	}
    
	@Override
	public void render(TileEntityBoiling te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderBuffers,
            int combinedLight, int combinedOverlay) {

		 matrixStack.push();
	        matrixStack.translate(0.5, 1, 0.5);

	        float timeRandom = MathHelper.getPositionRandom(te.getPos()) % 360;

	        float time = (te.getWorld().getGameTime() + timeRandom + partialTicks) * 1.5f;

	        float angle1 = time * 2.5f + 120 * (1 + (float) Math.sin(time * 0.05f));
	        float angle2 = time * 0.9f;
	        float bob = (float) Math.sin(time * (Math.PI / 180) * 2.91) * 0.06f;

	        matrixStack.translate(0, bob, 0);

	        for (int i = 0; i < 1; i++)
	        {
	            matrixStack.push();

	            float angle3 = angle2 + 90 * i;
	            matrixStack.rotate(Vector3f.YP.rotationDegrees(angle3));
	            matrixStack.translate(0.6, 0, 0);

	            matrixStack.rotate(Vector3f.YP.rotationDegrees(angle1));

	            model.render(renderBuffers, RenderType.getEntityTranslucent(new ResourceLocation("textures/block/diamond_block.png")), matrixStack, combinedLight, 0xFFFFFFFF);

	            matrixStack.pop();
	        }
	        matrixStack.pop();
	}
	
}
