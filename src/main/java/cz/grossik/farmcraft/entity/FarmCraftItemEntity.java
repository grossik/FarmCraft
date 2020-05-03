package cz.grossik.farmcraft.entity;

import cz.grossik.farmcraft.init.ItemInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FarmCraftItemEntity extends ItemEntity {
		
	public FarmCraftItemEntity(EntityType<? extends ItemEntity> p_i50217_1_, World p_i50217_2_) {
		super(p_i50217_1_, p_i50217_2_);
	}
	
	public FarmCraftItemEntity(EntityType<? extends ItemEntity> p_i50217_1_, World p_i50217_2_, BlockPos pos, ItemStack stack) {
		super(p_i50217_1_, p_i50217_2_);
		this.setPosition(pos.getX(), pos.getY(), pos.getZ());
		this.setItem(stack);
		this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(p_i50217_2_));
		this.rotationYaw = this.rand.nextFloat() * 360.0F;
		this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
	}
	   
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	   
	/*@Override
	public void tick() {
		System.err.println("tick");
		if(this.isInWater()) {
			System.err.println("water");
			ItemStack newItemStack = new ItemStack(ItemInit.soaked_barley.get());
			ItemEntity newItem = new ItemEntity(this.world, this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ(), newItemStack);
			this.world.addEntity(newItem);
			this.remove();
		}
		super.tick();
	}*/
	
	@Override
	public void onCollideWithPlayer(PlayerEntity entityIn) {
		
	}
}
