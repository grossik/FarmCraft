package cz.grossik.farmcraft.item;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemStickSeed extends Item {

	private int type;
	private Item output;
	
	public ItemStickSeed(Properties properties, int type, Item output) {
		super(properties);
		this.type = type;
		this.output = output;
	}
	
	public int getType() {
		return this.type;
	}
	
	public Item getItemOutput() {
		return this.output;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new StringTextComponent("Use a Wooden stick for planting").applyTextStyle(TextFormatting.BLUE));
	}
}
