package cz.grossik.farmcraft.block;

import cz.grossik.farmcraft.tileentity.FarmCraftTileEntityTypes;
import cz.grossik.farmcraft.tileentity.TileEntityBoiling;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BoilingBlock extends ContainerBlock {

	public BoilingBlock(Properties builder) {
		super(builder);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return FarmCraftTileEntityTypes.boiling.get().create();
	}

	public ActionResultType onBlockActivated(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
		if (p_225533_2_.isRemote) {
			return ActionResultType.SUCCESS;
		} else {
			this.interactWith(p_225533_2_, p_225533_3_, p_225533_4_);
			return ActionResultType.SUCCESS;
		}	
   	}
	   
	protected void interactWith(World worldIn, BlockPos pos, PlayerEntity player) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
   		if (tileentity instanceof TileEntityBoiling) {
   			player.openContainer((INamedContainerProvider)tileentity);
   		}
	}	
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
}
