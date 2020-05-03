package cz.grossik.farmcraft;

import com.google.common.collect.ImmutableSet;

import cz.grossik.farmcraft.crop.BlueberryBush;
import cz.grossik.farmcraft.crop.StrawberryBush;
import cz.grossik.farmcraft.init.BlockInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

public class FarmCraftGenerate {

	public static void generate() {
		for(Biome biome : ForgeRegistries.BIOMES) {
			if(biome == Biomes.FOREST || biome == Biomes.PLAINS) {
				BlockState strawberry = BlockInit.strawberry.get().getDefaultState().with(StrawberryBush.AGE, Integer.valueOf(3));
				BlockState blueberry = BlockInit.blueberry.get().getDefaultState().with(BlueberryBush.AGE, Integer.valueOf(3));
				BlockState field_226750_aK_ = Blocks.GRASS_BLOCK.getDefaultState();
				
				BlockClusterFeatureConfig strawberryConfig = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(strawberry), new SimpleBlockPlacer())).tries(32).func_227316_a_(ImmutableSet.of(field_226750_aK_.getBlock())).func_227317_b_().build();
				biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(strawberryConfig).withPlacement(Placement.COUNT_HEIGHTMAP_DOUBLE.configure(new FrequencyConfig(1))));		

				BlockClusterFeatureConfig blueberryConfig = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(blueberry), new SimpleBlockPlacer())).tries(32).func_227316_a_(ImmutableSet.of(field_226750_aK_.getBlock())).func_227317_b_().build();
				biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(blueberryConfig).withPlacement(Placement.COUNT_HEIGHTMAP_DOUBLE.configure(new FrequencyConfig(1))));	
			}
		}
	}
}
