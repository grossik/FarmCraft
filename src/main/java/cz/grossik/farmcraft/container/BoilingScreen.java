package cz.grossik.farmcraft.container;

import com.mojang.blaze3d.systems.RenderSystem;

import cz.grossik.farmcraft.Main;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoilingScreen extends ContainerScreen<BoilingContainer> {

	private boolean field_214090_m;
	private static final ResourceLocation GUI = new ResourceLocation(Main.MOD_ID, "textures/gui/container/boiling.png");

	public BoilingScreen(BoilingContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

   public void init() {
	   super.init();
	   this.field_214090_m = this.width < 379;
   	}
   
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = this.title.getFormattedText();
		this.font.drawString(s, (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }
	   
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
	      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	      this.minecraft.getTextureManager().bindTexture(GUI);
	      int i = this.guiLeft;
	      int j = this.guiTop;
	      this.blit(i, j, 0, 0, this.xSize, this.ySize);
	}

}
