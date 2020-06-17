package cz.grossik.farmcraft.item;

import java.util.Random;

import cz.grossik.farmcraft.entity.FarmCraftItemEntity;
import cz.grossik.farmcraft.init.EntityInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
        FarmCraftItemEntity itementity = new FarmCraftItemEntity(EntityInit.ITEM_ENTITY.get(), world, location.getPosX(), location.getPosY(), location.getPosZ(), itemstack);
        itementity.setPickupDelay(40);
        itementity.setMotion(location.getMotion());
        return itementity;
	}
}
