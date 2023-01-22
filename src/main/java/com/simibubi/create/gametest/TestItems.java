package com.simibubi.create.gametest;

import java.util.List;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.block.depot.DepotTileEntity;
import com.simibubi.create.content.logistics.block.redstone.NixieTubeTileEntity;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TestItems extends CreateTestBase {
	public static final String PATH = Create.ID + ":items/";

	@GameTest(template = PATH + "arm_purgatory", timeoutTicks = TEN_SECONDS)
	public static void armPurgatory(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(2, 3, 2);
		BlockPos depot1Pos = new BlockPos(3, 2, 1);
		DepotTileEntity depot1 = helper.getBlockEntity(AllTileEntities.DEPOT.get(), depot1Pos);
		BlockPos depot2Pos = new BlockPos(1, 2, 1);
		DepotTileEntity depot2 = helper.getBlockEntity(AllTileEntities.DEPOT.get(), depot2Pos);
		helper.pullLever(lever);
		helper.succeedWhen(() -> {
			helper.assertSecondsPassed(5);
			ItemStack held1 = depot1.getHeldItem();
			boolean held1Empty = held1.isEmpty();
			int held1Count = held1.getCount();
			ItemStack held2 = depot2.getHeldItem();
			boolean held2Empty = held2.isEmpty();
			int held2Count = held2.getCount();
			if (held1Empty && held2Empty)
				helper.fail("No item present");
			if (!held1Empty && held1Count != 1)
				helper.fail("Unexpected count on depot 1: " + held1Count);
			if (!held2Empty && held2Count != 1)
				helper.fail("Unexpected count on depot 2: " + held2Count);
		});
	}

	@GameTest(template = PATH + "content_observer_counting")
	public static void contentObserverCounting(CreateGameTestHelper helper) {
		BlockPos chest = new BlockPos(3, 2, 1);
		long totalChestItems = helper.getTotalItems(chest);
		BlockPos chestNixiePos = new BlockPos(2, 3, 1);
		NixieTubeTileEntity chestNixie = helper.getBlockEntity(AllTileEntities.NIXIE_TUBE.get(), chestNixiePos);

		BlockPos doubleChest = new BlockPos(2, 2, 3);
		long totalDoubleChestItems = helper.getTotalItems(doubleChest);
		BlockPos doubleChestNixiePos = new BlockPos(2, 3, 1);
		NixieTubeTileEntity doubleChestNixie = helper.getBlockEntity(AllTileEntities.NIXIE_TUBE.get(), doubleChestNixiePos);

		helper.succeedWhen(() -> {
			String chestNixieText = chestNixie.getFullText().getString();
			long chestNixieReading = Long.parseLong(chestNixieText);
			if (chestNixieReading != totalChestItems)
				helper.fail("Chest nixie detected %s, expected %s".formatted(chestNixieReading, totalChestItems));
			String doubleChestNixieText = doubleChestNixie.getFullText().getString();
			long doubleChestNixieReading = Long.parseLong(doubleChestNixieText);
			if (doubleChestNixieReading != totalDoubleChestItems)
				helper.fail("Double chest nixie detected %s, expected %s".formatted(doubleChestNixieReading, totalDoubleChestItems));
		});
	}

	@GameTest(template = PATH + "storages", timeoutTicks = TEN_SECONDS)
	public static void storages(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(12, 3, 2);
		BlockPos startChest = new BlockPos(13, 3, 1);
		List<ItemStack> originalStacks;
		try (Transaction t = Transaction.openOuter()) {
			originalStacks = TransferUtil.extractAllAsStacks(helper.itemStorageAt(startChest));
		}
		BlockPos endShulker = new BlockPos(1, 3, 1);
		Storage<ItemVariant> endShulkerStorage = helper.itemStorageAt(endShulker);
		helper.pullLever(lever);
		helper.succeedWhen(() -> {
			try (Transaction t = TransferUtil.getTransaction()) {
				for (ItemStack stack : originalStacks) {
					int count = stack.getCount();
					long extracted = endShulkerStorage.extract(ItemVariant.of(stack), count, t);
					if (extracted != count) {
						helper.fail("Expected %s, got %s".formatted(stack, extracted));
					}
				}
			}
		});
	}

	@GameTest(template = PATH + "vault_comparator_output")
	public static void vaultComparatorOutput(CreateGameTestHelper helper) {
		BlockPos smallInput = new BlockPos(1, 3, 1).above();
		BlockPos smallNixie = new BlockPos(3, 2, 1);
		helper.assertNixieRedstone(smallNixie, 0);
		helper.spawnItem(smallInput, new ItemStack(Items.BREAD, 64 * 9));
		BlockPos medInput = new BlockPos(1, 4, 4).above();
		BlockPos medNixie = new BlockPos(4, 2, 4);
		helper.assertNixieRedstone(medNixie, 0);
		helper.spawnItem(medInput, new ItemStack(Items.BREAD, 1)); // todo count for power 7
		BlockPos bigInput = new BlockPos(1, 5, 8).above();
		BlockPos bigNixie = new BlockPos(5, 2, 7);
		helper.assertNixieRedstone(bigNixie, 0);
		helper.spawnItem(bigInput, new ItemStack(Items.BREAD, 1)); // todo count for power 7
		helper.succeedWhen(() -> {
			helper.assertNixieRedstone(smallNixie, 7);
			helper.assertNixieRedstone(medNixie, 7);
			helper.assertNixieRedstone(bigNixie, 7);
		});
	}
}
