package com.simibubi.create.gametest;

import com.simibubi.create.Create;
import com.simibubi.create.content.curiosities.deco.PlacardTileEntity;
import com.simibubi.create.content.schematics.block.SchematicannonTileEntity;
import com.simibubi.create.content.schematics.block.SchematicannonTileEntity.State;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

public class TestMisc extends CreateTestBase {
	public static final String PATH = Create.ID + ":misc/";

	@GameTest(template = PATH + "schematicannon", timeoutTicks = 15 * 20)
	public static void schematicannon(CreateGameTestHelper helper) {
		// get schematic
		BlockPos placardPos = new BlockPos(3, 3, 7);
		PlacardTileEntity placard = (PlacardTileEntity) helper.getBlockEntity(placardPos);
		ItemStack schematic = placard.getHeldItem().copy();
		// deploy to pos
		BlockPos anchor = helper.absolutePos(new BlockPos(1, 2, 1));
		schematic.getOrCreateTag().putBoolean("Deployed", true);
		schematic.getOrCreateTag().put("Anchor", NbtUtils.writeBlockPos(anchor));
		// setup cannon
		BlockPos cannonPos = new BlockPos(3, 2, 6);
		SchematicannonTileEntity cannon = (SchematicannonTileEntity) helper.getBlockEntity(cannonPos);
		cannon.inventory.setStackInSlot(0, schematic);
		// run
		cannon.state = State.RUNNING;
		cannon.statusMsg = "running";
		helper.succeedWhen(() -> {
			if (cannon.state != State.STOPPED) {
				helper.fail("Schematicannon not done");
			}
			BlockPos lastBlock = new BlockPos(1, 4, 7);
			helper.assertBlock(lastBlock, b -> b == Blocks.RED_WOOL, "Incorrect block at end of schematic");
		});
	}

	@GameTest(template = PATH + "shearing")
	public static void shearing(CreateGameTestHelper helper) {
		BlockPos sheepPos = new BlockPos(2, 1, 2);
		BlockPos sheepAbsolutePos = helper.absolutePos(sheepPos);
		AABB area = new AABB(sheepAbsolutePos).inflate(1);
		Sheep sheep = helper.getLevel().getEntities(EntityType.SHEEP, area, Entity::isAlive).get(0);
		sheep.shear(SoundSource.NEUTRAL);
		helper.succeedWhen(() -> {
			helper.assertItemEntityPresent(Items.WHITE_WOOL, sheepPos, 2);
		});
	}
}
