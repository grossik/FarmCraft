package cz.grossik.farmcraft.init;

import cz.grossik.farmcraft.Main;
import cz.grossik.farmcraft.Main.FarmCraftItemGroup;
import cz.grossik.farmcraft.effect.DrunkEffect;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionInit {

	public static final DeferredRegister<Effect> POTION = new DeferredRegister<>(ForgeRegistries.POTIONS, Main.MOD_ID);
	
	public static final DeferredRegister<Potion> POTION_TYPES = new DeferredRegister<>(ForgeRegistries.POTION_TYPES, Main.MOD_ID);

	public static RegistryObject<Effect> drunk_effect = POTION.register("drunk", () ->
		new DrunkEffect(EffectType.NEUTRAL, 0xD4F0AD)
	);
	
    public static RegistryObject<Potion> drunk = POTION_TYPES.register("drunk", () ->
		new Potion(new EffectInstance(drunk_effect.get(), 3600))
	);
}
