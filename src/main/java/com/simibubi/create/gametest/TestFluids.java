package com.simibubi.create.gametest;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.fluids.actors.HosePulleyFluidHandler;
import com.simibubi.create.content.contraptions.relays.gauge.SpeedGaugeTileEntity;
import com.simibubi.create.content.contraptions.relays.gauge.StressGaugeTileEntity;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.material.Fluids;

public class TestFluids extends CreateTestBase {
	public static final String PATH = Create.ID + ":fluids/";

	@GameTest(template = PATH + "hose_pulley_transfer", timeoutTicks = TWENTY_SECONDS)
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
			helper.assertSecondsPassed(15);
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
			Storage<FluidVariant> storage = helper.fluidStorageAt(pulleyPos);
			if (storage instanceof HosePulleyFluidHandler hose) {
				StorageView<FluidVariant> internalTank = hose.getUnderlyingView();
				if (!internalTank.isResourceBlank())
					helper.fail("Pulley not empty");
			} else {
				helper.fail("Not a pulley");
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
			helper.assertBlockPresent(Blocks.WATER, waterPos);
			helper.assertTankEmpty(basinPos);
		});
	}

	@GameTest(template = PATH + "in_world_pumping_in")
	public static void inWorldPumpingPickup(CreateGameTestHelper helper) {
		BlockPos pumpPos = new BlockPos(3, 2, 2);
		BlockPos basinPos = pumpPos.east();
		BlockPos waterPos = pumpPos.west();
		FluidStack expectedResult = new FluidStack(Fluids.WATER, FluidConstants.BUCKET);
		helper.flipBlock(pumpPos);
		helper.succeedWhen(() -> {
			helper.assertBlockPresent(Blocks.AIR, waterPos);
			helper.assertFluidPresent(expectedResult, basinPos);
		});
	}

	@GameTest(template = PATH + "steam_engine")
	public static void steamEngine(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(4, 3, 3);
		helper.pullLever(lever);
		BlockPos stressometer = new BlockPos(5, 2, 5);
		BlockPos speedometer = new BlockPos(4, 2, 5);
		helper.succeedWhen(() -> {
			StressGaugeTileEntity stress = helper.getBlockEntity(AllTileEntities.STRESSOMETER.get(), stressometer);
			SpeedGaugeTileEntity speed = helper.getBlockEntity(AllTileEntities.SPEEDOMETER.get(), speedometer);
			float capacity = stress.getNetworkCapacity();
			helper.assertCloseEnoughTo(capacity, 2048);
			float rotationSpeed = Mth.abs(speed.getSpeed());
			helper.assertCloseEnoughTo(rotationSpeed, 16);
		});
	}

	@GameTest(template = PATH + "3_pipe_combine", timeoutTicks = TWENTY_SECONDS)
	public static void threePipeCombine(CreateGameTestHelper helper) {
		BlockPos tank1Pos = new BlockPos(5, 2, 1);
		BlockPos tank2Pos = tank1Pos.south();
		BlockPos tank3Pos = tank2Pos.south();
		long totalContents = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos);

		BlockPos pumpPos = new BlockPos(2, 2, 2);
		helper.flipBlock(pumpPos);
		helper.succeedWhen(() -> {
			helper.assertSecondsPassed(13);
			BlockPos outputTankPos = new BlockPos(1, 2, 2);
			Storage<FluidVariant> storage = helper.fluidStorageAt(outputTankPos);
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

	@GameTest(template = PATH + "3_pipe_split", timeoutTicks = TWENTY_SECONDS)
	public static void threePipeSplit(CreateGameTestHelper helper) {
		BlockPos pumpPos = new BlockPos(2, 2, 2);
		BlockPos tank1Pos = new BlockPos(5, 2, 1);
		BlockPos tank2Pos = tank1Pos.south();
		BlockPos tank3Pos = tank2Pos.south();
		BlockPos outputTankPos = new BlockPos(1, 2, 2);

		long totalContents = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos, outputTankPos);
		helper.flipBlock(pumpPos);

		helper.succeedWhen(() -> {
			helper.assertSecondsPassed(13);
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
}
