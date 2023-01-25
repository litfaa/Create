package com.simibubi.create.gametest;

import java.util.List;

import org.jetbrains.annotations.Contract;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.logistics.block.belts.tunnel.BrassTunnelTileEntity.SelectionMode;
import com.simibubi.create.content.logistics.block.redstone.NixieTubeTileEntity;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.mixin.accessor.GameTestHelperAccessor;
import com.simibubi.create.foundation.tileEntity.IMultiTileContainer;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.RegisteredObjects;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

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
			fail("FACING property not in block: " + Registry.BLOCK.getId(original.getBlock()));
		Direction facing = original.getValue(BlockStateProperties.FACING);
		BlockState reversed = original.setValue(BlockStateProperties.FACING, facing.getOpposite());
		setBlock(pos, reversed);
	}

	public ItemEntity spawnItem(BlockPos pos, ItemStack stack) {
		Vec3 spawn = Vec3.atCenterOf(absolutePos(pos));
		ServerLevel level = getLevel();
		ItemEntity item = new ItemEntity(level, spawn.x, spawn.y, spawn.z, stack, 0, 0, 0);
		level.addFreshEntity(item);
		return item;
	}

	public FluidStack getTankContents(BlockPos tank) {
		Storage<FluidVariant> storage = fluidStorageAt(tank);
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

	public Storage<ItemVariant> itemStorageAt(BlockPos pos) {
		Storage<ItemVariant> storage = TransferUtil.getItemStorage(getLevel(), absolutePos(pos));
		if (storage == null)
			fail("Storage not present");
		return storage;
	}

	public long getTotalItems(BlockPos pos) {
		Storage<ItemVariant> storage = itemStorageAt(pos);
		return TransferUtil.getAllItems(storage).stream().mapToLong(ItemStack::getCount).sum();
	}

	public Storage<FluidVariant> fluidStorageAt(BlockPos pos) {
		Storage<FluidVariant> storage = TransferUtil.getFluidStorage(getLevel(), absolutePos(pos));
		if (storage == null)
			fail("Storage not present");
		return storage;
	}

	public <T extends BlockEntity> T getBlockEntity(BlockEntityType<T> type, BlockPos pos) {
		BlockEntity be = getBlockEntity(pos);
		BlockEntityType<?> actualType = be == null ? null : be.getType();
		if (actualType != type) {
			String actualId = actualType == null ? "null" : RegisteredObjects.getKeyOrThrow(actualType).toString();
			String error = "Expected block entity at pos [%s] with type [%s], got [%s]".formatted(
					pos, RegisteredObjects.getKeyOrThrow(type), actualId
			);
			fail(error);
		}
		return (T) be;
	}

	public <T extends BlockEntity & IMultiTileContainer> T getControllerBlockEntity(BlockEntityType<T> type, BlockPos anySegment) {
		T be = getBlockEntity(type, anySegment).getControllerTE();
		if (be == null)
			fail("Could not get block entity controller with type [%s] from pos [%s]".formatted(RegisteredObjects.getKeyOrThrow(type), anySegment));
		return be;
	}

	public void assertSecondsPassed(int seconds) {
		if (getTick() < seconds * 20L)
			fail("Waiting for %s seconds to pass".formatted(seconds));
	}

	public long secondsPassed() {
		return getTick() % 20;
	}

	public void assertAnyContained(BlockPos pos, Item... items) {
		Storage<ItemVariant> storage = itemStorageAt(pos);
		boolean anyFound = false;
		try (Transaction t = Transaction.openOuter()) {
			for (Item item : items) {
				anyFound |= storage.extract(ItemVariant.of(item), 1, t) != 0;
			}
		}
		if (!anyFound)
			fail("No mathing items found in storage at pos: " + pos);
	}

	public void assertAllStacksPresent(List<ItemStack> stacks, BlockPos pos) {
		Storage<ItemVariant> storage = itemStorageAt(pos);
		try (Transaction t = Transaction.openOuter()) {
			for (ItemStack stack : stacks) {
				long extracted = storage.extract(ItemVariant.of(stack), stack.getCount(), t);
				if (extracted != stack.getCount()) {
					ItemStack extractedStack = extracted == 0
							? ItemStack.EMPTY
							: ItemHandlerHelper.copyStackWithSize(stack, ItemHelper.truncateLong(extracted));
					fail("Item [%s] not extracted as expected; got [%s]".formatted(stack, extractedStack));
				}
			}
		}
	}

	public void assertFluidPresent(FluidStack fluid, BlockPos pos) {
		FluidStack contained = getTankContents(pos);
		if (!fluid.isFluidEqual(contained))
			fail("Different fluids");
		if (fluid.getAmount() != contained.getAmount())
			fail("Different amounts");
	}

	public void assertTankEmpty(BlockPos pos) {
		assertFluidPresent(FluidStack.EMPTY, pos);
	}

	public <T extends Entity> T getFirstEntity(EntityType<T> type, BlockPos pos) {
		List<T> list = getEntitiesBetween(type, pos.north().east().above(), pos.south().west().below());
		if (list.isEmpty())
			fail("No entities at pos: " + pos);
		return list.get(0);
	}

	public <T extends Entity> List<T> getEntitiesBetween(EntityType<T> type, BlockPos pos1, BlockPos pos2) {
		BoundingBox box = BoundingBox.fromCorners(absolutePos(pos1), absolutePos(pos2));
		List<? extends T> entities = getLevel().getEntities(type, e -> box.isInside(e.blockPosition()));
		return (List<T>) entities;
	}

	public void assertNixieRedstone(BlockPos pos, int strength) {
		NixieTubeTileEntity nixie = getBlockEntity(AllTileEntities.NIXIE_TUBE.get(), pos);
		int actualStrength = nixie.getRedstoneStrength();
		if (actualStrength != strength)
			fail("Expected nixie tube at %s to have power of %s, got %s".formatted(pos, strength, actualStrength));
	}

	public void assertCloseEnoughTo(double value, double expected) {
		assertInRange(value, expected - 1, expected + 1);
	}

	public void assertInRange(double value, double min, double max) {
		if (value < min)
			fail("Value %s below expected min of %s".formatted(value, min));
		if (value > max)
			fail("Value %s greater than expected max of %s".formatted(value, max));
	}

	public void whenSecondsPassed(int seconds, Runnable run) {
		runAfterDelay((long) seconds * CreateTestBase.TICKS_PER_SECOND, run);
	}

	public <T extends TileEntityBehaviour> T getBehavior(BlockPos pos, BehaviourType<T> type) {
		T behavior = TileEntityBehaviour.get(getLevel(), absolutePos(pos), type);
		if (behavior == null)
			fail("Behavior at " + pos + " missing, expected " + type.getName());
		return behavior;
	}

	public void setTunnelMode(BlockPos pos, SelectionMode mode) {
		ScrollValueBehaviour behavior = getBehavior(pos, ScrollOptionBehaviour.TYPE);
		behavior.setValue(mode.ordinal());
	}

	@Override
	public void assertContainerEmpty(BlockPos pos) {
		super.assertContainerEmpty(pos);
		// extra check for FAPI storages
		Storage<ItemVariant> storage = itemStorageAt(pos);
		try (Transaction t = Transaction.openOuter()) {
			if (!TransferUtil.extractAnyItem(storage, 1).isEmpty()) {
				fail("Storage not empty");
			}
		}
	}

	public void assertContainersEmpty(List<BlockPos> positions) {
		for (BlockPos pos : positions) {
			assertContainerEmpty(pos);
		}
	}

	@Contract("_->fail") // make IDEA happier
	@Override
	public void fail(String exceptionMessage) {
		super.fail(exceptionMessage);
	}

	// support FAPI storages

	@Override
	public void assertContainerContains(BlockPos pos, Item item) {
		assertContainerContains(pos, new ItemStack(item));
	}

	public void assertContainerContains(BlockPos pos, ItemLike item) {
		assertContainerContains(pos, item.asItem());
	}

	public void assertContainerContains(BlockPos pos, ItemVariant item) {
		assertContainerContains(pos, item.toStack());
	}

	public void assertContainerContains(BlockPos pos, ItemStack item) {
		Storage<ItemVariant> storage = itemStorageAt(pos);
		try (Transaction t = Transaction.openOuter()) {
			int count = item.getCount();
			long extracted = storage.extract(ItemVariant.of(item), count, t);
			if (extracted != count) {
				fail("Storage does not contain " + item.getItem());
			}
		}
	}
}
