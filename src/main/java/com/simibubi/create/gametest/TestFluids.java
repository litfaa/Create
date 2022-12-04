package com.simibubi.create.gametest;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.fluids.tank.FluidTankTileEntity;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

public class TestFluids extends CreateTestBase {
	public static final String PATH = Create.ID + ":fluids/";

	@GameTest(template = PATH + "3_pipe_combine", timeoutTicks = 20 * 20)
	public static void threePipeCombine(CreateGameTestHelper helper) {
		BlockPos tank1Pos = new BlockPos(5, 2, 1);
		BlockPos tank2Pos = tank1Pos.south();
		BlockPos tank3Pos = tank2Pos.south();
		long totalContents = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos);

		BlockPos pumpPos = new BlockPos(2, 2, 2);
		helper.flipBlock(pumpPos);
		helper.succeedWhen(() -> {
			if (helper.getTick() < 13 * 20) { // wait 13 sec to allow filling to finish
				helper.fail("waiting");
			}
			BlockPos outputTankPos = new BlockPos(1, 2, 2);
			FluidTankTileEntity tank = (FluidTankTileEntity) helper.getBlockEntity(outputTankPos);
			Storage<FluidVariant> storage = tank.getFluidStorage(null);
			long moved = helper.getTankContents(outputTankPos).getAmount();
			long capacity = TransferUtil.totalCapacity(storage);
			// verify tank is correctly filled
			if (moved != capacity) {
				helper.fail("tank not full [%s/%s]".formatted(moved, capacity));
			}
			// verify nothing was duped or deleted
			long remaining = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos);
			long newTotalContents = moved + remaining;
			if (newTotalContents != totalContents) {
				helper.fail("Wrong total fluid amount. expected [%s], got [%s]".formatted(totalContents, newTotalContents));
			}
		});
	}

	@GameTest(template = PATH + "3_pipe_split", timeoutTicks = 20 * 20)
	public static void threePipeSplit(CreateGameTestHelper helper) {
		BlockPos pumpPos = new BlockPos(2, 2, 2);
		BlockPos tank1Pos = new BlockPos(5, 2, 1);
		BlockPos tank2Pos = tank1Pos.south();
		BlockPos tank3Pos = tank2Pos.south();
		BlockPos outputTankPos = new BlockPos(1, 2, 2);

		long totalContents = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos, outputTankPos);
		helper.flipBlock(pumpPos);

		helper.succeedWhen(() -> {
			if (helper.getTick() < 13 * 20) { // wait 13 sec to allow filling to finish
				helper.fail("waiting");
			}
			FluidStack contents = helper.getTankContents(outputTankPos);
			if (!contents.isEmpty()) {
				helper.fail("Tank not empty: " + contents.getAmount());
			}
			long newTotalContents = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos);
			if (newTotalContents != totalContents) {
				helper.fail("Wrong total fluid amount. expected [%s], got [%s]".formatted(totalContents, newTotalContents));
			}
		});
	}

	@GameTest(template = PATH + "hose_pulley_transfer", timeoutTicks = 20 * 20)
	public static void hosePulleyTransfer(CreateGameTestHelper helper) {
		// there was supposed to be redstone here built in, but it kept popping off, so put it there manually
		BlockPos brokenRedstone = new BlockPos(4, 8, 3);
		BlockState redstone = Blocks.REDSTONE_WIRE.defaultBlockState()
				.setValue(RedStoneWireBlock.NORTH, RedstoneSide.NONE)
				.setValue(RedStoneWireBlock.SOUTH, RedstoneSide.NONE)
				.setValue(RedStoneWireBlock.EAST, RedstoneSide.UP)
				.setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE)
				.setValue(RedStoneWireBlock.POWER, 14);
		helper.setBlock(brokenRedstone, redstone);
		// pump
		BlockPos lever = new BlockPos(6, 9, 3);
		helper.pullLever(lever);
		helper.succeedWhen(() -> {
			if (helper.getTick() < 15 * 20) { // wait 15 sec to allow filling to finish
				helper.fail("waiting");
			}
			// check filled
			BlockPos filledLowerCorner = new BlockPos(8, 3, 2);
			BlockPos filledUpperCorner = new BlockPos(10, 5, 4);
			BlockPos.betweenClosed(filledLowerCorner, filledUpperCorner)
					.forEach(pos -> helper.assertBlockPresent(Blocks.WATER, pos));
			// check emptied
			BlockPos emptiedLowerCorner = new BlockPos(2, 3, 2);
			BlockPos emptiedUpperCorner = new BlockPos(4, 5, 4);
			BlockPos.betweenClosed(emptiedLowerCorner, emptiedUpperCorner)
					.forEach(pos -> helper.assertBlockPresent(Blocks.AIR, pos));
			// check nothing left in pulley
			BlockPos pulleyPos = new BlockPos(8, 7, 4);
			FluidStack contents = helper.getTankContents(pulleyPos);
			if (!contents.isEmpty()) {
				helper.fail("Pulley not empty: " + contents.getAmount());
			}
		});
	}

	@GameTest(template = PATH + "in_world_pumping_in")
	public static void inWorldPumpingPickup(CreateGameTestHelper helper) {
		BlockPos pumpPos = new BlockPos(3, 2, 2);
		BlockPos basinPos = pumpPos.east();
		BlockPos waterPos = pumpPos.west();
		helper.flipBlock(pumpPos);
		helper.succeedWhen(() -> {
			if (!helper.getBlockState(waterPos).isAir()) {
				helper.fail("Water not collected");
			}
			BasinTileEntity basin = (BasinTileEntity) helper.getBlockEntity(basinPos);
			long amount = basin.inputTank.getPrimaryHandler().amount;
			if (amount != FluidConstants.BUCKET) {
				helper.fail("Incorrect amount of water collected: " + amount);
			}
		});
	}

	@GameTest(template = PATH + "in_world_pumping_out")
	public static void inWorldPumpingOutput(CreateGameTestHelper helper) {
		BlockPos pumpPos = new BlockPos(3, 2, 2);
		BlockPos waterPos = pumpPos.west();
		BlockPos basinPos = pumpPos.east();
		helper.flipBlock(pumpPos);
		helper.succeedWhen(() -> {
			if (!helper.getBlockState(waterPos).is(Blocks.WATER)) {
				helper.fail("Water not dispensed");
			}

			BasinTileEntity basin = (BasinTileEntity) helper.getBlockEntity(basinPos);
			SmartFluidTank tank = basin.inputTank.getPrimaryHandler();
			if (tank.amount != 0) {
				helper.fail("Incorrect amount of water remaining: " + tank.amount);
			}
		});
	}
}
