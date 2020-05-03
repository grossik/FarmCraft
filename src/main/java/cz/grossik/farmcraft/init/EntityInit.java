package cz.grossik.farmcraft.init;

import cz.grossik.farmcraft.Main;
import cz.grossik.farmcraft.entity.ExampleEntity;
import cz.grossik.farmcraft.entity.FarmCraftItemEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityInit {
	
    public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, Main.MOD_ID);

	public static RegistryObject<EntityType<ExampleEntity>> EXAMPLE_ENTITY = ENTITIES
			.register("example_entity",
					() -> EntityType.Builder.<ExampleEntity>create(ExampleEntity::new, EntityClassification.CREATURE)
							.size(0.5F, 0.9F)
							.build(new ResourceLocation("example_entity").toString()));	
	
	public static RegistryObject<EntityType<FarmCraftItemEntity>> ITEM_ENTITY = ENTITIES
			.register("item_farmcraft", () -> EntityType.Builder.<FarmCraftItemEntity>create(FarmCraftItemEntity::new, EntityClassification.MISC)
					.size(0.25F, 0.25F)
					.build(new ResourceLocation("item_farmcraft").toString()));
}
