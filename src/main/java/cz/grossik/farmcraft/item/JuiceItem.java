package cz.grossik.farmcraft.item;

import cz.grossik.farmcraft.init.EntityInit;
import cz.grossik.farmcraft.init.ItemInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.world.World;

public class JuiceItem extends Item {
	
	public JuiceItem(Properties properties) {
		super(properties);
	}

	
	   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		      super.onItemUseFinish(stack, worldIn, entityLiving);
		      if (entityLiving instanceof ServerPlayerEntity) {
		         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entityLiving;
		         CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, stack);
		         serverplayerentity.addStat(Stats.ITEM_USED.get(this));
		      }

		      if (stack.isEmpty()) {
		         return new ItemStack(ItemInit.juice_glass.get());
		      } else {
		         if (entityLiving instanceof PlayerEntity && !((PlayerEntity)entityLiving).abilities.isCreativeMode) {
		            ItemStack itemstack = new ItemStack(ItemInit.juice_glass.get());
		            PlayerEntity playerentity = (PlayerEntity)entityLiving;
		            if (!playerentity.inventory.addItemStackToInventory(itemstack)) {
		               playerentity.dropItem(itemstack, false);
		            }
		         }

		         return stack;
		      }
		   }
	   
   public UseAction getUseAction(ItemStack stack) {
	   return UseAction.DRINK;
   }
   
   /*@Override
   public int getEntityLifespan(ItemStack itemStack, World world){
       return 250;
   }*/
}
