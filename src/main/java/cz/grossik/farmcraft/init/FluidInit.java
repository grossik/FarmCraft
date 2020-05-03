package cz.grossik.farmcraft.init;

import java.util.function.Supplier;

import cz.grossik.farmcraft.Main;
import cz.grossik.farmcraft.fluid.BeerFluid;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

//@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Bus.MOD)
public class FluidInit {
	
    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, Main.MOD_ID);

    public static RegistryObject<FlowingFluid> beer_fluid = FLUIDS.register("beer", () ->
		new BeerFluid.Source()
	);

	public static RegistryObject<FlowingFluid> beer_fluid_flowing = FLUIDS.register("beer_flowing", () ->
	    new BeerFluid.Flowing()
	);
	
	public static class Tags {
		public static final Tag<Fluid> beer = new FluidTags.Wrapper(new ResourceLocation(Main.MOD_ID, "beer_still"));
	}
}