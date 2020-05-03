package cz.grossik.farmcraft.recipe;

import cz.grossik.farmcraft.Main;
import cz.grossik.farmcraft.recipe.FarmCraftRecipeSerialize;
import cz.grossik.farmcraft.recipe.JuicerRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class FarmCraftRecipeType {
	public final static ResourceLocation JUICER_RES = new ResourceLocation(Main.MOD_ID, "juicer");
	public static IRecipeType<JuicerRecipe> JUICER_RECIPE = null;
	public static FarmCraftRecipeSerialize<JuicerRecipe> JUICER = null;
	   
	public static void init() {
		JUICER_RECIPE = register(JUICER_RES);
		JUICER = register(JUICER_RES, new FarmCraftRecipeSerialize<>(JuicerRecipe::new, 200));
	}
	
	static <T extends IRecipe<?>> IRecipeType<T> register(ResourceLocation resLoc) {
	      return Registry.register(Registry.RECIPE_TYPE, resLoc, new IRecipeType<T>() {
	         public String toString() {
	            return resLoc.getPath();
	         }
	      });
	   }
	   
	   static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(ResourceLocation key, S recipeSerializer) {
		      return (S)(Registry.<IRecipeSerializer<?>>register(Registry.RECIPE_SERIALIZER, key, recipeSerializer));
	   }
}
