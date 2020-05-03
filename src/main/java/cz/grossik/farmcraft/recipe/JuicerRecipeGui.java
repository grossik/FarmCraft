package cz.grossik.farmcraft.recipe;

import java.util.Set;

import cz.grossik.farmcraft.tileentity.JuicerTileEntity;
import net.minecraft.client.gui.recipebook.AbstractRecipeBookGui;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JuicerRecipeGui extends AbstractRecipeBookGui {
	   protected boolean func_212962_b() {
		      return this.recipeBook.isFurnaceFilteringCraftable();
		   }

		   protected void func_212959_a(boolean p_212959_1_) {
		      this.recipeBook.setFurnaceFilteringCraftable(p_212959_1_);
		   }

		   protected boolean func_212963_d() {
		      return this.recipeBook.isFurnaceGuiOpen();
		   }

		   protected void func_212957_c(boolean p_212957_1_) {
		      this.recipeBook.setFurnaceGuiOpen(p_212957_1_);
		   }

		   protected String func_212960_g() {
		      return "gui.recipebook.toggleRecipes.smeltable";
		   }

		   protected Set<Item> func_212958_h() {
		      return JuicerTileEntity.getBurnTimes().keySet();
		   }
		}