package com.simibubi.create.gametest;

import com.simibubi.create.foundation.mixin.accessor.GameTestHelperAccessor;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * An extension to {@link GameTestHelper} with added utilities.
 * To use, make your test class extend {@link CreateTestBase} and directly replace {@link GameTestHelper} params.
 */
public class CreateGameTestHelper extends GameTestHelper {
	private CreateGameTestHelper(GameTestInfo testInfo) {
		super(testInfo);
	}

	public static CreateGameTestHelper make(GameTestHelper original) {
		if (original instanceof GameTestHelperAccessor access) {
			CreateGameTestHelper helper = new CreateGameTestHelper(access.create$testInfo());
			if (helper instanceof GameTestHelperAccessor newAccess) {
				boolean finalCheckAdded = access.create$finalCheckAdded();
				newAccess.create$finalCheckAdded(finalCheckAdded);
				return helper;
			}
		}
		throw new IllegalStateException("Accessor not applied");
	}

	/**
	 * Flip the direction of any block with the {@link BlockStateProperties#FACING} property.
	 */
	public void flipBlock(BlockPos pos) {
		BlockState original = getBlockState(pos);
		if (!original.hasProperty(BlockStateProperties.FACING))
			throw new GameTestAssertException("FACING property not in block: " + Registry.BLOCK.getId(original.getBlock()));
		Direction facing = original.getValue(BlockStateProperties.FACING);
		BlockState reversed = original.setValue(BlockStateProperties.FACING, facing.getOpposite());
		setBlock(pos, reversed);
	}

	public FluidStack getTankContents(BlockPos tank) {
		BlockPos pos = absolutePos(tank);
		Storage<FluidVariant> storage = TransferUtil.getFluidStorage(getLevel(), pos);
		if (storage == null)
			throw new GameTestAssertException("No fluid storage at pos: " + pos);
		return TransferUtil.simulateExtractAnyFluid(storage, Long.MAX_VALUE);
	}

	/**
	 * Get the total fluid amount across all fluid tanks at the given positions.
	 */
	public long getFluidInTanks(BlockPos... tanks) {
		long total = 0;
		for (BlockPos tank : tanks) {
			total += getTankContents(tank).getAmount();
		}
		return total;
	}
}
