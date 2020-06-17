package cz.grossik.farmcraft.multiblock;

import javax.annotation.Nullable;

import cz.grossik.farmcraft.block.BoilingBlock;
import cz.grossik.farmcraft.init.BlockInit;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BoilingMultiBlock implements IMultiBlockType {

    public static BoilingMultiBlock INSTANCE = new BoilingMultiBlock();

    private boolean isBlockPart(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() == BlockInit.boiling_block.get();
    }

    private boolean isValidFormedBlockPart(World world, BlockPos pos, int dx, int dy, int dz) {
        BlockPos p = pos;
        if (isFormedSuperchestController(world, p)) {
        	BoilingPartIndex index = world.getBlockState(p).get(BoilingBlock.FORMED);
            return index == BoilingPartIndex.getIndex(dx, dy, dz);
        } else if (isFormedSuperchestPart(world, p)) {
        	BoilingPartIndex index = world.getBlockState(p).get(BoilingBlock.FORMED);
            return index == BoilingPartIndex.getIndex(dx, dy, dz);
        } else {
            // We can already stop here
            return false;
        }
    }

    private boolean isValidUnformedBlockPart(World world, BlockPos pos, int dx, int dy, int dz) {
        if (isUnformedSuperchestController(world, pos)) {
            return true;
        } else if (isUnformedSuperchestPart(world, pos)) {
            // We can already stop here
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public BlockPos getBottomLowerLeft(World world, BlockPos pos) {
        if (isBlockPart(world, pos)) {
            BlockState state = world.getBlockState(pos);
            BoilingPartIndex index = state.get(BoilingBlock.FORMED);
            return pos.add(-index.getDx(), -index.getDy(), -index.getDz());
        } else {
            return null;
        }
    }

    @Override
    public void unformBlock(World world, BlockPos pos) {
    	BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(BoilingBlock.FORMED, BoilingPartIndex.UNFORMED), 3);
    }

    @Override
    public void formBlock(World world, BlockPos pos, int dx, int dy, int dz) {
    	BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(BoilingBlock.FORMED, BoilingPartIndex.getIndex(dx, dy, dz)), 3);
    }

    @Override
    public boolean isValidUnformedMultiBlock(World world, BlockPos pos) {
        int cntSuper = 0;
        for (int dx = 0 ; dx < getWidth() ; dx++) {
            for (int dy = 0 ; dy < getHeight() ; dy++) {
                for (int dz = 0 ; dz < getDepth() ; dz++) {
                    BlockPos p = pos.add(dx, dy, dz);
                    if (!isValidUnformedBlockPart(world, p, dx, dy, dz)) {
                        return false;
                    }
                    if (world.getBlockState(p).getBlock() == BlockInit.boiling_block.get()) {
                        cntSuper++;
                    }
                }
            }
        }
        return cntSuper == 8;
    }

    @Override
    public boolean isValidFormedMultiBlock(World world, BlockPos pos) {
        int cntSuper = 0;
        for (int dx = 0; dx < getWidth(); dx++) {
            for (int dy = 0; dy < getHeight(); dy++) {
                for (int dz = 0; dz < getDepth(); dz++) {
                    BlockPos p = pos.add(dx, dy, dz);
                    if (!isValidFormedBlockPart(world, p, dx, dy, dz)) {
                        return false;
                    }
                    if (world.getBlockState(p).getBlock() == BlockInit.boiling_block.get()) {
                        cntSuper++;
                    }
                }
            }
        }
        return cntSuper == 8;
    }

    @Override
    public int getWidth() {
        return 2;
    }

    @Override
    public int getHeight() {
        return 2;
    }

    @Override
    public int getDepth() {
        return 2;
    }

    private static boolean isUnformedSuperchestController(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() == BlockInit.boiling_block.get() && state.get(BoilingBlock.FORMED) == BoilingPartIndex.UNFORMED;
    }

    public static boolean isFormedSuperchestController(World world, BlockPos pos) {
    	BlockState state = world.getBlockState(pos);
        return state.getBlock() == BlockInit.boiling_block.get() && state.get(BoilingBlock.FORMED) != BoilingPartIndex.UNFORMED;
    }

    private static boolean isUnformedSuperchestPart(World world, BlockPos pos) {
    	BlockState state = world.getBlockState(pos);
        return state.getBlock() == BlockInit.boiling_block.get() && state.get(BoilingBlock.FORMED) == BoilingPartIndex.UNFORMED;
    }

    private static boolean isFormedSuperchestPart(World world, BlockPos pos) {
    	BlockState state = world.getBlockState(pos);
        return state.getBlock() == BlockInit.boiling_block.get() && state.get(BoilingBlock.FORMED) != BoilingPartIndex.UNFORMED;
    }

}