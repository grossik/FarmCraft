package cz.grossik.farmcraft.container;

import cz.grossik.farmcraft.recipe.FarmCraftRecipeType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.IIntArray;

public class JuicerContainer extends AbstractJuicerContainer {
	public JuicerContainer(int p_i50082_1_, PlayerInventory p_i50082_2_) {
		super(FarmCraftContainerType.JUICER.get(), FarmCraftRecipeType.JUICER_RECIPE, p_i50082_1_, p_i50082_2_);
    }

	public JuicerContainer(int p_i50083_1_, PlayerInventory p_i50083_2_, IInventory p_i50083_3_, IIntArray p_i50083_4_) {
		super(FarmCraftContainerType.JUICER.get(), FarmCraftRecipeType.JUICER_RECIPE, p_i50083_1_, p_i50083_2_, p_i50083_3_, p_i50083_4_);	
	}

	@Override
	public void fillStackedContents(RecipeItemHelper itemHelperIn) {
		// TODO Auto-generated method stub
		
	}
}