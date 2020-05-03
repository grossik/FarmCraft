package cz.grossik.farmcraft.crop;

import java.util.Random;

import cz.grossik.farmcraft.init.BlockInit;
import cz.grossik.farmcraft.init.ItemInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class HopsBlock extends CropsBlock {
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
    private static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

	public HopsBlock(Block.Properties builder) {
		super(builder);
		this.setDefaultState(this.stateContainer.getBaseState().with(this.getAgeProperty(), Integer.valueOf(0)).with(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	   
	@Override
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.getBlock() instanceof FarmlandBlock;
	}

	@Override
	public int getMaxAge() {
		return 7;
	}

	@Override
	public int getAge(BlockState state) {
		return state.get(this.getAgeProperty());
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		//super.tick(state, worldIn, pos, random);
		if(!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		if(worldIn.getLightSubtracted(pos, 0) >= 9) {
			int i = this.getAge(state);
			if(i > 2) {
				if(worldIn.getBlockState(pos.up()) == Blocks.AIR.getDefaultState() && state.get(HALF) == DoubleBlockHalf.LOWER) {
					worldIn.setBlockState(pos.up(), this.withAge(i).with(HALF, DoubleBlockHalf.UPPER), 3);
				}
				
				if(worldIn.getBlockState(pos.up()).getBlock() == BlockInit.hops.get()) {
					if(worldIn.getBlockState(pos.up()).get(HALF) == DoubleBlockHalf.UPPER) {
						if (i < this.getMaxAge()) {
							float f = getGrowthChance(this, worldIn, pos);
					        if(net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
					        	worldIn.setBlockState(pos, this.withAge(i + 1).with(HALF, state.get(HALF)), 3);
					        	worldIn.setBlockState(pos.up(), this.withAge(i + 1).with(HALF, worldIn.getBlockState(pos.up()).get(HALF)), 3);
					        	net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
					        }
						}
					}
				}
			} else { 
				if (i < this.getMaxAge()) {
					float f = getGrowthChance(this, worldIn, pos);
			        if(net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
			        	worldIn.setBlockState(pos, this.withAge(i + 1).with(HALF, DoubleBlockHalf.LOWER), 3);
			        	net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
			        }
				}
			}
		}
	}
	
	@Override
	public void grow(World worldIn, BlockPos pos, BlockState state) {
		int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
		int j = this.getMaxAge();
		if (i > j) {
			i = j;
		}
		
		if(i > 2) {
			if(worldIn.getBlockState(pos.up()) == Blocks.AIR.getDefaultState() && state.get(HALF) == DoubleBlockHalf.LOWER) {
				worldIn.setBlockState(pos.up(), this.withAge(i).with(HALF, DoubleBlockHalf.UPPER), 3);
			}
		}

		if(state.get(HALF) == DoubleBlockHalf.LOWER) {
			worldIn.setBlockState(pos, this.withAge(i).with(HALF, state.get(HALF)), 2);
			if(worldIn.getBlockState(pos.up()).getBlock() == BlockInit.hops.get()) {
				if(worldIn.getBlockState(pos.up()).get(HALF) == DoubleBlockHalf.UPPER) {
					worldIn.setBlockState(pos.up(), this.withAge(i).with(HALF, worldIn.getBlockState(pos.up()).get(HALF)), 3);
				}
			}
		} else if(state.get(HALF) == DoubleBlockHalf.UPPER) {
			worldIn.setBlockState(pos, this.withAge(i).with(HALF, state.get(HALF)), 2);
			if(worldIn.getBlockState(pos.down()).getBlock() == BlockInit.hops.get()) {
				if(worldIn.getBlockState(pos.down()).get(HALF) == DoubleBlockHalf.LOWER) {
					worldIn.setBlockState(pos.down(), this.withAge(i).with(HALF, worldIn.getBlockState(pos.down()).get(HALF)), 3);
				}
			}
		}
	}
	   
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return (worldIn.getLightSubtracted(pos, 0) >= 8 || worldIn.canSeeSky(pos)) && placementChecker(state, worldIn, pos);
	}

	private boolean placementChecker(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockState block = worldIn.getBlockState(pos.down());
		if (block.getBlock() == Blocks.FARMLAND || (block.getBlock() == BlockInit.hops.get() && state.get(HALF) == DoubleBlockHalf.UPPER))
			return true;
		else
			return false;
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf doubleblockhalf = state.get(HALF);
        BlockPos blockpos = doubleblockhalf == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (blockstate.getBlock() == this && blockstate.get(HALF) != doubleblockhalf) {
           worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
           worldIn.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
           ItemStack itemstack = player.getHeldItemMainhand();
           if(!worldIn.isRemote && !player.isCreative() && player.canHarvestBlock(blockstate)) {
        	   if(state.get(HALF) == DoubleBlockHalf.UPPER) {
        		   Block.spawnDrops(state, worldIn, pos, (TileEntity)null, player, itemstack);
        		   Block.spawnDrops(blockstate, worldIn, blockpos, (TileEntity)null, player, itemstack);
        	   }
       		}
        }

        super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	protected IItemProvider getSeedsItem() {
		return ItemInit.hops_seed.get();
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(ItemInit.hops_item.get());
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(this.getAgeProperty(), HALF);
   }
	
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}
}