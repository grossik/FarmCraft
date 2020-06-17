package cz.grossik.farmcraft.entity;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import cz.grossik.farmcraft.init.ItemInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FarmCraftItemEntity extends ItemEntity {
	
	private int pickupDelayNew = 40;
	
	public FarmCraftItemEntity(EntityType<? extends ItemEntity> p_i50217_1_, World p_i50217_2_) {
		super(p_i50217_1_, p_i50217_2_);
	}
	
	public FarmCraftItemEntity(EntityType<? extends ItemEntity> p_i50217_1_, World p_i50217_2_, double posX, double posY, double posZ, ItemStack stack) {
		super(p_i50217_1_, p_i50217_2_);
		this.setPosition(posX, posY, posZ);
		this.setItem(stack);
		this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(p_i50217_2_));
	}
	   
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void tick() {
		if(this.pickupDelayNew > 0 && this.pickupDelayNew != 32767) {
			--this.pickupDelayNew;
		}
        
		if(this.isInWater() && this.pickupDelayNew == 0) {
			Random random = this.rand;
			BlockPos pos = getPosition();
			world.addOptionalParticle(ParticleTypes.DRIPPING_WATER, true, (double)pos.getX() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.0D, 0.0D);			
			
			if(random.nextInt(150) == 0) {
				ItemStack newItemStack = new ItemStack(ItemInit.soaked_barley.get(), this.getItem().getCount());
				ItemEntity newItem = new ItemEntity(this.world, this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ(), newItemStack);
				this.world.addEntity(newItem);
				this.remove();
			}
		}
		super.tick();
	}
}
