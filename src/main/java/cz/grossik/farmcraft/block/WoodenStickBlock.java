package cz.grossik.farmcraft.block;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Stream;

import cz.grossik.farmcraft.FarmCraftFunction;
import cz.grossik.farmcraft.init.ItemInit;
import cz.grossik.farmcraft.item.ItemStickSeed;
import cz.grossik.farmcraft.tileentity.FarmCraftTileEntityTypes;
import cz.grossik.farmcraft.tileentity.JuicerTileEntity;
import cz.grossik.farmcraft.tileentity.TileEntityWoodenStick;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.PlantType;

public class WoodenStickBlock extends ContainerBlock implements net.minecraftforge.common.IPlantable, IGrowable {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
	public static final BooleanProperty HAS_SEED = BooleanProperty.create("has_seed");
	public static final IntegerProperty GET_SEED = IntegerProperty.create("seed", 0, 1);

	public WoodenStickBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(this.getAgeProperty(), Integer.valueOf(0)).with(HAS_SEED, false).with(GET_SEED, 0));
	}
	
	   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
	       builder.add(AGE, HAS_SEED, GET_SEED);
	   }
	   
	   public IntegerProperty getAgeProperty() {
		      return AGE;
		   }

		   public int getMaxAge() {
		      return 7;
		   }

			
			@Override
			public boolean hasTileEntity(BlockState state) {
				return true;
			}
			
	   protected int getAge(BlockState state) {
		      return state.get(this.getAgeProperty());
		   }

		   public BlockState withAge(int age, int type) {
			   //u has seed muze byt true, protoze jinak se neudela update
		      return this.getDefaultState().with(this.getAgeProperty(), Integer.valueOf(age)).with(HAS_SEED, true).with(GET_SEED, FarmCraftFunction.getWoodenStickItem(type).getType());
		   }
		   
		   public BlockState withAge() {
			   //u has seed muze byt true, protoze jinak se neudela update
		      return this.getDefaultState().with(this.getAgeProperty(), Integer.valueOf(0)).with(HAS_SEED, false).with(GET_SEED, 0);
		   }
			   
	   public ActionResultType onBlockActivated(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
		   ItemStack itemstack = p_225533_4_.getHeldItem(p_225533_5_);
		   	if (p_225533_2_.isRemote) {
		         return ActionResultType.SUCCESS;
		      } else {
		         if(this.interactWith(p_225533_2_, p_225533_3_, p_225533_4_, itemstack)) {
		        	 return ActionResultType.SUCCESS;
		         } else {
		        	 return ActionResultType.FAIL;
		         }
		      }
		   }
		

		   protected boolean interactWith(World worldIn, BlockPos pos, PlayerEntity player, ItemStack itemstack) {
			   	TileEntity tileentity = worldIn.getTileEntity(pos);
		   	    	if (tileentity instanceof TileEntityWoodenStick) {
		   	    		if(itemstack.getItem() instanceof ItemStickSeed) {
		   	    			boolean insertItem = ((TileEntityWoodenStick) tileentity).addSeed(itemstack);
		   	    			if(insertItem) {
		   	    				if(!player.isCreative()) {
		   	    					itemstack.shrink(1);
		   	    				}
		   	    				worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
		   	    				return true;
		   	    			}
		   	    		}
		   	    	}
		   	    	return false;
		   }	
	
   public BlockRenderType getRenderType(BlockState state) {
	   return BlockRenderType.MODEL;
   }
		   
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return PlantType.Crop;
	}
	
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
	   return state.getBlock() == Blocks.FARMLAND;
	}
	
	@Override
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
	      return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	   }

	@Override
	   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
	      BlockPos blockpos = pos.down();
	      if (state.getBlock() == this) //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
	         return worldIn.getBlockState(blockpos).canSustainPlant(worldIn, blockpos, Direction.UP, this);
	      return this.isValidGround(worldIn.getBlockState(blockpos), worldIn, blockpos);
	   }

	@Override
	   public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
	      return true;
	   }

	@Override
	   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
	      return type == PathType.AIR && !this.blocksMovement ? true : super.allowsMovement(state, worldIn, pos, type);
	   }
	   
	/*public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}*/

	   @Override
	   public BlockState getPlant(IBlockReader world, BlockPos pos) {
	      BlockState state = world.getBlockState(pos);
	      if (state.getBlock() != this) return getDefaultState();
	      return state;
	   }

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return FarmCraftTileEntityTypes.wooden_stick.get().create();
	}

   public boolean isMaxAge(BlockState state) {
	      return state.get(this.getAgeProperty()) >= this.getMaxAge();
   }
	   
   @Override
	   public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
	      return !this.isMaxAge(state);
	   }

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		// TODO Auto-generated method stub
		if(state.get(HAS_SEED)) {
			return true;
		}
		return false;
	}

	@Override
	public void grow(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_,
			BlockState p_225535_4_) {
		if(p_225535_4_.get(HAS_SEED) == true) {
			this.grow(p_225535_1_, p_225535_3_, p_225535_4_);
		}
	}
	
	@Override
	   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
		      super.tick(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
		      if(p_225534_1_.get(HAS_SEED) == true) {
			      if (!p_225534_2_.isAreaLoaded(p_225534_3_, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
			      if (p_225534_2_.getLightSubtracted(p_225534_3_, 0) >= 9) {
			         int i = this.getAge(p_225534_1_);
			         if (i < this.getMaxAge()) {
			            float f = getGrowthChance(this, p_225534_2_, p_225534_3_);
			            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_225534_2_, p_225534_3_, p_225534_1_, p_225534_4_.nextInt((int)(25.0F / f) + 1) == 0)) {
			            	TileEntity tileentity = p_225534_2_.getTileEntity(p_225534_3_);
				   	    	if (tileentity instanceof TileEntityWoodenStick) {			   	    	
				            	p_225534_2_.setBlockState(p_225534_3_, this.withAge(i + 1, p_225534_1_.get(GET_SEED)), 2);
				                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_225534_2_, p_225534_3_, p_225534_1_);
				   	    	}
			            }
			         }
			      }
		      }
		   }
	
	
	   public void grow(World worldIn, BlockPos pos, BlockState state) {
		      int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
		      int j = this.getMaxAge();
		      if (i > j) {
		         i = j;
		      }

          	TileEntity tileentity =  worldIn.getTileEntity(pos);
	   	    	if (tileentity instanceof TileEntityWoodenStick) {			   	    	
	   	    		worldIn.setBlockState(pos, this.withAge(i, state.get(GET_SEED)), 2);
	   	    	}
		      
		   }
	   
	   protected int getBonemealAgeIncrease(World worldIn) {
		      return MathHelper.nextInt(worldIn.rand, 2, 5);
		   }

		   protected static float getGrowthChance(Block blockIn, IBlockReader worldIn, BlockPos pos) {
		      float f = 1.0F;
		      BlockPos blockpos = pos.down();

		      for(int i = -1; i <= 1; ++i) {
		         for(int j = -1; j <= 1; ++j) {
		            float f1 = 0.0F;
		            BlockState blockstate = worldIn.getBlockState(blockpos.add(i, 0, j));
		            if (blockstate.canSustainPlant(worldIn, blockpos.add(i, 0, j), net.minecraft.util.Direction.UP, (net.minecraftforge.common.IPlantable)blockIn)) {
		               f1 = 1.0F;
		               if (blockstate.isFertile(worldIn, blockpos.add(i, 0, j))) {
		                  f1 = 3.0F;
		               }
		            }

		            if (i != 0 || j != 0) {
		               f1 /= 4.0F;
		            }

		            f += f1;
		         }
		      }

		      BlockPos blockpos1 = pos.north();
		      BlockPos blockpos2 = pos.south();
		      BlockPos blockpos3 = pos.west();
		      BlockPos blockpos4 = pos.east();
		      boolean flag = blockIn == worldIn.getBlockState(blockpos3).getBlock() || blockIn == worldIn.getBlockState(blockpos4).getBlock();
		      boolean flag1 = blockIn == worldIn.getBlockState(blockpos1).getBlock() || blockIn == worldIn.getBlockState(blockpos2).getBlock();
		      if (flag && flag1) {
		         f /= 2.0F;
		      } else {
		         boolean flag2 = blockIn == worldIn.getBlockState(blockpos3.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.south()).getBlock() || blockIn == worldIn.getBlockState(blockpos3.south()).getBlock();
		         if (flag2) {
		            f /= 2.0F;
		         }
		      }

		      return f;
		   }

		   private Collection<ItemEntity> captureDrops = null;
		   public Collection<ItemEntity> captureDrops() {
		      return captureDrops;
		   }
		   
		   public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
			   if(state.get(HAS_SEED)) {
				   if(this.getAge(state) == 7) {
					   	TileEntity tileentity = worldIn.getTileEntity(pos);
			   	    	if (tileentity instanceof TileEntityWoodenStick) {
			   	    		 ((TileEntityWoodenStick) tileentity).deleteSeed();
			   	    		Item itemInSlot = ((TileEntityWoodenStick) tileentity).getSeed().getStackInSlot(0).getItem();
			   	    		 worldIn.setBlockState(pos, this.withAge(), 2);
			   	    		
			   	    	     ItemEntity itementityItem = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), FarmCraftFunction.getWoodenStickItem(state.get(GET_SEED)).getItemOutput().getDefaultInstance());
			   	    	     itementityItem.setDefaultPickupDelay();
				   	         if (captureDrops() != null) { 
				   	        	captureDrops().add(itementityItem);
			   	    		 } else {
				   	        	worldIn.addEntity(itementityItem);
				   	         }
			   	    	     Random rand = new Random();
			   	    	     for(int i = 0; i < rand.nextInt(2)+1; i++) {
			   	    	    	 ItemEntity itementity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), FarmCraftFunction.getWoodenStickItem(state.get(GET_SEED)).getDefaultInstance());
				   	         	itementity.setDefaultPickupDelay();
					   	         if (captureDrops() != null) { 
					   	        	 captureDrops().add(itementity);
				   	    		 } else {
					   	        	 worldIn.addEntity(itementity);
					   	         }
			   	    	     }

			   	    	}					   
				   } else {
					   	TileEntity tileentity = worldIn.getTileEntity(pos);
			   	    	if (tileentity instanceof TileEntityWoodenStick) {
			   	    		 ((TileEntityWoodenStick) tileentity).deleteSeed();
			   	    		worldIn.setBlockState(pos, this.withAge(), 2);
			   	    		Item itemInSlot = ((TileEntityWoodenStick) tileentity).getSeed().getStackInSlot(0).getItem();
				   	         ItemEntity itementity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), FarmCraftFunction.getWoodenStickItem(state.get(GET_SEED)).getDefaultInstance());
				   	         itementity.setDefaultPickupDelay();
				   	         if (captureDrops() != null) captureDrops().add(itementity);
				   	         else
				   	         worldIn.addEntity(itementity);
			   	    	}
				   }
				   worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
			   }
		   }
		   
}
