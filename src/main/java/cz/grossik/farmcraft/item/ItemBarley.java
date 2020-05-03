package cz.grossik.farmcraft.item;

import java.util.Random;

import cz.grossik.farmcraft.entity.FarmCraftItemEntity;
import cz.grossik.farmcraft.init.EntityInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
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
			Random rand = new Random();
	      if (itemstack.isEmpty()) {
	          return null;
	       } else {
	          double d0 = location.getPosYEye() - (double)0.3F;
	          FarmCraftItemEntity itementity = new FarmCraftItemEntity(EntityInit.ITEM_ENTITY.get(), world, location.getPosX(), d0, location.getPosZ(), itemstack);
	          itementity.setPickupDelay(40);
   
	            float f7 = 0.3F;
	            float f8 = MathHelper.sin(location.rotationPitch * ((float)Math.PI / 180F));
	            float f2 = MathHelper.cos(location.rotationPitch * ((float)Math.PI / 180F));
	            float f3 = MathHelper.sin(location.rotationYaw * ((float)Math.PI / 180F));
	            float f4 = MathHelper.cos(location.rotationYaw * ((float)Math.PI / 180F));
	            float f5 = rand.nextFloat() * ((float)Math.PI * 2F);
	            float f6 = 0.02F * rand.nextFloat();
	            itementity.setMotion((double)(-f3 * f2 * 0.3F) + Math.cos((double)f5) * (double)f6, (double)(-f8 * 0.3F + 0.1F + (rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(f4 * f2 * 0.3F) + Math.sin((double)f5) * (double)f6);
	          
	          return itementity;
	       }	      
	}
}
