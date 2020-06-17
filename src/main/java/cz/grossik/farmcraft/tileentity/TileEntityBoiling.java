package cz.grossik.farmcraft.tileentity;

import cz.grossik.farmcraft.container.BoilingContainer;
import cz.grossik.farmcraft.handler.FarmCraftItemsHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityBoiling extends TileEntity implements IInventory, INamedContainerProvider {

	public boolean isValidMultiblock = false;
	
	private ItemStackHandler items;
	
	public TileEntityBoiling() {
		super(FarmCraftTileEntityTypes.boiling.get());
		items = new FarmCraftItemsHandler(1);
	}

	@Override
	public void clear() {
		for(int i = 0; i < items.getSlots(); i++) {
			items.insertItem(i, ItemStack.EMPTY, false);
		}
	}

	public int getSizeInventory() {
		return 1;
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
	   
	@Override
	public ItemStack getStackInSlot(int index) {
		return this.items.getStackInSlot(index);
	}

	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack itemstack = this.items.getStackInSlot(index);
		boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
		this.items.insertItem(index, stack, false);
		if(stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
	
		/*if (index == 0 && !flag) {
     		this.markDirty();
	  	}*/
	}

	public boolean isUsableByPlayer(PlayerEntity player) {
		if(this.world.getTileEntity(this.pos) != this) {
			return false;
		} else {
			return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		return new BoilingContainer(p_createMenu_1_, p_createMenu_2_, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("farmcraft.container.boiling");
	}
	
	public void read(CompoundNBT compound) {
		super.read(compound);
		
		items.deserializeNBT(compound.getCompound("boiling:slots"));
	}
	
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		
		compound.put("boiling:slots", items.serializeNBT());
		
		return compound;
	}
}
