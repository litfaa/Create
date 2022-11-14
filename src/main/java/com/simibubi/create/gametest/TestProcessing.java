package com.simibubi.create.gametest;

import com.simibubi.create.Create;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class TestProcessing {
	public static final String PATH = Create.ID + ":processing/";

	@GameTest(template = PATH + "sand_washing", timeoutTicks = 40 * 20) // 30 seconds until timeout
	public static void sandWashing(GameTestHelper helper) {
		BlockPos leverPos = new BlockPos(5, 3, 1);
		helper.pullLever(leverPos);
		BlockPos chestPos = new BlockPos(8, 3, 2);
		ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(chestPos);
		helper.succeedWhen(() -> {
			if (!chest.getItem(0).is(Items.CLAY_BALL)) {
				helper.fail("Clay was not processed");
			}
		});
	}
}
