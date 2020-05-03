package cz.grossik.farmcraft;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.structure.PlainsVillagePools;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.datafixers.util.Pair;

import cz.grossik.farmcraft.container.FarmCraftContainerType;
import cz.grossik.farmcraft.init.BlockInit;
import cz.grossik.farmcraft.init.EntityInit;
import cz.grossik.farmcraft.init.FluidInit;
import cz.grossik.farmcraft.init.ItemInit;
import cz.grossik.farmcraft.init.PotionInit;
import cz.grossik.farmcraft.init.VillagerInit;
import cz.grossik.farmcraft.recipe.FarmCraftRecipeType;
import cz.grossik.farmcraft.renderer.ExampleEntityRender;
import cz.grossik.farmcraft.renderer.FarmCraftItemEntityRenderer;
import cz.grossik.farmcraft.renderer.TileEntityJuicerRenderer;
import cz.grossik.farmcraft.tileentity.FarmCraftTileEntityTypes;

@Mod("farmcraft")
public class Main {
    //private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "farmcraft";
    public static Main instance;
    
    public Main() {
    	final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    	modEventBus.addListener(this::setup);
    	modEventBus.addListener(this::doClientStuff);
    	modEventBus.addListener(this::loadCompleteEvent);
    	modEventBus.addListener(this::onSetVillagerTrades);
    	modEventBus.addListener(this::villagerDrunkEvent);
    	//modEventBus.addListener(this::entityJoin);
    	
        BlockInit.BLOCKS.register(modEventBus);
        ItemInit.ITEMS.register(modEventBus);
    	FluidInit.FLUIDS.register(modEventBus);
    	EntityInit.ENTITIES.register(modEventBus);
    	FarmCraftContainerType.CONTAINERS.register(modEventBus);
    	VillagerInit.POIS.register(modEventBus);
    	VillagerInit.PROFESSIONS.register(modEventBus);
    	PotionInit.POTION.register(modEventBus);
    	PotionInit.POTION_TYPES.register(modEventBus);

    	FarmCraftTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
    	FarmCraftRecipeType.init();
    	
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);                
    }

    @SubscribeEvent
	public void setup(FMLCommonSetupEvent event){   
    	VillagerInit.init();
    }
    
    @SubscribeEvent
    public void onSetVillagerTrades(VillagerTradesEvent event) {
    	if(event.getType() == VillagerInit.BREWER.get()) {
            ArrayList<VillagerTrades.ITrade> trades = new ArrayList<>();
            trades.add(new BasicTrade(new ItemStack(Items.EMERALD, 3), ItemStack.EMPTY, new ItemStack(ItemInit.bucket_beer.get(), 1), 12, 10, 0.05F));
            event.getTrades().put(1, trades);
    	}
    }

    @SubscribeEvent
    public void villagerDrunkEvent(EntityInteractSpecific event) {
    	if(event.getTarget() instanceof VillagerEntity) {
    		VillagerEntity villager = (VillagerEntity) event.getTarget();
    		if(villager.isPotionActive(PotionInit.drunk_effect.get())) {
				for(MerchantOffer merchantoffer1 : villager.getOffers()) {
					double d0 = 0.3D + 0.0625D * (double)1;
					int j = (int)Math.floor(d0 * (double)merchantoffer1.getBuyingStackFirst().getCount());
					merchantoffer1.increaseSpecialPrice(-Math.max(j, 1));
				}	
    		}
    	}
    }
    
    /*@SubscribeEvent
    public void entityJoin(EntityJoinWorldEvent event) {
    	if(event.getEntity() instanceof ItemEntity) {
    		System.err.println("Called dsadsa dsa");
    		ItemEntity itemEntity = (ItemEntity) event.getEntity();
    		ItemStack itemStack = itemEntity.getItem();
    		if(itemStack.getItem() == ItemInit.hops_item.get()) {
    			BlockPos pos = new BlockPos(itemEntity);
    			if(itemEntity.getEntityWorld().getBlockState(pos).getMaterial() == Material.WATER) {
    				ItemStack newItemStack = new ItemStack(Items.DIAMOND);
    				ItemEntity newItem = new ItemEntity(itemEntity.getEntityWorld(), itemEntity.getPosX(), itemEntity.getPosY(), itemEntity.getPosZ(), newItemStack);
    				itemEntity.getEntityWorld().addEntity(newItem);
    			}
    		}
    	}
    }*/

    @SubscribeEvent
    public void doClientStuff(FMLClientSetupEvent event) {
    	ClientRegistry.bindTileEntityRenderer(FarmCraftTileEntityTypes.juicer.get(), TileEntityJuicerRenderer::new);
    	
    	//Crop render
    	RenderTypeLookup.setRenderLayer(BlockInit.wooden_stick.get(), RenderType.getCutout());
    	RenderTypeLookup.setRenderLayer(BlockInit.strawberry.get(), RenderType.getCutout());
    	RenderTypeLookup.setRenderLayer(BlockInit.blueberry.get(), RenderType.getCutout());
    	RenderTypeLookup.setRenderLayer(BlockInit.hops.get(), RenderType.getCutout());
    	
    	RenderTypeLookup.setRenderLayer(BlockInit.scarecrow.get(), RenderType.getCutout());
    	
    	RenderTypeLookup.setRenderLayer(FluidInit.beer_fluid.get(), RenderType.getTranslucent());
    	RenderTypeLookup.setRenderLayer(FluidInit.beer_fluid_flowing.get(), RenderType.getTranslucent());
    	RenderTypeLookup.setRenderLayer(BlockInit.beer_block.get(), RenderType.getTranslucent());
    	
    	FarmCraftContainerType.registerScreens(event);
    	
    	RenderingRegistry.registerEntityRenderingHandler(EntityInit.EXAMPLE_ENTITY.get(), ExampleEntityRender::new);
    	RenderingRegistry.registerEntityRenderingHandler(EntityInit.ITEM_ENTITY.get(), FarmCraftItemEntityRenderer::new);
    }
   
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {

    }
    
	@SubscribeEvent
	public void loadCompleteEvent(FMLLoadCompleteEvent event) {
		FarmCraftGenerate.generate();
	}
	
    public static class FarmCraftItemGroup extends ItemGroup {
    	public static final FarmCraftItemGroup instance = new FarmCraftItemGroup(ItemGroup.GROUPS.length, "farmcrafttab");
    	
		public FarmCraftItemGroup(int index, String label) {
			super(index, label);
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(ItemInit.strawberry.get());
		}
    	
    }
}
