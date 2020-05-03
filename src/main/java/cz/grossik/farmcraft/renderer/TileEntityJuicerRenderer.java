package cz.grossik.farmcraft.renderer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import cz.grossik.farmcraft.block.JuicerBlock;
import cz.grossik.farmcraft.tileentity.JuicerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class TileEntityJuicerRenderer extends TileEntityRenderer<JuicerTileEntity> {

	public TileEntityJuicerRenderer(TileEntityRendererDispatcher p_i226006_1_) {
		super(p_i226006_1_);
	}

	@Override
	public void render(JuicerTileEntity te, float p_225616_2_, MatrixStack matrix,
			IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_) {
		te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inventory -> {
			BlockState blockstate = te.getBlockState();
	        if (!(blockstate.getBlock() instanceof JuicerBlock)) {
	            return;
	        }
	        if(blockstate.get(JuicerBlock.VYPALUJE)) {
	        	ItemStack itemstack = inventory.getStackInSlot(0);
	        	if(itemstack != ItemStack.EMPTY) {
	        		matrix.push();
	        		matrix.translate(0.5, 0.5, 0.5);
	                Direction direction = te.getBlockState().get(JuicerBlock.FACING);
	                matrix.rotate(Vector3f.XP.rotationDegrees(direction.getHorizontalIndex() * -90F - 90F));
	                matrix.translate(-0.5, -0.5, -0.5);
	        		this.drawFluidHladina(te, matrix, buffer, 11F * 0.0625F, 1.5F * 0.0625F, 7F * 0.0625F, 2F * 0.0625F, 5F * 0.0625F, 2F * 0.0625F, p_225616_5_);
	        		matrix.pop();
	        	}
	        }
			/*BlockState blockstate = te.getBlockState();
	        if (!(blockstate.getBlock() instanceof JuicerBlock)) {
	            return;
	        }
	        if(blockstate.get(JuicerBlock.VYPALUJE)) {
	        	ItemStack itemstack = inventory.getStackInSlot(0);
	        	if(itemstack != ItemStack.EMPTY) {
					matrix.func_227861_a_(.5, .75, .5);
					matrix.func_227863_a_(new Quaternion(0f, 45f, 0f, true));
					matrix.func_227863_a_(new Quaternion(0f, 0f, 135f, true));
					
					Minecraft.getInstance().getItemRenderer().func_229110_a_(itemstack, TransformType.FIXED, p_225616_5_, p_225616_6_, matrix, buffer);
	        	}
	        }*/
	        
    		/*matrix.func_227860_a_();
    		matrix.func_227861_a_(0.5, 0.5, 0.5);
            Direction direction = te.getBlockState().get(JuicerBlock.FACING);
            matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(direction.getHorizontalIndex() * -90F - 90F));
            matrix.func_227861_a_(-0.5, -0.5, -0.5);
            
            //0.0625F == 1 pixel
            this.drawFluid(te, matrix, buffer, 9F * 0.0625F, 7F * 0.0625F, 7F * 0.0625F, 2F * 0.0625F, 1F * 0.0625F, 2F * 0.0625F, p_225616_5_);
            this.drawFluid(te, matrix, buffer, 11F * 0.0625F, 3F * 0.0625F, 7F * 0.0625F, 1F * 0.0625F, 5F * 0.0625F, 2F * 0.0625F, p_225616_5_);
            matrix.func_227865_b_();*/
		});
	}
	
    private void drawFluidHladina(JuicerTileEntity te, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light){
        Fluid fluid = Fluids.WATER;
        if(fluid == Fluids.EMPTY)
            return;

        TextureAtlasSprite sprite = ForgeHooksClient.getFluidSprites(te.getWorld(), te.getPos(), fluid.getDefaultState())[0];

        float minU = sprite.getMinU();
        float maxU = Math.min(minU + (sprite.getMaxU() - minU) * depth, sprite.getMaxU());
        float minV = sprite.getMinV();
        float maxV = Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());
        int waterColor = 0xFF0000;
        float red = (float)(waterColor >> 16 & 255) / 255.0F;
        float green = (float)(waterColor >> 8 & 255) / 255.0F;
        float blue = (float)(waterColor & 255) / 255.0F;

        
        height *= ((double) (te.getCookTimeTotal() - te.getBurnTime()) / (double) te.getCookTimeTotal()) - 0.45;
        //System.err.println(te.getCookTime() + " - " + te.getCookTimeTotal() + " - " + te.getBurnTime());

        IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.getTranslucent());
        Matrix4f matrix = matrixStack.getLast().getMatrix();
    	
        //top
    	buffer.pos(matrix, x, y + height, z).color(red, green, blue, 1.0F).tex(maxU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x, y + height, z + depth).color(red, green, blue, 1.0F).tex(minU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y + height, z + depth).color(red, green, blue, 1.0F).tex(minU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y + height, z).color(red, green, blue, 1.0F).tex(maxU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    }

    private void drawFluid(JuicerTileEntity te, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light){
        Fluid fluid = Fluids.WATER;
        if(fluid == Fluids.EMPTY)
            return;

        TextureAtlasSprite sprite = ForgeHooksClient.getFluidSprites(te.getWorld(), te.getPos(), fluid.getDefaultState())[0];

        float minU = sprite.getMinU();
        float maxU = Math.min(minU + (sprite.getMaxU() - minU) * depth, sprite.getMaxU());
        float minV = sprite.getMinV();
        float maxV = Math.min(minV + (sprite.getMaxV() - minV) * width, sprite.getMaxV());
        int waterColor = 0xFF0000;
        float red = (float)(waterColor >> 16 & 255) / 255.0F;
        float green = (float)(waterColor >> 8 & 255) / 255.0F;
        float blue = (float)(waterColor & 255) / 255.0F;

        IVertexBuilder buffer = renderTypeBuffer.getBuffer(RenderType.getTranslucent());
        Matrix4f matrix = matrixStack.getLast().getMatrix();
    	
        //top
    	buffer.pos(matrix, x, y + height, z).color(red, green, blue, 1.0F).tex(maxU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x, y + height, z + depth).color(red, green, blue, 1.0F).tex(minU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y + height, z + depth).color(red, green, blue, 1.0F).tex(minU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y + height, z).color(red, green, blue, 1.0F).tex(maxU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
        
    	//bottom
    	buffer.pos(matrix, x, y, z).color(red, green, blue, 1.0F).tex(maxU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x, y, z + depth).color(red, green, blue, 1.0F).tex(minU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y, z + depth).color(red, green, blue, 1.0F).tex(minU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y, z).color(red, green, blue, 1.0F).tex(maxU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();

    	//east
    	buffer.pos(matrix, x, y, z).color(red, green, blue, 1.0F).tex(maxU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y, z).color(red, green, blue, 1.0F).tex(minU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y + height, z).color(red, green, blue, 1.0F).tex(minU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x, y + height, z).color(red, green, blue, 1.0F).tex(maxU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    
    	//west
    	buffer.pos(matrix, x, y, z + depth).color(red, green, blue, 1.0F).tex(maxU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y, z + depth).color(red, green, blue, 1.0F).tex(minU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y + height, z + depth).color(red, green, blue, 1.0F).tex(minU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x, y + height, z + depth).color(red, green, blue, 1.0F).tex(maxU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    
    	buffer.pos(matrix, x, y, z).color(red, green, blue, 1.0F).tex(maxU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x, y, z + depth).color(red, green, blue, 1.0F).tex(minU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x, y + height, z + depth).color(red, green, blue, 1.0F).tex(minU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x, y + height, z).color(red, green, blue, 1.0F).tex(maxU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    
    	buffer.pos(matrix, x + width, y, z).color(red, green, blue, 1.0F).tex(maxU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y, z + depth).color(red, green, blue, 1.0F).tex(minU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y + height, z + depth).color(red, green, blue, 1.0F).tex(minU, minV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    	buffer.pos(matrix, x + width, y + height, z).color(red, green, blue, 1.0F).tex(maxU, maxV).lightmap(light).normal(1.0F, 1.0F, 1.0F).endVertex();
    }
}
