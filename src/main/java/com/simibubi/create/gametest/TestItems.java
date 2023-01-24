package com.simibubi.create.gametest;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.block.belts.tunnel.BrassTunnelTileEntity.SelectionMode;
import com.simibubi.create.content.logistics.block.depot.DepotTileEntity;
import com.simibubi.create.content.logistics.block.redstone.NixieTubeTileEntity;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplayLayout;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplaySection;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplayTileEntity;
import com.simibubi.create.foundation.utility.Components;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;

public class TestItems extends CreateTestBase {
	public static final String PATH = Create.ID + ":items/";

	@GameTest(template = PATH + "andesite_tunnel_split")
	public static void andesiteTunnelSplit(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(2, 6, 2);
		helper.pullLever(lever);
		Map<BlockPos, ItemStack> outputs = Map.of(
				new BlockPos(2, 2, 1), new ItemStack(AllItems.BRASS_INGOT.get(), 1),
				new BlockPos(3, 2, 1), new ItemStack(AllItems.BRASS_INGOT.get(), 1),
				new BlockPos(4, 2, 2), new ItemStack(AllItems.BRASS_INGOT.get(), 3)
		);
		helper.succeedWhen(() -> outputs.forEach(helper::assertContainerContains));
	}

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

	@GameTest(template = PATH + "attribute_filters", timeoutTicks = TEN_SECONDS)
	public static void attributeFilters(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(2, 3, 1);
		BlockPos end = new BlockPos(11, 2, 2);
		Map<BlockPos, ItemStack> outputs = Map.of(
				new BlockPos(3, 2, 1), new ItemStack(AllBlocks.BRASS_BLOCK.get()),
				new BlockPos(4, 2, 1), new ItemStack(Items.APPLE),
				new BlockPos(5, 2, 1), new ItemStack(Items.WATER_BUCKET),
				new BlockPos(6, 2, 1), EnchantedBookItem.createForEnchantment(
						new EnchantmentInstance(Enchantments.ALL_DAMAGE_PROTECTION, 1)
				),
				new BlockPos(7, 2, 1), Util.make(
						new ItemStack(Items.NETHERITE_SWORD),
						s -> s.setDamageValue(1)
				),
				new BlockPos(8, 2, 1), new ItemStack(Items.IRON_HELMET),
				new BlockPos(9, 2, 1), new ItemStack(Items.COAL),
				new BlockPos(10, 2, 1), new ItemStack(Items.POTATO)
		);
		helper.pullLever(lever);
		helper.succeedWhen(() -> {
			outputs.forEach(helper::assertContainerContains);
			helper.assertContainerEmpty(end);
		});
	}

	@GameTest(template = PATH + "belt_coaster", timeoutTicks = TEN_SECONDS)
	public static void beltCoaster(CreateGameTestHelper helper) {
		BlockPos input = new BlockPos(1, 5, 6);
		BlockPos output = new BlockPos(3, 8, 6);
		BlockPos lever = new BlockPos(1, 5, 5);
		helper.pullLever(lever);
		helper.succeedWhen(() -> {
			long outputItems = helper.getTotalItems(output);
			if (outputItems != 27)
				helper.fail("Expected 27 items, got " + outputItems);
			long remainingItems = helper.getTotalItems(input);
			if (remainingItems != 2)
				helper.fail("Expected 2 items remaining, got " + remainingItems);
		});
	}

	@GameTest(template = PATH + "brass_tunnel_filtering")
	public static void brassTunnelFiltering(CreateGameTestHelper helper) {
		Map<BlockPos, ItemStack> outputs = Map.of(
				new BlockPos(3, 2, 2), new ItemStack(Items.COPPER_INGOT, 13),
				new BlockPos(4, 2, 3), new ItemStack(AllItems.ZINC_INGOT.get(), 4),
				new BlockPos(4, 2, 4), new ItemStack(Items.IRON_INGOT, 2),
				new BlockPos(4, 2, 5), new ItemStack(Items.GOLD_INGOT, 24),
				new BlockPos(3, 2, 6), new ItemStack(Items.DIAMOND, 17)
		);
		BlockPos lever = new BlockPos(2, 3, 2);
		helper.pullLever(lever);
		helper.succeedWhen(() -> outputs.forEach(helper::assertContainerContains));
	}

	@GameTest(template = PATH + "brass_tunnel_prefer_nearest", timeoutTicks = TEN_SECONDS)
	public static void brassTunnelPreferNearest(CreateGameTestHelper helper) {
		List<BlockPos> tunnels = List.of(
				new BlockPos(3, 3, 1),
				new BlockPos(3, 3, 2),
				new BlockPos(3, 3, 3)
		);
		List<BlockPos> out = List.of(
				new BlockPos(5, 2, 1),
				new BlockPos(5, 2, 2),
				new BlockPos(5, 2, 3)
		);
		BlockPos lever = new BlockPos(2, 3, 2);
		helper.pullLever(lever);
		// tunnels reconnect and lose their modes
		tunnels.forEach(tunnel -> helper.setTunnelMode(tunnel, SelectionMode.PREFER_NEAREST));
		helper.succeedWhen(() ->
				out.forEach(pos ->
						helper.assertContainerContains(pos, AllBlocks.BRASS_CASING.get())
				)
		);
	}

