package cz.grossik.farmcraft.container;

import cz.grossik.farmcraft.recipe.AbstractJuicerRecipe;
import cz.grossik.farmcraft.recipe.FarmCraftRecipeType;
import cz.grossik.farmcraft.tileentity.TileEntityBoiling;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BoilingContainer extends Container {

	private final IInventory inventory;
	
	public BoilingContainer(int p_i50082_1_, PlayerInventory p_i50082_2_) {
		this(FarmCraftContainerType.BOILING.get(), FarmCraftRecipeType.JUICER_RECIPE, p_i50082_1_, p_i50082_2_);
    }

	public BoilingContainer(int p_i50083_1_, PlayerInventory p_i50083_2_, IInventory p_i50083_3_) {
		this(FarmCraftContainerType.BOILING.get(), FarmCraftRecipeType.JUICER_RECIPE, p_i50083_1_, p_i50083_2_, p_i50083_3_, new IntArray(4));	
	}
	
	private BoilingContainer(ContainerType<?> containerTypeIn, IRecipeType<? extends AbstractJuicerRecipe> recipeTypeIn, int id, PlayerInventory playerInventoryIn) {
		this(containerTypeIn, recipeTypeIn, id, playerInventoryIn, new Inventory(1), new IntArray(4));
	}

	private BoilingContainer(ContainerType<?> containerTypeIn, IRecipeType<? extends AbstractJuicerRecipe> recipeTypeIn, int id, PlayerInventory playerInventoryIn, IInventory furnaceInventoryIn, IIntArray p_i50104_6_) {
		super(containerTypeIn, id);
		assertInventorySize(furnaceInventoryIn, 1);
		assertIntArraySize(p_i50104_6_, 4);
		this.inventory = furnaceInventoryIn;
		
		this.addSlot(new Slot(furnaceInventoryIn, 0, 56, 17));
		
		for(int i = 0; i < 3; ++i) {
	         for(int j = 0; j < 9; ++j) {
	            this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
	         }
	      }

		for(int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(playerInventoryIn, k, 8 + k * 18, 142));
		}
		
		this.trackIntArray(p_i50104_6_);
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getSize() {
		return 1;
	}
	   
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		// TODO Auto-generated method stub
		return inventory.isUsableByPlayer(playerIn);
	}
	
}
