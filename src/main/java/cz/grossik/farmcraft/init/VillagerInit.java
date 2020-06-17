package cz.grossik.farmcraft.init;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;

import cz.grossik.farmcraft.Main;
import cz.grossik.farmcraft.block.BoilingBlock;
import cz.grossik.farmcraft.multiblock.BoilingPartIndex;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.structure.PlainsVillagePools;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class VillagerInit {

    private static Constructor<PointOfInterestType> poiConstructor;
    private static Constructor<VillagerProfession> professionConstructor;
	private static Method blockStatesInjector = ObfuscationReflectionHelper.findMethod(PointOfInterestType.class, "func_221052_a", PointOfInterestType.class);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =  new DeferredRegister<>(ForgeRegistries.PROFESSIONS, Main.MOD_ID);
    public static final DeferredRegister<PointOfInterestType> POIS =  new DeferredRegister<>(ForgeRegistries.POI_TYPES, Main.MOD_ID);
	static {
		try {
            poiConstructor = PointOfInterestType.class.getDeclaredConstructor(String.class, Set.class, int.class, int.class);
            poiConstructor.setAccessible(true);
            professionConstructor = VillagerProfession.class.getDeclaredConstructor(String.class, PointOfInterestType.class, ImmutableSet.class, ImmutableSet.class, SoundEvent.class);
            professionConstructor.setAccessible(true);
		} catch(NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
	}
	
    public static final RegistryObject<PointOfInterestType> BREWER_POI = POIS.register("brewer", ()-> pointOfInterestType("brewer", getAllStates(BlockInit.boiling_block.get()), 1, 1));

    public static final RegistryObject<VillagerProfession> BREWER = PROFESSIONS.register("brewer", ()-> villagerProfession("brewer", BREWER_POI.get(), SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER));
	
	@SuppressWarnings("deprecation")
	public static void init() {
		PlainsVillagePools.init();
		JigsawPattern old = JigsawManager.REGISTRY.get(new ResourceLocation("minecraft", "village/plains/houses"));
		List<JigsawPiece> shuffled = old.getShuffledPieces(new Random());
		List<Pair<JigsawPiece, Integer>> newPieces = new ArrayList<>();
		for(JigsawPiece p : shuffled){
			newPieces.add(new Pair<>(p, 1));
		}
		newPieces.add(new Pair<>(new SingleJigsawPiece("farmcraft:village/houses/farm"), 1));
		ResourceLocation something = old.func_214948_a();
		JigsawManager.REGISTRY.register(new JigsawPattern(new ResourceLocation("minecraft", "village/plains/houses"), something, newPieces, PlacementBehaviour.RIGID));
	}
	
    public static PointOfInterestType pointOfInterestType(String resourceLocation, Set<BlockState> blockStates, int maxFreeTickets, int p_i225712_4_) {
        try {
            PointOfInterestType poi = (PointOfInterestType) poiConstructor.newInstance(resourceLocation, blockStates, maxFreeTickets, p_i225712_4_);
            blockStatesInjector.invoke(null, poi);
            return poi;
        }catch (InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }

    public static VillagerProfession villagerProfession(String resourceLocation, PointOfInterestType type, @Nullable SoundEvent soundEvent){
        try {
            VillagerProfession poi = (VillagerProfession) professionConstructor.newInstance(resourceLocation, type, ImmutableSet.of(), ImmutableSet.of(), soundEvent);
            return poi;
        }catch (InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }
    
    private static Set<BlockState> getAllStates(Block block) {
		return ImmutableSet.copyOf(block.getStateContainer().getValidStates().stream().filter(blockState -> {
			if(blockState.has(BoilingBlock.FORMED)) {
				if(blockState.get(BoilingBlock.FORMED) == BoilingPartIndex.P000) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList()));
    }
}
