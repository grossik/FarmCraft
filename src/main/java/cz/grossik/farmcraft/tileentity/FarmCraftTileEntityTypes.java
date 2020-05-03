package cz.grossik.farmcraft.tileentity;

import cz.grossik.farmcraft.Main;
import cz.grossik.farmcraft.init.BlockInit;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FarmCraftTileEntityTypes {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, Main.MOD_ID);

	public static final RegistryObject<TileEntityType<JuicerTileEntity>> juicer = TILE_ENTITY_TYPES.register("juicer", () -> TileEntityType.Builder.create(JuicerTileEntity::new, BlockInit.juicer.get()).build(null));

	public static final RegistryObject<TileEntityType<TileEntityWoodenStick>> wooden_stick = TILE_ENTITY_TYPES.register("wooden_stick", () -> TileEntityType.Builder.create(TileEntityWoodenStick::new, BlockInit.wooden_stick.get()).build(null));

	public static final RegistryObject<TileEntityType<TileEntityBoiling>> boiling = TILE_ENTITY_TYPES.register("boiling", () -> TileEntityType.Builder.create(TileEntityBoiling::new, BlockInit.boiling_block.get()).build(null));
}
