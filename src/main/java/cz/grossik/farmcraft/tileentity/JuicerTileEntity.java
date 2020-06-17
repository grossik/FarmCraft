package cz.grossik.farmcraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cz.grossik.farmcraft.recipe.FarmCraftRecipeType;
import cz.grossik.farmcraft.block.JuicerBlock;
import cz.grossik.farmcraft.container.JuicerContainer;
import cz.grossik.farmcraft.handler.FarmCraftItemsHandler;
import cz.grossik.farmcraft.init.ItemInit;
import cz.grossik.farmcraft.recipe.AbstractJuicerRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

//ISidedInventory, 
public class JuicerTileEntity extends LockableTileEntity implements IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity {
	   private static final int[] SLOTS_UP = new int[]{0};
	   private static final int[] SLOTS_DOWN = new int[]{2};
	   private static final int[] SLOTS_HORIZONTAL = new int[]{1};
		private ItemStackHandler items;
		private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);
	   private int burnTime;
	   private int recipesUsed;
	   private int cookTime;
	   private int cookTimeTotal;
	   protected final IIntArray furnaceData = new IIntArray() {
	      public int get(int index) {
	         switch(index) {
	         case 0:
	            return JuicerTileEntity.this.burnTime;
	         case 1:
	            return JuicerTileEntity.this.recipesUsed;
	         case 2:
	            return JuicerTileEntity.this.cookTime;
	         case 3:
	            return JuicerTileEntity.this.cookTimeTotal;
	         default:
	            return 0;
	         }
	      }

	      public void set(int index, int value) {
	         switch(index) {
	         case 0:
	        	 JuicerTileEntity.this.burnTime = value;
	            break;
	         case 1:
	        	 JuicerTileEntity.this.recipesUsed = value;
	            break;
	         case 2:
	        	 JuicerTileEntity.this.cookTime = value;
	            break;
	         case 3:
	        	 JuicerTileEntity.this.cookTimeTotal = value;
	         }

	      }

	      public int size() {
	         return 4;
	      }
	   };
	   private final Map<ResourceLocation, Integer> field_214022_n = Maps.newHashMap();
	   protected final IRecipeType<?> recipeType;

	   public JuicerTileEntity() {
		   super(FarmCraftTileEntityTypes.juicer.get());
		   items = new FarmCraftItemsHandler(3);
		   this.recipeType = FarmCraftRecipeType.JUICER_RECIPE;
	   }

	   protected ITextComponent getDefaultName() {
	      return new TranslationTextComponent("farmcraft.container.juicer");
	   }

	   protected Container createMenu(int id, PlayerInventory player) {
	      return new JuicerContainer(id, player, this, this.furnaceData);
	   }
		   
	   @Deprecated //Forge - get burn times by calling ForgeHooks#getBurnTime(ItemStack)
	   public static Map<Item, Integer> getBurnTimes() {
	      Map<Item, Integer> map = Maps.newLinkedHashMap();
	      addItemBurnTime(map, ItemInit.juice_glass.get(), 200);
	      return map;
	   }

	   private static void addItemBurnTime(Map<Item, Integer> map, IItemProvider itemProvider, int burnTimeIn) {
	      map.put(itemProvider.asItem(), burnTimeIn);
	   }

	   public boolean isBurning() {
	      return this.burnTime > 0;
	   }

	   public void read(CompoundNBT compound) {
	      super.read(compound);
	      
	      //this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
	      items.deserializeNBT(compound.getCompound("juicer:slots"));
	      //ItemStackHelper.loadAllItems(compound, this.items);
	      this.burnTime = compound.getInt("juicer:burnTime");
	      this.cookTime = compound.getInt("juicer:cookTime");
	      this.cookTimeTotal = compound.getInt("juicer:cookTimeTotal");
	      this.recipesUsed = this.getBurnTime(items.getStackInSlot(1));
	      int i = compound.getShort("RecipesUsedSize");

	      for(int j = 0; j < i; ++j) {
	         ResourceLocation resourcelocation = new ResourceLocation(compound.getString("RecipeLocation" + j));
	         int k = compound.getInt("RecipeAmount" + j);
	         this.field_214022_n.put(resourcelocation, k);
	      }

	   }

	   public CompoundNBT write(CompoundNBT compound) {
	      super.write(compound);
	      compound.putInt("juicer:burnTime", this.burnTime);
	      compound.putInt("juicer:cookTime", this.cookTime);
	      compound.putInt("juicer:cookTimeTotal", this.cookTimeTotal);
	      //ItemStackHelper.saveAllItems(compound, this.items);
	      compound.put("juicer:slots", items.serializeNBT());
	      compound.putShort("RecipesUsedSize", (short)this.field_214022_n.size());
	      int i = 0;

	      for(Entry<ResourceLocation, Integer> entry : this.field_214022_n.entrySet()) {
	         compound.putString("RecipeLocation" + i, entry.getKey().toString());
	         compound.putInt("RecipeAmount" + i, entry.getValue());
	         ++i;
	      }

	      return compound;
	   }

	   public void tick() {
	      boolean flag = this.isBurning();
	      boolean flag1 = false;
	      if (this.isBurning()) {
	         --this.burnTime;
	      }

	      if (!this.world.isRemote) {
	         ItemStack itemstack = this.items.getStackInSlot(1);
	         if (this.isBurning() || !itemstack.isEmpty() && !this.items.getStackInSlot(0).isEmpty()) {
	            IRecipe<?> irecipe = this.world.getRecipeManager().getRecipe((IRecipeType<AbstractJuicerRecipe>)this.recipeType, this, this.world).orElse(null);
	            if (!this.isBurning() && this.canSmelt(irecipe)) {
	               this.burnTime = this.getBurnTime(itemstack);
	               this.recipesUsed = this.burnTime;
	               if (this.isBurning()) {
	                  flag1 = true;
	                  if (itemstack.hasContainerItem())
	                      this.items.insertItem(1, itemstack.getContainerItem(), false);
	                  else
	                  if (!itemstack.isEmpty()) {
	                     //Item item = itemstack.getItem();
	                     //itemstack.shrink(1);
	                     if (itemstack.isEmpty()) {
	                        this.items.insertItem(1, itemstack.getContainerItem(), false);
	                     }
	                  }
	               }
	            }

	            if (this.isBurning() && this.canSmelt(irecipe)) {
	               ++this.cookTime;
	               if (this.cookTime == this.cookTimeTotal) {
	                  this.cookTime = 0;
	                  this.burnTime = 0;
	                  
	                  this.cookTimeTotal = this.func_214005_h();
	                  this.func_214007_c(irecipe);
	                  flag1 = true;
	               }
	            } else {
	               this.cookTime = 0;
	               this.burnTime = 0;
	            }
	         } else if (!this.isBurning() && this.cookTime > 0) {
	            this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
	         }

	         if (flag != this.isBurning()) {
	            flag1 = true;
	            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(JuicerBlock.VYPALUJE, Boolean.valueOf(this.isBurning())), 3);
	         }
	         
        	 BlockState blockstate = this.world.getBlockState(this.getPos());
             if (!(blockstate.getBlock() instanceof JuicerBlock)) {
                 return;
              }
             
	         if(itemstack.getItem() == ItemInit.juice_glass.get()) {
	        	 blockstate = blockstate.with(JuicerBlock.HAS_BOTTLE, true);
	         } else if(this.items.getStackInSlot(2).isEmpty() == false) {
	        	 blockstate = blockstate.with(JuicerBlock.HAS_BOTTLE, true);
	         } else {
	        	 blockstate = blockstate.with(JuicerBlock.HAS_BOTTLE, false);
	         }
	         
	         this.world.setBlockState(this.pos, blockstate, 3);
	      }

	      if (flag1) {
	    	  this.markDirty();
	      }
	   }

	   protected boolean canSmelt(@Nullable IRecipe<?> recipeIn) {
	      if (!this.items.getStackInSlot(0).isEmpty() && !this.items.getStackInSlot(1).isEmpty() && recipeIn != null) {
	         ItemStack itemstack = recipeIn.getRecipeOutput();
	         if (itemstack.isEmpty()) {
	            return false;
	         } else {
	            ItemStack itemstack1 = this.items.getStackInSlot(2);
	            if (itemstack1.isEmpty()) {
	               return true;
	            } else if (!itemstack1.isItemEqual(itemstack)) {
	               return false;
	            } else if (itemstack1.getCount() + itemstack.getCount() <= this.getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
	               return true;
	            } else {
	               return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
	            }
	         }
	      } else {
	         return false;
	      }
	   }

	   private void func_214007_c(@Nullable IRecipe<?> p_214007_1_) {
	      if (p_214007_1_ != null && this.canSmelt(p_214007_1_)) {
	         ItemStack itemstack = this.items.getStackInSlot(0);
	         ItemStack itemstack1 = p_214007_1_.getRecipeOutput();
	         ItemStack itemstack2 = this.items.getStackInSlot(2);
	         ItemStack itemstack3 = this.items.getStackInSlot(1);
	         if (itemstack2.isEmpty()) {
	            this.items.insertItem(2, itemstack1.copy(), false);
	         } else if (itemstack2.getItem() == itemstack1.getItem()) {
	            itemstack2.grow(itemstack1.getCount());
	         }

	         if (!this.world.isRemote) {
	            this.setRecipeUsed(p_214007_1_);
	         }

	         itemstack.shrink(1);
	         itemstack3.shrink(1);
	      }
	   }
	   
	   protected int getBurnTime(ItemStack p_213997_1_) {
		   Item item = p_213997_1_.getItem();
		   if (p_213997_1_.isEmpty()) {
			   return 0;
		   } else {
			   if(getBurnTimes().containsKey(item)) {
				   return getBurnTimes().get(item);
			   } else {
				   return 0;
			   }
		   }
	   }

	   protected int func_214005_h() {
	      return this.world.getRecipeManager().getRecipe((IRecipeType<AbstractJuicerRecipe>)this.recipeType, this, this.world).map(AbstractJuicerRecipe::getCookTime).orElse(200);
	   }

	   public int[] getSlotsForFace(Direction side) {
	      if (side == Direction.DOWN) {
	         return SLOTS_DOWN;
	      } else {
	         return side == Direction.UP ? SLOTS_UP : SLOTS_HORIZONTAL;
	      }
	   }

	   public int getSizeInventory() {
	      return 3;
	   }

	   public boolean isEmpty() {
		   for(int i = 0; i < items.getSlots(); i++) {
			   ItemStack itemstack = this.items.getStackInSlot(i);
		         if (!itemstack.isEmpty()) {
			            return false;
		         }
		   }

	      return true;
	   }

	   public ItemStack decrStackSize(int index, int count) {
		   return index >= 0 && index < items.getSlots() && !items.getStackInSlot(index).isEmpty() && count > 0 ? items.getStackInSlot(index).split(count) : ItemStack.EMPTY;
	   }

	   public ItemStack removeStackFromSlot(int index) {
		   return index >= 0 && index < items.getSlots() ? items.insertItem(index, ItemStack.EMPTY, false) : ItemStack.EMPTY;
	   }

	   public void setInventorySlotContents(int index, ItemStack stack) {
	      ItemStack itemstack = this.items.getStackInSlot(index);
	      boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
	      this.items.insertItem(index, stack, false);
	      if (stack.getCount() > this.getInventoryStackLimit()) {
	         stack.setCount(this.getInventoryStackLimit());
	      }

	      if (index == 0 && !flag) {
	         this.cookTimeTotal = this.func_214005_h();
	         this.cookTime = 0;
	         //this.burnTime = 0;
	         this.markDirty();
	      }

	   }

	   public boolean isUsableByPlayer(PlayerEntity player) {
	      if (this.world.getTileEntity(this.pos) != this) {
	         return false;
	      } else {
	         return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	      }
	   }

	   public boolean isItemValidForSlot(int index, ItemStack stack) {
	      if (index == 2) {
	         return false;
	      } else if (index != 1) {
	         return true;
	      } else {
	         ItemStack itemstack = this.items.getStackInSlot(1);
	         if(itemstack.getItem() == ItemInit.juice_glass.get()) {
	        	 return true;
	         } else {
	        	 return false;
	         }
	      }
	   }

	   public void clear() {
		   for(int i = 0; i < items.getSlots(); i++) {
			   items.insertItem(i, ItemStack.EMPTY, false);
		   }
	   }

	   public void setRecipeUsed(@Nullable IRecipe<?> recipe) {
	      if (recipe != null) {
	         this.field_214022_n.compute(recipe.getId(), (p_214004_0_, p_214004_1_) -> {
	            return 1 + (p_214004_1_ == null ? 0 : p_214004_1_);
	         });
	      }

	   }

	   @Nullable
	   public IRecipe<?> getRecipeUsed() {
	      return null;
	   }

	   public void onCrafting(PlayerEntity player) {
	   }

	   public void func_213995_d(PlayerEntity p_213995_1_) {
	      List<IRecipe<?>> list = Lists.newArrayList();

	      for(Entry<ResourceLocation, Integer> entry : this.field_214022_n.entrySet()) {
	         p_213995_1_.world.getRecipeManager().getRecipe(entry.getKey()).ifPresent((p_213993_3_) -> {
	            list.add(p_213993_3_);
	            func_214003_a(p_213995_1_, entry.getValue(), ((AbstractJuicerRecipe)p_213993_3_).getExperience());
	         });
	      }

	      p_213995_1_.unlockRecipes(list);
	      this.field_214022_n.clear();
	   }

	   private static void func_214003_a(PlayerEntity p_214003_0_, int p_214003_1_, float p_214003_2_) {
	      if (p_214003_2_ == 0.0F) {
	         p_214003_1_ = 0;
	      } else if (p_214003_2_ < 1.0F) {
	         int i = MathHelper.floor((float)p_214003_1_ * p_214003_2_);
	         if (i < MathHelper.ceil((float)p_214003_1_ * p_214003_2_) && Math.random() < (double)((float)p_214003_1_ * p_214003_2_ - (float)i)) {
	            ++i;
	         }

	         p_214003_1_ = i;
	      }
	   }

	   public void fillStackedContents(RecipeItemHelper helper) {
		   for(int i = 0; i < items.getSlots(); i++) {
			   ItemStack itemstack = this.items.getStackInSlot(i);
		   }
	   }

		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction side) {
			/*if (!this.removed && side != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
		         if (side == Direction.UP)
		             return handlers[0].cast();
		          else if (side == Direction.DOWN)
		             return handlers[1].cast();
		          else
		             return handlers[2].cast();
			}
			return super.getCapability(cap, side);*/
			return super.getCapability(cap, side);
		}

	   @Override
	   public void remove() {
	      super.remove();
			if(itemHandler != null) {
				itemHandler.invalidate();
			}
	   }
	   
	   public ItemStackHandler getInventory() {
		   return this.items;
	   }
	   
		@Override
		  @Nullable
		  public SUpdateTileEntityPacket getUpdatePacket() {
				CompoundNBT nbtTagCompound = new CompoundNBT();
				write(nbtTagCompound);
				return new SUpdateTileEntityPacket(this.pos, 0, nbtTagCompound);
			}
		
		@Override
		public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
			read(pkt.getNbtCompound());
		}
		
		  @Override
		  public CompoundNBT getUpdateTag(){
		    CompoundNBT nbtTagCompound = new CompoundNBT();
		    write(nbtTagCompound);
		    return nbtTagCompound;
		  }
		  
		  @Override
		  public void handleUpdateTag(CompoundNBT tag){
		    this.read(tag);
		  }
		  
		  public int getCookTime() {
			  return this.cookTime;
		  }
		  
		  public int getBurnTime() {
			  return this.burnTime;
		  }
		  
		  public int getCookTimeTotal() {
			  return this.cookTimeTotal;
		  }

		@Override
		public ItemStack getStackInSlot(int index) {
			return this.items.getStackInSlot(index);
		}

		/*@Override
		public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
			return this.isItemValidForSlot(index, itemStackIn);
		}

		@Override
	   public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
	      if (direction == Direction.DOWN && index == 1) {
	         Item item = stack.getItem();
	         if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
	            return false;
	         }
	      }

	      return true;
	   }*/
	}