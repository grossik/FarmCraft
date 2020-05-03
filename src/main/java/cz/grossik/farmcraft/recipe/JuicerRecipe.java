package cz.grossik.farmcraft.recipe;

import cz.grossik.farmcraft.init.BlockInit;
import cz.grossik.farmcraft.init.ItemInit;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class JuicerRecipe extends AbstractJuicerRecipe {
   public JuicerRecipe(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookTimeIn) {
	      super(FarmCraftRecipeType.JUICER_RECIPE, FarmCraftRecipeType.JUICER_RES, groupIn, ingredientIn, resultIn, experienceIn, cookTimeIn);
	   }

	   public ItemStack getIcon() {
	      return new ItemStack(ItemInit.juice_glass.get());
	   }

	   public IRecipeSerializer<?> getSerializer() {
	      return FarmCraftRecipeType.JUICER;
	   }
	}