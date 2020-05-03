package cz.grossik.farmcraft.effect;

import cz.grossik.farmcraft.init.PotionInit;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class DrunkEffect extends Effect {

	public DrunkEffect(EffectType typeIn, int liquidColorIn) {
		super(typeIn, liquidColorIn);
	}
	
	/*@Override
	public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
		if(this == PotionInit.drunk_effect.get()) {
			System.err.println("Má potion");
			if(entityLivingBaseIn instanceof VillagerEntity) {
				VillagerEntity villager = (VillagerEntity) entityLivingBaseIn;
				for(MerchantOffer merchantoffer1 : villager.getOffers()) {
					double d0 = 0.3D + 0.0625D * (double)1;
					int j = (int)Math.floor(d0 * (double)merchantoffer1.getBuyingStackFirst().getCount());
					merchantoffer1.increaseSpecialPrice(-Math.max(j, 1));
				}
			}
		}
	}*/
}