	@GameTest(template = PATH + "brass_tunnel_round_robin", timeoutTicks = TEN_SECONDS)
	public static void brassTunnelRoundRobin(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(2, 3, 2);
		List<BlockPos> tunnels = List.of(
				new BlockPos(3, 3, 1),
				new BlockPos(3, 3, 2),
				new BlockPos(3, 3, 3)
		);
		List<BlockPos> outputs = List.of(
				new BlockPos(7, 3, 1),
				new BlockPos(7, 3, 2),
				new BlockPos(7, 3, 3)
		);
		helper.pullLever(lever);
		tunnels.forEach(pos -> helper.setTunnelMode(pos, SelectionMode.ROUND_ROBIN));
		helper.succeedWhen(() -> {
			long items = 0;
			for (BlockPos out : outputs) {
				helper.assertContainerContains(out, AllBlocks.BRASS_CASING.get());
				items += helper.getTotalItems(out);
			}
			if (items != 10)
				helper.fail("expected 10 items, got " + items);
		});
	}

	@GameTest(template = PATH + "brass_tunnel_split")
	public static void brassTunnelSplit(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(2, 3, 2);
		List<BlockPos> tunnels = List.of(
				new BlockPos(3, 3, 1),
				new BlockPos(3, 3, 2),
				new BlockPos(3, 3, 3)
		);
		List<BlockPos> outputs = List.of(
				new BlockPos(7, 2, 1),
				new BlockPos(7, 2, 2),
				new BlockPos(7, 2, 3)
		);
		helper.pullLever(lever);
		tunnels.forEach(tunnel -> helper.setTunnelMode(tunnel, SelectionMode.SPLIT));
		helper.succeedWhen(() -> {
			long items = 0;
			for (BlockPos out : outputs) {
				helper.assertContainerContains(out, AllBlocks.BRASS_CASING.get());
				items += helper.getTotalItems(out);
			}
			if (items != 10)
				helper.fail("expected 10 items, got " + items);
		});
	}

	@GameTest(template = PATH + "brass_tunnel_sync_input", timeoutTicks = TEN_SECONDS)
	public static void brassTunnelSyncInput(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(1, 3, 2);
		List<BlockPos> redstoneBlocks = List.of(
				new BlockPos(3, 4, 1),
				new BlockPos(3, 4, 2),
				new BlockPos(3, 4, 3)
		);
		List<BlockPos> tunnels = List.of(
				new BlockPos(5, 3, 1),
				new BlockPos(5, 3, 2),
				new BlockPos(5, 3, 3)
		);
		List<BlockPos> outputs = List.of(
				new BlockPos(7, 2, 1),
				new BlockPos(7, 2, 2),
				new BlockPos(7, 2, 3)
		);
		helper.pullLever(lever);
		tunnels.forEach(tunnel -> helper.setTunnelMode(tunnel, SelectionMode.SYNCHRONIZE));
		helper.succeedWhen(() -> {
			if (helper.secondsPassed() < 9) {
				helper.setBlock(redstoneBlocks.get(0), Blocks.AIR);
				helper.assertSecondsPassed(3);
				outputs.forEach(helper::assertContainerEmpty);
				helper.setBlock(redstoneBlocks.get(1), Blocks.AIR);
				helper.assertSecondsPassed(6);
				outputs.forEach(helper::assertContainerEmpty);
				helper.setBlock(redstoneBlocks.get(2), Blocks.AIR);
				helper.assertSecondsPassed(9);
			} else {
				outputs.forEach(out -> helper.assertContainerContains(out, AllBlocks.BRASS_CASING.get()));
			}
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

	@GameTest(template = PATH + "depot_display", timeoutTicks = TEN_SECONDS)
	public static void depotDisplay(CreateGameTestHelper helper) {
		BlockPos displayPos = new BlockPos(5, 3, 1);
		List<DepotTileEntity> depots = Stream.of(
				new BlockPos(2, 2, 1),
				new BlockPos(1, 2, 1)
		).map(pos -> helper.getBlockEntity(AllTileEntities.DEPOT.get(), pos)).toList();
		List<BlockPos> levers = List.of(
				new BlockPos(2, 5, 0),
				new BlockPos(1, 5, 0)
		);
		levers.forEach(helper::pullLever);
		FlapDisplayTileEntity display = helper.getBlockEntity(AllTileEntities.FLAP_DISPLAY.get(), displayPos).getController();
		helper.succeedWhen(() -> {
			for (int i = 0; i < 2; i++) {
				FlapDisplayLayout line = display.getLines().get(i);
				MutableComponent textComponent = Components.empty();
				line.getSections().stream().map(FlapDisplaySection::getText).forEach(textComponent::append);
				String text = textComponent.getString().toLowerCase(Locale.ROOT).trim();

				DepotTileEntity depot = depots.get(i);
				ItemStack item = depot.getHeldItem();
				String name = Registry.ITEM.getKey(item.getItem()).getPath();

				if (!name.equals(text))
					helper.fail("Text mismatch: wanted [" + name + "], got: " + text);
			}
		});
	}

	@GameTest(template = PATH + "stockpile_switch")
	public static void stockpileSwitch(CreateGameTestHelper helper) {
		BlockPos chest = new BlockPos(1, 2, 1);
		BlockPos lamp = new BlockPos(2, 3, 1);
		helper.assertBlockProperty(lamp, RedstoneLampBlock.LIT, false);
		Storage<ItemVariant> chestStorage = helper.itemStorageAt(chest);
		TransferUtil.insert(chestStorage, ItemVariant.of(Items.DIAMOND), 18 * 64);
		helper.succeedWhen(() -> helper.assertBlockProperty(lamp, RedstoneLampBlock.LIT, true));
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
