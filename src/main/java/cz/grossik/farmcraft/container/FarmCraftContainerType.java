package cz.grossik.farmcraft.container;

import cz.grossik.farmcraft.Main;
import cz.grossik.farmcraft.entity.ExampleEntity;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class FarmCraftContainerType {    
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, Main.MOD_ID);

	/*public final static ResourceLocation JUICER_RES = new ResourceLocation(Main.MOD_ID, "juicer");
	public final static ResourceLocation BOILING_RES = new ResourceLocation(Main.MOD_ID, "boiling");
	public static ContainerType<JuicerContainer> JUICER = null;
	public static ContainerType<BoilingContainer> BOILING = null;*/
	
	public static final RegistryObject<ContainerType<JuicerContainer>> JUICER = CONTAINERS.register("juicer", () -> IForgeContainerType.create((windowId, inv, data) -> {
		return new JuicerContainer(windowId, Minecraft.getInstance().player.inventory);
	}));
	
	public static final RegistryObject<ContainerType<BoilingContainer>> BOILING = CONTAINERS.register("boiling", () -> IForgeContainerType.create((windowId, inv, data) -> {
		return new BoilingContainer(windowId, Minecraft.getInstance().player.inventory);
	}));
	
	/*public static void init() {
		JUICER = register(JUICER_RES, JuicerContainer::new);
		BOILING = register(BOILING_RES, BoilingContainer::new);
	}*/
	
	@OnlyIn(Dist.CLIENT)
    public static void registerScreens(FMLClientSetupEvent event) {
		ScreenManager.registerFactory(JUICER.get(), JuicerScreen::new);
		ScreenManager.registerFactory(BOILING.get(), BoilingScreen::new);
	}
	
    private static <C extends Container> ContainerType<C> register(ResourceLocation name, ContainerType.IFactory<C> containerFactory) {
        ContainerType<C> type = new ContainerType<>(containerFactory);
        return register(name, type);
    }

    private static <C extends Container> ContainerType<C> register(ResourceLocation name, ContainerType<C> containerType) {
        containerType.setRegistryName(name.getNamespace());
        ForgeRegistries.CONTAINERS.register(containerType);
        return containerType;
    }
    
    /*private static <T extends Container> ContainerType<T> register(ResourceLocation key, ContainerType.IFactory<T> factory) {
    	return Registry.register(Registry.MENU, key, new ContainerType<>(factory));
	}*/
}
