package cz.grossik.farmcraft.init;

import cz.grossik.farmcraft.Main;
import cz.grossik.farmcraft.block.BeerBlock;
import cz.grossik.farmcraft.block.BoilingBlock;
import cz.grossik.farmcraft.block.JuicerBlock;
import cz.grossik.farmcraft.block.ScarecrowBlock;
import cz.grossik.farmcraft.block.WoodenStickBlock;
import cz.grossik.farmcraft.crop.BlueberryBush;
import cz.grossik.farmcraft.crop.HopsBlock;
import cz.grossik.farmcraft.crop.StrawberryBush;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit {	
	public static class FarmCraftMaterial {
		public static final Material WOODEN_STICK_MATERIAL = (new Material.Builder(MaterialColor.FOLIAGE)).doesNotBlockMovement().notSolid().build();
		//public static final Material BEER = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().liquid().build();
	}
	
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Main.MOD_ID);

	//Crop
    public static RegistryObject<WoodenStickBlock> wooden_stick = BLOCKS.register("wooden_stick", () ->
    	new WoodenStickBlock(Block.Properties.create(FarmCraftMaterial.WOODEN_STICK_MATERIAL).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.5F, 0.5F).sound(SoundType.WOOD))
	);
    
    public static RegistryObject<StrawberryBush> strawberry = BLOCKS.register("strawberry_bush", () ->
    	new StrawberryBush(Block.Properties.create(Material.PLANTS).tickRandomly().doesNotBlockMovement().sound(SoundType.SWEET_BERRY_BUSH))
    );
    
    public static RegistryObject<BlueberryBush> blueberry = BLOCKS.register("blueberry_bush", () ->
		new BlueberryBush(Block.Properties.create(Material.PLANTS).tickRandomly().doesNotBlockMovement().sound(SoundType.SWEET_BERRY_BUSH))
	);
	
	//Other
    public static RegistryObject<ScarecrowBlock> scarecrow = BLOCKS.register("scarecrow", () ->
    	new ScarecrowBlock(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(2F, 10F).sound(SoundType.WOOD))
	);

    public static RegistryObject<JuicerBlock> juicer = BLOCKS.register("juicer", () ->
    	new JuicerBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.5F))
	);
	
    public static RegistryObject<BeerBlock> beer_block = BLOCKS.register("beer", () ->
	    new BeerBlock(() -> FluidInit.beer_fluid.get(), Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops())
	);
    
    public static RegistryObject<BoilingBlock> boiling_block = BLOCKS.register("boiling_block", ()->
		new BoilingBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(3.5F))
	);
    
    //Hops
    public static RegistryObject<HopsBlock> hops = BLOCKS.register("hops", () ->
		new HopsBlock(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.CROP))
	);
}
