package cz.grossik.farmcraft.container;

import cz.grossik.farmcraft.init.ItemInit;
import cz.grossik.farmcraft.recipe.AbstractJuicerRecipe;
import cz.grossik.farmcraft.tileentity.JuicerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.ServerRecipePlacerFurnace;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractJuicerContainer extends RecipeBookContainer<IInventory> {
	   private final IInventory furnaceInventory;
	   private final IIntArray field_217064_e;
	   protected final World world;
	   private final IRecipeType<? extends AbstractJuicerRecipe> recipeType;

	   protected AbstractJuicerContainer(ContainerType<?> containerTypeIn, IRecipeType<? extends AbstractJuicerRecipe> recipeTypeIn, int id, PlayerInventory playerInventoryIn) {
	      this(containerTypeIn, recipeTypeIn, id, playerInventoryIn, new Inventory(3), new IntArray(4));
	   }

	   protected AbstractJuicerContainer(ContainerType<?> containerTypeIn, IRecipeType<? extends AbstractJuicerRecipe> recipeTypeIn, int id, PlayerInventory playerInventoryIn, IInventory furnaceInventoryIn, IIntArray p_i50104_6_) {
	      super(containerTypeIn, id);
	      this.recipeType = recipeTypeIn;
	      assertInventorySize(furnaceInventoryIn, 3);
	      assertIntArraySize(p_i50104_6_, 4);
	      this.furnaceInventory = furnaceInventoryIn;
	      this.field_217064_e = p_i50104_6_;
	      this.world = playerInventoryIn.player.world;
	      this.addSlot(new Slot(furnaceInventoryIn, 0, 56, 17));
	      this.addSlot(new JuicerGlassSlot(this, furnaceInventoryIn, 1, 56, 53));
	      this.addSlot(new JuicerResultSlot(playerInventoryIn.player, furnaceInventoryIn, 2, 116, 35));

	      for(int i = 0; i < 3; ++i) {
	         for(int j = 0; j < 9; ++j) {
	            this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
	         }
	      }

	      for(int k = 0; k < 9; ++k) {
	         this.addSlot(new Slot(playerInventoryIn, k, 8 + k * 18, 142));
	      }

	      this.trackIntArray(p_i50104_6_);
	   }

	   public void func_201771_a(RecipeItemHelper p_201771_1_) {
	      if (this.furnaceInventory instanceof IRecipeHelperPopulator) {
	         ((IRecipeHelperPopulator)this.furnaceInventory).fillStackedContents(p_201771_1_);
	      }

	   }

	   public void clear() {
	      this.furnaceInventory.clear();
	   }

	   public void func_217056_a(boolean p_217056_1_, IRecipe<?> p_217056_2_, ServerPlayerEntity p_217056_3_) {
	      (new ServerRecipePlacerFurnace<>(this)).place(p_217056_3_, (IRecipe<IInventory>)p_217056_2_, p_217056_1_);
	   }

	   public boolean matches(IRecipe<? super IInventory> recipeIn) {
	      return recipeIn.matches(this.furnaceInventory, this.world);
	   }

	   public int getOutputSlot() {
	      return 2;
	   }

	   public int getWidth() {
	      return 1;
	   }

	   public int getHeight() {
	      return 1;
	   }

	   @OnlyIn(Dist.CLIENT)
	   public int getSize() {
	      return 3;
	   }

	   /**
	    * Determines whether supplied player can use this container
	    */
	   public boolean canInteractWith(PlayerEntity playerIn) {
	      return this.furnaceInventory.isUsableByPlayer(playerIn);
	   }

	   /**
	    * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	    * inventory and the other inventory(s).
	    */
	   public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
	      ItemStack itemstack = ItemStack.EMPTY;
	      Slot slot = this.inventorySlots.get(index);
	      if (slot != null && slot.getHasStack()) {
	         ItemStack itemstack1 = slot.getStack();
	         itemstack = itemstack1.copy();
	         if (index == 2) {
	            if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
	               return ItemStack.EMPTY;
	            }

	            slot.onSlotChange(itemstack1, itemstack);
	         } else if (index != 1 && index != 0) {
	            if (this.func_217057_a(itemstack1)) {
	               if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
	                  return ItemStack.EMPTY;
	               }
	            } else if (this.isFuel(itemstack1)) {
	               if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
	                  return ItemStack.EMPTY;
	               }
	            } else if (index >= 3 && index < 30) {
	               if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
	                  return ItemStack.EMPTY;
	               }
	            } else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
	               return ItemStack.EMPTY;
	            }
	         } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
	            return ItemStack.EMPTY;
	         }

	         if (itemstack1.isEmpty()) {
	            slot.putStack(ItemStack.EMPTY);
	         } else {
	            slot.onSlotChanged();
	         }

	         if (itemstack1.getCount() == itemstack.getCount()) {
	            return ItemStack.EMPTY;
	         }

	         slot.onTake(playerIn, itemstack1);
	      }

	      return itemstack;
	   }

	   protected boolean func_217057_a(ItemStack p_217057_1_) {
	      return this.world.getRecipeManager().getRecipe((IRecipeType)this.recipeType, new Inventory(p_217057_1_), this.world).isPresent();
	   }

	   protected boolean isFuel(ItemStack p_217058_1_) {
		   if(p_217058_1_.getItem() == ItemInit.juice_glass.get()) {
			   return true;
		   }
		   
		   return false;
	   }

	   @OnlyIn(Dist.CLIENT)
	   public int getCookProgressionScaled() {
	      int i = this.field_217064_e.get(2);
	      int j = this.field_217064_e.get(3);
	      return j != 0 && i != 0 ? i * 24 / j : 0;
	   }

	   @OnlyIn(Dist.CLIENT)
	   public int getBurnLeftScaled() {
	      int i = this.field_217064_e.get(1);
	      if (i == 0) {
	         i = 200;
	      }

	      return this.field_217064_e.get(0) * 14 / i;
	   }

	   @OnlyIn(Dist.CLIENT)
	   public boolean func_217061_l() {
	      return this.field_217064_e.get(0) > 0;
	   }
	   
	   public class JuicerGlassSlot extends Slot {
		   private final AbstractJuicerContainer field_216939_a;

		   public JuicerGlassSlot(AbstractJuicerContainer p_i50084_1_, IInventory p_i50084_2_, int p_i50084_3_, int p_i50084_4_, int p_i50084_5_) {
		      super(p_i50084_2_, p_i50084_3_, p_i50084_4_, p_i50084_5_);
		      this.field_216939_a = p_i50084_1_;
		   }

		   public boolean isItemValid(ItemStack stack) {
			   return this.field_216939_a.isFuel(stack);
		   }

		   public int getItemStackLimit(ItemStack stack) {
		      return super.getItemStackLimit(stack);
		   }
		}
	   
	   public class JuicerResultSlot extends Slot {
		   private final PlayerEntity player;
		   private int removeCount;

		   public JuicerResultSlot(PlayerEntity player, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
		      super(inventoryIn, slotIndex, xPosition, yPosition);
		      this.player = player;
		   }

		   public boolean isItemValid(ItemStack stack) {
		      return false;
		   }

		   public ItemStack decrStackSize(int amount) {
		      if (this.getHasStack()) {
		         this.removeCount += Math.min(amount, this.getStack().getCount());
		      }

		      return super.decrStackSize(amount);
		   }

		   public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
		      this.onCrafting(stack);
		      super.onTake(thePlayer, stack);
		      return stack;
		   }

		   protected void onCrafting(ItemStack stack, int amount) {
		      this.removeCount += amount;
		      this.onCrafting(stack);
		   }

		   protected void onCrafting(ItemStack stack) {
		      stack.onCrafting(this.player.world, this.player, this.removeCount);
		      if (!this.player.world.isRemote && this.inventory instanceof JuicerTileEntity) {
		         ((JuicerTileEntity)this.inventory).func_213995_d(this.player);
		      }

		      this.removeCount = 0;
		      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerSmeltedEvent(this.player, stack);
		   }
		}
	}