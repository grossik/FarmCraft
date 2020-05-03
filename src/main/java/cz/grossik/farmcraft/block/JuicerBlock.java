package cz.grossik.farmcraft.block;

import java.util.Random;
import java.util.stream.Stream;

import cz.grossik.farmcraft.tileentity.FarmCraftTileEntityTypes;
import cz.grossik.farmcraft.tileentity.JuicerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class JuicerBlock extends ContainerBlock {
	public static final BooleanProperty HAS_BOTTLE = BooleanProperty.create("has_glass");
	public static final BooleanProperty VYPALUJE = RedstoneTorchBlock.LIT;
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	protected static final VoxelShape SHAPE_N = Stream.of(Block.makeCuboidShape(5, 0, 7, 11, 11, 13),
			Block.makeCuboidShape(6, 11, 9, 7, 14, 11),
			Block.makeCuboidShape(9, 11, 9, 10, 14, 11),
			Block.makeCuboidShape(7, 11, 8, 9, 14, 9),
			Block.makeCuboidShape(7, 11, 11, 9, 14, 12),
			Block.makeCuboidShape(6, 14, 8, 10, 15, 12),
			Block.makeCuboidShape(7, 6, 5, 9, 7, 7),
			Block.makeCuboidShape(7, 8, 6, 9, 9, 7),
			Block.makeCuboidShape(6, 7, 6, 7, 8, 7),
			Block.makeCuboidShape(9, 7, 6, 10, 8, 7)).reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();
	
	protected static final VoxelShape SHAPE_S = Stream.of(Block.makeCuboidShape(5, 0, 3, 11, 11, 9),
			Block.makeCuboidShape(9, 11, 5, 10, 14, 7),
			Block.makeCuboidShape(6, 11, 5, 7, 14, 7),
			Block.makeCuboidShape(7, 11, 7, 9, 14, 8),
			Block.makeCuboidShape(7, 11, 4, 9, 14, 5),
			Block.makeCuboidShape(6, 14, 4, 10, 15, 8),
			Block.makeCuboidShape(7, 6, 9, 9, 7, 11),
			Block.makeCuboidShape(7, 8, 9, 9, 9, 10),
			Block.makeCuboidShape(9, 7, 9, 10, 8, 10),
			Block.makeCuboidShape(6, 7, 9, 7, 8, 10)).reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();
	
	protected static final VoxelShape SHAPE_E = Stream.of(	Block.makeCuboidShape(3, 0, 5, 9, 11, 11),
			Block.makeCuboidShape(5, 11, 6, 7, 14, 7),
			Block.makeCuboidShape(5, 11, 9, 7, 14, 10),
			Block.makeCuboidShape(7, 11, 7, 8, 14, 9),
			Block.makeCuboidShape(4, 11, 7, 5, 14, 9),
			Block.makeCuboidShape(4, 14, 6, 8, 15, 10),
			Block.makeCuboidShape(9, 6, 7, 11, 7, 9),
			Block.makeCuboidShape(9, 8, 7, 10, 9, 9),
			Block.makeCuboidShape(9, 7, 6, 10, 8, 7),
			Block.makeCuboidShape(9, 7, 9, 10, 8, 10)).reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	protected static final VoxelShape SHAPE_W = Stream.of(Block.makeCuboidShape(7, 0, 5, 13, 11, 11),
			Block.makeCuboidShape(9, 11, 9, 11, 14, 10),
			Block.makeCuboidShape(9, 11, 6, 11, 14, 7),
			Block.makeCuboidShape(8, 11, 7, 9, 14, 9),
			Block.makeCuboidShape(11, 11, 7, 12, 14, 9),
			Block.makeCuboidShape(8, 14, 6, 12, 15, 10),
			Block.makeCuboidShape(5, 6, 7, 7, 7, 9),
			Block.makeCuboidShape(6, 8, 7, 7, 9, 9),
			Block.makeCuboidShape(6, 7, 9, 7, 8, 10),
			Block.makeCuboidShape(6, 7, 6, 7, 8, 7)).reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();
	
	   public JuicerBlock(Block.Properties p_i50000_1_) {
	      super(p_i50000_1_);
	      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HAS_BOTTLE, false).with(VYPALUJE, Boolean.valueOf(false)));
	   }
	
	   public BlockRenderType getRenderType(BlockState state) {
		   return BlockRenderType.MODEL;
	   }
	   
	   public TileEntity createNewTileEntity(IBlockReader worldIn) {
		   return FarmCraftTileEntityTypes.juicer.get().create();
	   }
	   
	   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		   	switch(state.get(FACING)) {
			   	case NORTH:
			   		return SHAPE_N;
			   	case SOUTH:
			   		return SHAPE_S;
			   	case EAST:
			   		return SHAPE_E;
			   	case WEST:
			   		return SHAPE_W;
		   		default:
		   			return SHAPE_N;
		   	}
	   }
	
		@Override
		public boolean hasTileEntity(BlockState state) {
			return true;
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
		   		if (tileentity instanceof JuicerTileEntity) {
		         player.openContainer((INamedContainerProvider)tileentity);
		         player.addStat(Stats.INTERACT_WITH_FURNACE);
		      }

	   }	
	   
	   public BlockState getStateForPlacement(BlockItemUseContext context) {
	      return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	   }
	
	   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
	      if (stack.hasDisplayName()) {
	         TileEntity tileentity = worldIn.getTileEntity(pos);
	         if (tileentity instanceof JuicerTileEntity) {
	            ((JuicerTileEntity)tileentity).setCustomName(stack.getDisplayName());
	         }
	      }
	
	   }
	
	   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
	      if (state.getBlock() != newState.getBlock()) {
	         TileEntity tileentity = worldIn.getTileEntity(pos);
	         if (tileentity instanceof JuicerTileEntity) {
	            InventoryHelper.dropInventoryItems(worldIn, pos, (JuicerTileEntity)tileentity);
	            worldIn.updateComparatorOutputLevel(pos, this);
	         }
	
	         super.onReplaced(state, worldIn, pos, newState, isMoving);
	      }
	   }
	
	   public boolean hasComparatorInputOverride(BlockState state) {
	      return true;
	   }
	
	   public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
	      return Container.calcRedstone(worldIn.getTileEntity(pos));
	   }

	   public BlockState rotate(BlockState state, Rotation rot) {
	      return state.with(FACING, rot.rotate(state.get(FACING)));
	   }

	   public BlockState mirror(BlockState state, Mirror mirrorIn) {
	      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	   }
	
	   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
	       builder.add(FACING, VYPALUJE, HAS_BOTTLE);
	   }
	   
	   @OnlyIn(Dist.CLIENT)
	   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
	      /*if (stateIn.get(LIT)) {
	         double d0 = (double)pos.getX() + 0.5D;
	         double d1 = (double)pos.getY();
	         double d2 = (double)pos.getZ() + 0.5D;
	         if (rand.nextDouble() < 0.1D) {
	            worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
	         }

	         Direction direction = stateIn.get(FACING);
	         Direction.Axis direction$axis = direction.getAxis();
	         double d3 = 0.52D;
	         double d4 = rand.nextDouble() * 0.6D - 0.3D;
	         double d5 = direction$axis == Direction.Axis.X ? (double)direction.getXOffset() * 0.52D : d4;
	         double d6 = rand.nextDouble() * 6.0D / 16.0D;
	         double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getZOffset() * 0.52D : d4;
	         worldIn.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
	         worldIn.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
	      }*/
	   }
}