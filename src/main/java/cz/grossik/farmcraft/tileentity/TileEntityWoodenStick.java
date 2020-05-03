package cz.grossik.farmcraft.tileentity;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cz.grossik.farmcraft.FarmCraftFunction;
import cz.grossik.farmcraft.block.JuicerBlock;
import cz.grossik.farmcraft.block.WoodenStickBlock;
import cz.grossik.farmcraft.init.ItemInit;
import cz.grossik.farmcraft.item.ItemStickSeed;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityWoodenStick extends TileEntity {

	private ItemStackHandler seed;
	private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> seed);
	
	public TileEntityWoodenStick() {
		super(FarmCraftTileEntityTypes.wooden_stick.get());
		seed = new ItemStackHandler();
	}

	public boolean addSeed(ItemStack item) {
		if(seed.getStackInSlot(0) == ItemStack.EMPTY) {
		   	 BlockState blockstate = this.world.getBlockState(this.getPos());
		     if (!(blockstate.getBlock() instanceof WoodenStickBlock)) {
		         return false;
		      }
		     seed.insertItem(0, item, false);
		     blockstate = blockstate.with(WoodenStickBlock.GET_SEED, ((ItemStickSeed) item.getItem()).getType());
		     blockstate = blockstate.with(WoodenStickBlock.HAS_SEED, true);
		     this.world.setBlockState(this.pos, blockstate, 3);
			return true;
		} else {
			return false;
		}
	}
	
	public void deleteSeed() {
	   	 BlockState blockstate = this.world.getBlockState(this.getPos());
	     if (!(blockstate.getBlock() instanceof WoodenStickBlock)) {
	         return;
	      }
	     seed = new ItemStackHandler();
	     blockstate = blockstate.with(WoodenStickBlock.GET_SEED, 0);
	     blockstate = blockstate.with(WoodenStickBlock.HAS_SEED, false);
	     this.world.setBlockState(this.pos, blockstate, 3);
	}
	
	   public void read(CompoundNBT compound) {
		      super.read(compound);
	   
		      this.seed.deserializeNBT(compound.getCompound("crop:seed"));
	   }
	
	   public CompoundNBT write(CompoundNBT compound) {
		      super.write(compound);
		      compound.put("crop:seed", seed.serializeNBT());
		      return compound;
	   }
	   
	   public ItemStackHandler getSeed() {
		   return seed;
	   }
	   
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction side) {
			if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				return itemHandler.cast();
			}
			return super.getCapability(cap, side);
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
		  
}
