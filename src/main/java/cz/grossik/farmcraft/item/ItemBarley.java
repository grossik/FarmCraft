package cz.grossik.farmcraft.item;

import cz.grossik.farmcraft.entity.FarmCraftItemEntity;
import cz.grossik.farmcraft.init.EntityInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBarley extends Item {

	public ItemBarley(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasCustomEntity(ItemStack itemstack) {
		return true;
	}
	
	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new FarmCraftItemEntity(EntityInit.ITEM_ENTITY.get(), world);
	}
}
