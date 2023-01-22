package com.simibubi.create.gametest;

import com.simibubi.create.Create;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.item.Items;

public class TestProcessing extends CreateTestBase {
	public static final String PATH = Create.ID + ":processing/";

	@GameTest(template = PATH + "brass_mixing")
	public static void brassMixing(CreateGameTestHelper helper) {
		helper.fail("NYI");
	}

	@GameTest(template = PATH + "brass_mixing_2")
	public static void brassMixing2(CreateGameTestHelper helper) {
		helper.fail("NYI");
	}

	@GameTest(template = PATH + "precision_mechanism_crafting")
	public static void precisionMechanismCrafting(CreateGameTestHelper helper) {
		helper.fail("NYI");
	}

	@GameTest(template = PATH + "water_filling_bottle")
	public static void waterFillingBottle(CreateGameTestHelper helper) {
		helper.fail("NYI");
	}

	@GameTest(template = PATH + "sand_washing", timeoutTicks = TEN_SECONDS)
	public static void sandWashing(CreateGameTestHelper helper) {
		BlockPos leverPos = new BlockPos(5, 3, 1);
		helper.pullLever(leverPos);
		BlockPos chestPos = new BlockPos(8, 3, 2);
		helper.succeedWhen(() -> {
			helper.assertContainerContains(chestPos, Items.CLAY);
		});
	}
}
