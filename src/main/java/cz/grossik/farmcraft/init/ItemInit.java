package cz.grossik.farmcraft.init;

import cz.grossik.farmcraft.Main;
import cz.grossik.farmcraft.Main.FarmCraftItemGroup;
import cz.grossik.farmcraft.item.ItemBarley;
import cz.grossik.farmcraft.item.ItemStickSeed;
import cz.grossik.farmcraft.item.JuiceItem;
import cz.grossik.farmcraft.item.TestSpawnEggItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Food;
import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.PlantType;
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
/*@ObjectHolder(Main.MOD_ID)*/
public class ItemInit {
	public static class FCFoods { 
		public static final Food JUICE = new Food.Builder().hunger(5).saturation(0.4F).build();
	}
	
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Main.MOD_ID);

	//Other
    public static RegistryObject<BlockNamedItem> wooden_stick = ITEMS.register("wooden_stick", () ->
    	new BlockNamedItem(BlockInit.wooden_stick.get(), (new Item.Properties().group(FarmCraftItemGroup.instance)))
    );
   
    public static RegistryObject<BlockNamedItem> juicer = ITEMS.register("juicer", () ->
		new BlockNamedItem(BlockInit.juicer.get(), (new Item.Properties().group(FarmCraftItemGroup.instance)))
	);
    
    public static RegistryObject<BlockNamedItem> scarecrow = ITEMS.register("scarecrow", () ->
		new BlockNamedItem(BlockInit.scarecrow.get(), (new Item.Properties().group(FarmCraftItemGroup.instance)))
	);
    
    public static RegistryObject<BlockNamedItem> boiling_block = ITEMS.register("boiling_block", () ->
    	new BlockNamedItem(BlockInit.boiling_block.get(), (new Item.Properties().group(FarmCraftItemGroup.instance)))
	);
    
	//Fruit and vegetables
    public static RegistryObject<Item> tomato = ITEMS.register("tomato", () ->
    	new Item(new Item.Properties().group(FarmCraftItemGroup.instance).food(new Food.Builder().hunger(3).saturation(3).build()))
	);
    
    public static RegistryObject<BlockNamedItem> strawberry = ITEMS.register("strawberry", () ->
    	new BlockNamedItem(BlockInit.strawberry.get(), (new Item.Properties().group(FarmCraftItemGroup.instance).food(Foods.SWEET_BERRIES)))
	);
    
    public static RegistryObject<BlockNamedItem> blueberry = ITEMS.register("blueberry", () ->
		new BlockNamedItem(BlockInit.blueberry.get(), (new Item.Properties().group(FarmCraftItemGroup.instance).food(Foods.SWEET_BERRIES)))
    );
	
	//Seed
    public static RegistryObject<ItemStickSeed> seed_tomato = ITEMS.register("seed_tomato", () ->
    	new ItemStickSeed(new Item.Properties().group(FarmCraftItemGroup.instance), 1, tomato.get())
	);	
	
	//Juice
    public static RegistryObject<Item> juice_glass = ITEMS.register("juice_glass", () ->
    	new Item(new Item.Properties().group(FarmCraftItemGroup.instance))
	);	
    public static RegistryObject<JuiceItem> juice_carrot = ITEMS.register("juice_carrot", () ->
    	new JuiceItem(new Item.Properties().group(FarmCraftItemGroup.instance).food(FCFoods.JUICE).containerItem(juice_glass.get()))
	);	

	//Bucket	
    public static RegistryObject<BucketItem> bucket_beer = ITEMS.register("beer_bucket", () ->
	    new BucketItem(FluidInit.beer_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(FarmCraftItemGroup.instance))
	);
    
    public static RegistryObject<Item> example_spawnegg = ITEMS.register("example_spawnegg", () ->
		new TestSpawnEggItem(EntityInit.EXAMPLE_ENTITY, 195769, 10592673, (new Item.Properties()).group(FarmCraftItemGroup.instance))
	);
    
    //Hops
    public static RegistryObject<Item> hops_item = ITEMS.register("hops_item", () ->
		new Item(new Item.Properties().group(FarmCraftItemGroup.instance))
	);
    
    public static RegistryObject<BlockNamedItem> hops_seed = ITEMS.register("hops_seed", () ->
		new BlockNamedItem(BlockInit.hops.get(), (new Item.Properties()).group(FarmCraftItemGroup.instance))
	);
    
    public static RegistryObject<ItemBarley> barley = ITEMS.register("barley", () ->
		new ItemBarley(new Item.Properties().group(FarmCraftItemGroup.instance))
	);
    
    public static RegistryObject<Item> soaked_barley = ITEMS.register("soaked_barley", () ->
    	new Item(new Item.Properties().group(FarmCraftItemGroup.instance))
	);
}
