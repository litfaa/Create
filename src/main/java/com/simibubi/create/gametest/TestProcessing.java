package com.simibubi.create.gametest;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public class TestProcessing extends CreateTestBase {
	public static final String PATH = Create.ID + ":processing/";

	@GameTest(template = PATH + "brass_mixing", timeoutTicks = TEN_SECONDS)
	public static void brassMixing(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(2, 3, 2);
		BlockPos chest = new BlockPos(7, 3, 1);
		helper.pullLever(lever);
		helper.succeedWhen(() -> helper.assertContainerContains(chest, AllItems.BRASS_INGOT.get()));
	}

	@GameTest(template = PATH + "brass_mixing_2", timeoutTicks = TWENTY_SECONDS)
	public static void brassMixing2(CreateGameTestHelper helper) {
		BlockPos basinLever = new BlockPos(3, 3, 1);
		BlockPos armLever = new BlockPos(3, 3, 5);
		BlockPos output = new BlockPos(1, 2, 3);
		helper.pullLever(armLever);
		helper.whenSecondsPassed(7, () -> helper.pullLever(armLever));
		helper.whenSecondsPassed(10, () -> helper.pullLever(basinLever));
		helper.succeedWhen(() -> helper.assertContainerContains(output, AllItems.BRASS_INGOT.get()));
	}

	@GameTest(template = PATH + "precision_mechanism_crafting", timeoutTicks = TWENTY_SECONDS)
	public static void precisionMechanismCrafting(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(2, 5, 4);
		BlockPos output = new BlockPos(7, 2, 1);
		helper.pullLever(lever);
		helper.succeedWhen(() -> helper.assertContainerContains(output, AllItems.PRECISION_MECHANISM.get()));
	}

	@GameTest(template = PATH + "water_filling_bottle")
	public static void waterFillingBottle(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(2, 3, 2);
		BlockPos spawn = new BlockPos(1, 2, 1);
		BlockPos output = new BlockPos(1, 2, 4);
		ItemVariant waterBottle = ItemVariant.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
		helper.pullLever(lever);
		helper.whenSecondsPassed(1, () -> helper.spawnItem(spawn, Items.GLASS_BOTTLE.getDefaultInstance()));
		helper.succeedWhen(() -> helper.assertContainerContains(output, waterBottle));
	}

	@GameTest(template = PATH + "sand_washing", timeoutTicks = TEN_SECONDS)
	public static void sandWashing(CreateGameTestHelper helper) {
		BlockPos leverPos = new BlockPos(5, 3, 1);
		helper.pullLever(leverPos);
		BlockPos chestPos = new BlockPos(8, 3, 2);
		helper.succeedWhen(() -> helper.assertContainerContains(chestPos, Items.CLAY_BALL));
	}
}
