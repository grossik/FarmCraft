package cz.grossik.farmcraft.block;

import cz.grossik.farmcraft.init.FluidInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BeerBlock extends FlowingFluidBlock {

	public BeerBlock(java.util.function.Supplier<? extends FlowingFluid> fluidIn, Properties builder) {
		super(fluidIn, builder);
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		/*if(this.getFluid().isIn(FluidInit.Tags.beer)) {
			if(entityIn instanceof LivingEntity) {
				((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.POISON, 160, 2));
			}
			else entityIn.remove();
		}*/
		if(entityIn instanceof PlayerEntity) {
			((PlayerEntity) entityIn).addPotionEffect(new EffectInstance(Effects.REGENERATION, 320, 2));
		} else if(entityIn instanceof VillagerEntity) {
			((VillagerEntity) entityIn).addPotionEffect(new EffectInstance(Effects.REGENERATION, 320, 2));
		} else if(entityIn instanceof LivingEntity) {
			((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.POISON, 320, 2));
		}
		else entityIn.remove();
	}
}