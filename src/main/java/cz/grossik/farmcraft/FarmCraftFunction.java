package cz.grossik.farmcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import cz.grossik.farmcraft.init.ItemInit;
import cz.grossik.farmcraft.item.ItemStickSeed;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class FarmCraftFunction {

	public static ItemStickSeed getWoodenStickItem(int type) {
		switch(type) {
			case 1:
				return (ItemStickSeed) ItemInit.seed_tomato.get();
			default:
				return null;
		}
	}
	
	public static Set<Class<?>> getAllExtendedOrImplementedTypesRecursively(Class<?> clazz) {
	    List<Class<?>> res = new ArrayList<>();

	    do {
	        res.add(clazz);

	        Class<?>[] interfaces = clazz.getInterfaces();
	        if (interfaces.length > 0) {
	            res.addAll(Arrays.asList(interfaces));

	            for (Class<?> interfaze : interfaces) {
	                res.addAll(getAllExtendedOrImplementedTypesRecursively(interfaze));
	                if(interfaze == CropsBlock.class) {
	                	break;
	                }
	            }
	        }

	        Class<?> superClass = clazz.getSuperclass();

	        if (superClass == null) {
	            break;
	        }

	        clazz = superClass;
	    } while (!"java.lang.Object".equals(clazz.getCanonicalName()));

	    return new HashSet<Class<?>>(res);
	}
	
	public static boolean isThisBlockCrops(Class<?> clazz) {
		Set<Class<?>> classes = getAllExtendedOrImplementedTypesRecursively(clazz);
		for (Class<?> clazz1 : classes) {
		    if(clazz1 == CropsBlock.class) {
		    	return true;
		    }
		}
		return false;
	}	
}