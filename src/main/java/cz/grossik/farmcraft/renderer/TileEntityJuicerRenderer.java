package cz.grossik.farmcraft.renderer;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import cz.grossik.farmcraft.block.JuicerBlock;
import cz.grossik.farmcraft.tileentity.JuicerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.items.CapabilityItemHandler;

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
	        ItemStack itemstack = inventory.getStackInSlot(0);
        	ItemStack itemstack2 = inventory.getStackInSlot(2);
        	if(itemstack2.isEmpty() == false && itemstack.isEmpty()) {
        		matrix.push();
        		matrix.translate(0.5, 0.5, 0.5);
        		Direction direction = te.getBlockState().get(JuicerBlock.FACING);
                matrix.rotate(Vector3f.YP.rotationDegrees(direction.getHorizontalIndex() * -90F - 90F));
                matrix.translate(-0.5, -0.5, -0.5);
                float height = (5F * 0.0625F);
                height *= ((double) (te.getCookTimeTotal() - 0) / (double) te.getCookTimeTotal()) - 0.45;
        		this.drawFluidHladina(te, matrix, buffer, 11F * 0.0625F, 1.5F * 0.0625F, 7F * 0.0625F, 2F * 0.0625F, height, 2F * 0.0625F, p_225616_5_);
        		matrix.pop();
        	}
        	
	        if(blockstate.get(JuicerBlock.VYPALUJE)) {	        	
	        	if(itemstack != ItemStack.EMPTY) {
	        		matrix.push();
	        		matrix.translate(0.5, 0.5, 0.5);
	        		Direction direction = te.getBlockState().get(JuicerBlock.FACING);
	                matrix.rotate(Vector3f.YP.rotationDegrees(direction.getHorizontalIndex() * -90F - 90F));
	                matrix.translate(-0.5, -0.5, -0.5);
	                float height = (5F * 0.0625F);
	                height *= ((double) (te.getCookTimeTotal() - te.getBurnTime()) / (double) te.getCookTimeTotal()) - 0.45;
	        		this.drawFluidHladina(te, matrix, buffer, 11F * 0.0625F, 1.5F * 0.0625F, 7F * 0.0625F, 2F * 0.0625F, height, 2F * 0.0625F, p_225616_5_);
	        		matrix.pop();
	        		matrix.push();
	        		matrix.translate(0.5, 0.5, 0.5);
	                matrix.rotate(Vector3f.YP.rotationDegrees(direction.getHorizontalIndex() * -90F));
	                matrix.translate(-0.5, -0.5, -0.5);
	        		renderFluid2(te, matrix, buffer, 0.0625F * 7 + 0.01F, 0.0625F * 7, 0.0625F * 9 + 0.01F, 0.0625F * 2 - 0.01F * 2, 0.0625F, 0.0625F * 2);
	                renderFluid(te, matrix, buffer, 0.0625F * 7 + 0.01F, 0.0625F * 8, 0.0625F * 11 + 0.01F, 0.0625F * 2 - 0.01F * 2, -(0.0625F * 6), 0.0625F * 1);
	        		matrix.pop();
	        	}
	        }
		});
	}
	
    private void drawFluidHladina(JuicerTileEntity te, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light){
        Fluid fluid = Fluids.LAVA;
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
    }

    private void renderFluid(@Nonnull JuicerTileEntity tileEntity, MatrixStack matrix, IRenderTypeBuffer bufferIn, float x, float y, float z, float width, float height, float depth){
    	Fluid fluid = Fluids.FLOWING_LAVA;
		Matrix4f matrix4f = matrix.getLast().getMatrix();
        TextureAtlasSprite sprite = ForgeHooksClient.getFluidSprites(tileEntity.getWorld(), tileEntity.getPos(), fluid.getDefaultState())[0];
		if(sprite == null)
			return;
		IVertexBuilder renderer = bufferIn.getBuffer(RenderType.getText(sprite.getAtlasTexture().getTextureLocation()));

		float u1 = sprite.getMinU();
		float v1 = sprite.getMinV();
		float u2 = sprite.getMaxU();
		float v2 = sprite.getMaxV();

		int color = 0xFF0000;
		
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
		float a = 1;
		int light = 15728880;
		// Top
		renderer.pos(matrix4f, x, y + height, z).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y + height, z + depth).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y + height, z + depth).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y + height, z).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();

		// Bottom
		renderer.pos(matrix4f, x + width, y, z).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y, z + depth).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z + depth).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();

		// Sides
		//NORTH
		renderer.pos(matrix4f, x, y + height, z + depth).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z + depth).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y, z + depth).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y + height, z + depth).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();

		//SOUTH
		renderer.pos(matrix4f, x + width, y + height, z).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y, z).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y + height, z).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();

		//WEAST
		renderer.pos(matrix4f, x + width, y + height, z + depth).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y, z + depth).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y, z).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y + height, z).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();

		//EAST
		renderer.pos(matrix4f, x, y + height, z).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z + depth).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y + height, z + depth).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();
	}
    
    private void renderFluid2(@Nonnull JuicerTileEntity tileEntity, MatrixStack matrix, IRenderTypeBuffer bufferIn, float x, float y, float z, float width, float height, float depth){
    	Fluid fluid = Fluids.LAVA;
		Matrix4f matrix4f = matrix.getLast().getMatrix();
        TextureAtlasSprite sprite = ForgeHooksClient.getFluidSprites(tileEntity.getWorld(), tileEntity.getPos(), fluid.getDefaultState())[0];
		if(sprite == null)
			return;
		IVertexBuilder renderer = bufferIn.getBuffer(RenderType.getText(sprite.getAtlasTexture().getTextureLocation()));

		float u1 = sprite.getMinU();
		float v1 = sprite.getMinV();
		float u2 = sprite.getMaxU();
		float v2 = sprite.getMaxV();

		int color = 0xFF0000;
		
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
		float a = 1;
		int light = 15728880;
		// Top
		renderer.pos(matrix4f, x, y + height, z).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y + height, z + depth).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y + height, z + depth).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y + height, z).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();

		// Bottom
		renderer.pos(matrix4f, x + width, y, z).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y, z + depth).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z + depth).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();

		// Sides
		//NORTH
		/*renderer.pos(matrix4f, x, y + height, z + depth).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z + depth).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y, z + depth).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y + height, z + depth).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();
	 	*/
		//SOUTH
		/*renderer.pos(matrix4f, x + width, y + height, z).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y, z).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y + height, z).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();*/

		//WEAST
		renderer.pos(matrix4f, x + width, y + height, z + depth).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y, z + depth).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y, z).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x + width, y + height, z).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();

		//EAST
		renderer.pos(matrix4f, x, y + height, z).color(r, g, b, a).tex(u1, v1).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z).color(r, g, b, a).tex(u1, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y, z + depth).color(r, g, b, a).tex(u2, v2).lightmap(light).endVertex();
		renderer.pos(matrix4f, x, y + height, z + depth).color(r, g, b, a).tex(u2, v1).lightmap(light).endVertex();
	}
}
