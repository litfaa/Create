package com.simibubi.create.gametest;

import java.util.List;

import com.simibubi.create.Create;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LeverBlock;

public class TestContraptions extends CreateTestBase {
	public static final String PATH = Create.ID + ":contraptions/";

	@GameTest(template = PATH + "arrow_dispenser", timeoutTicks = TEN_SECONDS)
	public static void arrowDispenser(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(2, 3, 1);
		helper.pullLever(lever);
		BlockPos pos1 = new BlockPos(0, 5, 0);
		BlockPos pos2 = new BlockPos(4, 5, 4);
		helper.succeedWhen(() -> {
			helper.assertSecondsPassed(7);
			List<Arrow> arrows = helper.getEntitiesBetween(EntityType.ARROW, pos1, pos2);
			if (arrows.size() != 4)
				helper.fail("Expected 4 arrows");
			helper.pullLever(lever); // disassemble contraption
			BlockPos dispenser = new BlockPos(2, 5, 2);
			// there should be 1 left over
			helper.assertContainerContains(dispenser, Items.ARROW);
		});
	}

	@GameTest(template = PATH + "crop_farming", timeoutTicks = TEN_SECONDS)
	public static void cropFarming(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(4, 3, 1);
		helper.pullLever(lever);
		BlockPos output = new BlockPos(1, 3, 12);
		helper.succeedWhen(() -> {
			helper.assertAnyContained(output, Items.WHEAT, Items.POTATO, Items.CARROT);
		});
	}

	@GameTest(template = PATH + "mounted_item_extract", timeoutTicks = TWENTY_SECONDS)
	public static void mountedItemExtract(CreateGameTestHelper helper) {
		BlockPos barrel = new BlockPos(1, 3, 2);
		List<ItemStack> originalStacks;
		try (Transaction t = Transaction.openOuter()) {
			originalStacks = TransferUtil.extractAllAsStacks(helper.itemStorageAt(barrel));
		}
		BlockPos lever = new BlockPos(1, 5, 1);
		helper.pullLever(lever);
		BlockPos outputPos = new BlockPos(4, 2, 1);
		helper.succeedWhen(() -> {
			helper.assertAllStacksPresent(originalStacks, outputPos); // verify all extracted
			if (!helper.getBlockState(lever).getValue(LeverBlock.POWERED)) {
				helper.pullLever(lever); // disassemble contraption
			}
			helper.assertContainerEmpty(barrel); // verify nothing left
		});
	}

	@GameTest(template = PATH + "mounted_fluid_drain", timeoutTicks = TEN_SECONDS)
	public static void mountedFluidDrain(CreateGameTestHelper helper) {
		BlockPos tank = new BlockPos(1, 3, 2);
		FluidStack fluid = helper.getTankContents(tank);
		BlockPos lever = new BlockPos(1, 5, 1);
		helper.pullLever(lever);
		BlockPos output = new BlockPos(4, 2, 1);
		helper.succeedWhen(() -> {
			helper.assertFluidPresent(fluid, output); // verify all extracted
			if (!helper.getBlockState(lever).getValue(LeverBlock.POWERED)) {
				helper.pullLever(lever); // disassemble contraption
			}
			helper.assertTankEmpty(tank); // verify nothing left
		});
	}
}
