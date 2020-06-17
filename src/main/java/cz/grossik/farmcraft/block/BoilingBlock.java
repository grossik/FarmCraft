package cz.grossik.farmcraft.block;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import cz.grossik.farmcraft.init.BlockInit;
import cz.grossik.farmcraft.multiblock.BoilingMultiBlock;
import cz.grossik.farmcraft.multiblock.BoilingPartIndex;
import cz.grossik.farmcraft.multiblock.MultiBlockTools;
import cz.grossik.farmcraft.tileentity.FarmCraftTileEntityTypes;
import cz.grossik.farmcraft.tileentity.TileEntityBoiling;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.fml.network.NetworkHooks;

public class BoilingBlock extends Block {

    public static final EnumProperty<BoilingPartIndex> FORMED = EnumProperty.<BoilingPartIndex>create("formed", BoilingPartIndex.class);
	
	public BoilingBlock(Properties builder) {
		super(builder);		
		
		this.setDefaultState(this.stateContainer.getBaseState().with(FORMED, BoilingPartIndex.UNFORMED));
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if(state.get(FORMED) == BoilingPartIndex.P000 || state.get(FORMED) == BoilingPartIndex.P010) {
			return SHAPE_P000;
		} else if(state.get(FORMED) == BoilingPartIndex.P001 || state.get(FORMED) == BoilingPartIndex.P011) {
			return SHAPE_P001;
		} else if(state.get(FORMED) == BoilingPartIndex.P100 || state.get(FORMED) == BoilingPartIndex.P110) {  
			return SHAPE_P100;
		} else if(state.get(FORMED) == BoilingPartIndex.P101 || state.get(FORMED) == BoilingPartIndex.P111) {  
			return SHAPE_P101;
		} else {
			return VoxelShapes.fullCube();
		}
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		if(state.get(FORMED) == BoilingPartIndex.P000) {
			return FarmCraftTileEntityTypes.boiling.get().create();
		} else {
			return null;
		}
	}

