package cz.grossik.farmcraft.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class FarmCraftRecipeSerialize<T extends AbstractJuicerRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	   private final int field_222178_t;
	   private final FarmCraftRecipeSerialize.IFactory<T> field_222179_u;

	   public FarmCraftRecipeSerialize(FarmCraftRecipeSerialize.IFactory<T> p_i50025_1_, int p_i50025_2_) {
	      this.field_222178_t = p_i50025_2_;
	      this.field_222179_u = p_i50025_1_;
	   }

	   public T read(ResourceLocation recipeId, JsonObject json) {
	      String s = JSONUtils.getString(json, "group", "");
	      JsonElement jsonelement = (JsonElement)(JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
	      Ingredient ingredient = Ingredient.deserialize(jsonelement);
	      if (!json.has("result")) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
	      ItemStack itemstack;
	      if (json.get("result").isJsonObject()) itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
	      else {
	      String s1 = JSONUtils.getString(json, "result");
	      ResourceLocation resourcelocation = new ResourceLocation(s1);
	      itemstack = new ItemStack(Registry.ITEM.getValue(resourcelocation).orElseThrow(() -> {
	         return new IllegalStateException("Item: " + s1 + " does not exist");
	      }));
	      }
	      float f = JSONUtils.getFloat(json, "experience", 0.0F);
	      int i = JSONUtils.getInt(json, "cookingtime", this.field_222178_t);
	      return this.field_222179_u.create(recipeId, s, ingredient, itemstack, f, i);
	   }

	   public T read(ResourceLocation recipeId, PacketBuffer buffer) {
	      String s = buffer.readString(32767);
	      Ingredient ingredient = Ingredient.read(buffer);
	      ItemStack itemstack = buffer.readItemStack();
	      float f = buffer.readFloat();
	      int i = buffer.readVarInt();
	      return this.field_222179_u.create(recipeId, s, ingredient, itemstack, f, i);
	   }

	   public void write(PacketBuffer buffer, T recipe) {
	      buffer.writeString(recipe.group);
	      recipe.ingredient.write(buffer);
	      buffer.writeItemStack(recipe.result);
	      buffer.writeFloat(recipe.experience);
	      buffer.writeVarInt(recipe.cookTime);
	   }

	   interface IFactory<T extends AbstractJuicerRecipe> {
	      T create(ResourceLocation p_create_1_, String p_create_2_, Ingredient p_create_3_, ItemStack p_create_4_, float p_create_5_, int p_create_6_);
	   }
	}