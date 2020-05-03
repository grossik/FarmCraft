package cz.grossik.farmcraft.handler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class FarmCraftItemsHandler extends ItemStackHandler {
	public FarmCraftItemsHandler(int slots) {
		super(slots);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return super.insertItem(slot, stack, simulate);
	}
	
	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		if(slot+1 >= stacks.size()) return;
		if(this.stacks.get(slot) != ItemStack.EMPTY && this.stacks.get(slot+1) != ItemStack.EMPTY) {
			if(ItemStack.areItemsEqual(this.stacks.get(slot), this.stacks.get(slot+1))) {
				int totalSize = this.stacks.get(slot).getCount() + this.stacks.get(slot+1).getCount();
				int change = this.stacks.get(slot+1).getCount();
				if(totalSize > this.stacks.get(slot).getMaxStackSize()) {
					change = this.stacks.get(slot).getMaxStackSize() - this.stacks.get(slot).getCount();
				}
				this.stacks.get(slot).grow(change);
				this.stacks.get(slot+1).shrink(change);
				if(this.stacks.get(slot+1).isEmpty()) {
					this.stacks.set(slot+1, ItemStack.EMPTY);
				}
				onContentsChanged(slot+1);
			}
		}
	}
}