	@Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(placer instanceof PlayerEntity) {
			toggleMultiBlock(worldIn, pos, state, (PlayerEntity) placer);
		}
	}
    
	public ActionResultType onBlockActivated(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
		if (p_225533_2_.isRemote) {
			return ActionResultType.SUCCESS;
		} else {
			if(p_225533_1_.get(FORMED) != BoilingPartIndex.UNFORMED) {
				if(p_225533_1_.get(FORMED) == BoilingPartIndex.P000) {
					this.interactWith(p_225533_2_, p_225533_3_, p_225533_4_);
				} else {
					BlockPos controllerPos = getControllerPos(p_225533_2_, p_225533_3_);
		            if(controllerPos != null) {
		                BlockState controllerState = p_225533_2_.getBlockState(controllerPos);
		                return controllerState.getBlock().onBlockActivated(controllerState, p_225533_2_, controllerPos, p_225533_4_, p_225533_5_, p_225533_6_);
		            }
				}
				return ActionResultType.SUCCESS;
			} else {
				return ActionResultType.PASS;
			}
		}	
   	}
    
	protected void interactWith(World worldIn, BlockPos pos, PlayerEntity player) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileEntityBoiling) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityBoiling) tile, pos);
		}
	}	
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FORMED);
	}

	@Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if(!worldIn.isRemote) {
            MultiBlockTools.breakMultiblock(BoilingMultiBlock.INSTANCE, worldIn, pos);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
	}
	
    public static void toggleMultiBlock(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        // Form or break the multiblock
        if (!world.isRemote) {
            BoilingPartIndex formed = state.get(FORMED);
            if (formed == BoilingPartIndex.UNFORMED) {
                if (MultiBlockTools.formMultiblock(BoilingMultiBlock.INSTANCE, world, pos)) {
                    player.sendStatusMessage(new TranslationTextComponent(TextFormatting.GREEN + "Made a superchest!"), false);
                } else {
                    player.sendStatusMessage(new TranslationTextComponent(TextFormatting.RED + "Could not form superchest!"), false);
                }
            } else {
                /*if (!MultiBlockTools.breakMultiblock(BoilingMultiBlock.INSTANCE, world, pos)) {
                    player.sendStatusMessage(new TranslationTextComponent(TextFormatting.RED + "Not a valid superchest!"), false);
                }*/
            }
        }
    }

    public static boolean isFormedSuperchestController(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() == BlockInit.boiling_block.get() && state.get(FORMED) != BoilingPartIndex.UNFORMED;
    }

    @Nullable
    public static BlockPos getControllerPos(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == BlockInit.boiling_block.get()) {
        	BoilingPartIndex index = state.get(BoilingBlock.FORMED);
            // This index indicates where in the superblock this part is located. From this we can find the location of the bottom-left coordinate
            BlockPos bottomLeft = pos.add(-index.getDx(), -index.getDy(), -index.getDz());
            for (BoilingPartIndex idx : BoilingPartIndex.VALUES) {
                if (idx == BoilingPartIndex.P000) {
                    BlockPos p = bottomLeft.add(idx.getDx(), idx.getDy(), idx.getDz());
                    if (isFormedSuperchestController(world, p)) {
                        return p;
                    }
                }
            }

        }
        return null;
    }
    
	protected static final VoxelShape SHAPE_P000 = Stream.of(Block.makeCuboidShape(12, 0, 1, 16, 1, 2),
			Block.makeCuboidShape(0, 0, 12, 1, 16, 16),
			Block.makeCuboidShape(1, 0, 9, 2, 16, 12),
			Block.makeCuboidShape(2, 0, 7, 3, 16, 9),
			Block.makeCuboidShape(3, 0, 6, 4, 16, 7),
			Block.makeCuboidShape(4, 0, 5, 5, 16, 6),
			Block.makeCuboidShape(5, 0, 4, 6, 16, 5),
			Block.makeCuboidShape(6, 0, 3, 7, 16, 4),
			Block.makeCuboidShape(7, 0, 2, 9, 16, 3),
			Block.makeCuboidShape(9, 0, 1, 12, 16, 2),
			Block.makeCuboidShape(12, 0, 0, 16, 16, 1),
			Block.makeCuboidShape(1, 0, 12, 16, 1, 16),
			Block.makeCuboidShape(2, 0, 9, 16, 1, 12),
			Block.makeCuboidShape(3, 0, 7, 16, 1, 9),
			Block.makeCuboidShape(4, 0, 6, 16, 1, 7),
			Block.makeCuboidShape(5, 0, 5, 16, 1, 6),
			Block.makeCuboidShape(6, 0, 4, 16, 1, 5),
			Block.makeCuboidShape(7, 0, 3, 16, 1, 4),
			Block.makeCuboidShape(9, 0, 2, 16, 1, 3)).reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();
	
	protected static final VoxelShape SHAPE_P001 = Stream.of(Block.makeCuboidShape(12, 0, 15, 16, 16, 16),
			Block.makeCuboidShape(9, 0, 14, 12, 16, 15),
			Block.makeCuboidShape(7, 0, 13, 9, 16, 14),
			Block.makeCuboidShape(6, 0, 12, 7, 16, 13),
			Block.makeCuboidShape(5, 0, 11, 6, 16, 12),
			Block.makeCuboidShape(4, 0, 10, 5, 16, 11),
			Block.makeCuboidShape(3, 0, 9, 4, 16, 10),
			Block.makeCuboidShape(2, 0, 7, 3, 16, 9),
			Block.makeCuboidShape(1, 0, 4, 2, 16, 7),
			Block.makeCuboidShape(0, 0, 0, 1, 16, 4),
			Block.makeCuboidShape(12, 0, 0, 16, 1, 15),
			Block.makeCuboidShape(9, 0, 0, 12, 1, 14),
			Block.makeCuboidShape(7, 0, 0, 9, 1, 13),
			Block.makeCuboidShape(6, 0, 0, 7, 1, 12),
			Block.makeCuboidShape(5, 0, 0, 6, 1, 11),
			Block.makeCuboidShape(4, 0, 0, 5, 1, 10),
			Block.makeCuboidShape(3, 0, 0, 4, 1, 9),
			Block.makeCuboidShape(2, 0, 0, 3, 1, 7),
			Block.makeCuboidShape(1, 0, 0, 2, 1, 4)).reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();    
	
	protected static final VoxelShape SHAPE_P100 = Stream.of(Block.makeCuboidShape(0, 0, 0, 4, 16, 1),
			Block.makeCuboidShape(4, 0, 1, 7, 16, 2),
			Block.makeCuboidShape(7, 0, 2, 9, 16, 3),
			Block.makeCuboidShape(9, 0, 3, 10, 16, 4),
			Block.makeCuboidShape(10, 0, 4, 11, 16, 5),
			Block.makeCuboidShape(11, 0, 5, 12, 16, 6),
			Block.makeCuboidShape(12, 0, 6, 13, 16, 7),
			Block.makeCuboidShape(13, 0, 7, 14, 16, 9),
			Block.makeCuboidShape(14, 0, 9, 15, 16, 12),
			Block.makeCuboidShape(15, 0, 12, 16, 16, 16),
			Block.makeCuboidShape(0, 0, 1, 4, 1, 16),
			Block.makeCuboidShape(4, 0, 2, 7, 1, 16),
			Block.makeCuboidShape(7, 0, 3, 9, 1, 16),
			Block.makeCuboidShape(9, 0, 4, 10, 1, 16),
			Block.makeCuboidShape(10, 0, 5, 11, 1, 16),
			Block.makeCuboidShape(11, 0, 6, 12, 1, 16),
			Block.makeCuboidShape(12, 0, 7, 13, 1, 16),
			Block.makeCuboidShape(13, 0, 9, 14, 1, 16),
			Block.makeCuboidShape(14, 0, 12, 15, 1, 16)).reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();    
	
	protected static final VoxelShape SHAPE_P101 = Stream.of(Block.makeCuboidShape(15, 0, 0, 16, 16, 4),
			Block.makeCuboidShape(14, 0, 4, 15, 16, 7),
			Block.makeCuboidShape(13, 0, 7, 14, 16, 9),
			Block.makeCuboidShape(12, 0, 9, 13, 16, 10),
			Block.makeCuboidShape(11, 0, 10, 12, 16, 11),
			Block.makeCuboidShape(10, 0, 11, 11, 16, 12),
			Block.makeCuboidShape(9, 0, 12, 10, 16, 13),
			Block.makeCuboidShape(7, 0, 13, 9, 16, 14),
			Block.makeCuboidShape(4, 0, 14, 7, 16, 15),
			Block.makeCuboidShape(0, 0, 15, 4, 16, 16),
			Block.makeCuboidShape(0, 0, 0, 15, 1, 4),
			Block.makeCuboidShape(0, 0, 4, 14, 1, 7),
			Block.makeCuboidShape(0, 0, 7, 13, 1, 9),
			Block.makeCuboidShape(0, 0, 9, 12, 1, 10),
			Block.makeCuboidShape(0, 0, 10, 11, 1, 11),
			Block.makeCuboidShape(0, 0, 11, 10, 1, 12),
			Block.makeCuboidShape(0, 0, 12, 9, 1, 13),
			Block.makeCuboidShape(0, 0, 13, 7, 1, 14),
			Block.makeCuboidShape(0, 0, 14, 4, 1, 15)).reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();    
}
