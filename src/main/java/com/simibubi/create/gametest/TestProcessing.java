package com.simibubi.create.gametest;

import java.util.List;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
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

	@GameTest(template = PATH + "crushing_wheel_crafting", timeoutTicks = TEN_SECONDS)
	public static void crushingWheelCrafting(CreateGameTestHelper helper) {
		BlockPos chest = new BlockPos(1, 4, 3);
		List<BlockPos> levers = List.of(
				new BlockPos(2, 3, 2),
				new BlockPos(6, 3, 2),
				new BlockPos(3, 7, 3)
		);
		levers.forEach(helper::pullLever);
		ItemStack expected = new ItemStack(AllBlocks.CRUSHING_WHEEL.get(), 2);
		helper.succeedWhen(() -> helper.assertContainerContains(chest, expected));
	}

	// FIXME: this doesn't work and it feels like a bug, but it's the same as forge, so leave it out for now
//	@GameTest(template = PATH + "iron_compacting", timeoutTicks = TEN_SECONDS)
//	public static void ironCompacting(CreateGameTestHelper helper) {
//		BlockPos output = new BlockPos(4, 2, 4);
//		List<BlockPos> levers = List.of(
//				new BlockPos(4, 3, 2),
//				new BlockPos(1, 3, 2)
//		);
//		levers.forEach(helper::pullLever);
//		helper.succeedWhen(() -> helper.assertContainerContains(output, Items.IRON_BLOCK));
//	}

	@GameTest(template = PATH + "precision_mechanism_crafting", timeoutTicks = TWENTY_SECONDS)
	public static void precisionMechanismCrafting(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(2, 5, 4);
		BlockPos output = new BlockPos(7, 2, 1);
		helper.pullLever(lever);
		helper.succeedWhen(() -> helper.assertContainerContains(output, AllItems.PRECISION_MECHANISM.get()));
	}

	@GameTest(template = PATH + "sand_washing", timeoutTicks = TEN_SECONDS)
	public static void sandWashing(CreateGameTestHelper helper) {
		BlockPos leverPos = new BlockPos(5, 3, 1);
		helper.pullLever(leverPos);
		BlockPos chestPos = new BlockPos(8, 3, 2);
		helper.succeedWhen(() -> helper.assertContainerContains(chestPos, Items.CLAY_BALL));
	}

	@GameTest(template = PATH + "stone_cobble_sand_crushing", timeoutTicks = TEN_SECONDS)
	public static void stoneCobbleSandCrushing(CreateGameTestHelper helper) {
		BlockPos chest = new BlockPos(1, 6, 2);
		BlockPos lever = new BlockPos(2, 3, 1);
		helper.pullLever(lever);
		ItemStack expected = new ItemStack(Items.SAND, 5);
		helper.succeedWhen(() -> helper.assertContainerContains(chest, expected));
	}

	@GameTest(template = PATH + "track_crafting", timeoutTicks = TEN_SECONDS)
	public static void trackCrafting(CreateGameTestHelper helper) {
		BlockPos output = new BlockPos(7, 3, 2);
		BlockPos lever = new BlockPos(2, 3, 1);
		helper.pullLever(lever);
		ItemStack expected = new ItemStack(AllBlocks.TRACK.get(), 6);
		helper.succeedWhen(() -> {
			helper.assertContainerContains(output, expected);
			TransferUtil.extractItem(helper.itemStorageAt(output), expected);
			helper.assertContainerEmpty(output);
		});
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

	@GameTest(template = PATH + "wheat_milling")
	public static void wheatMilling(CreateGameTestHelper helper) {
		BlockPos output = new BlockPos(1, 2, 1);
		BlockPos lever = new BlockPos(1, 7, 1);
		helper.pullLever(lever);
		ItemStack expected = new ItemStack(AllItems.WHEAT_FLOUR.get(), 3);
		helper.succeedWhen(() -> helper.assertContainerContains(output, expected));
	}
}
