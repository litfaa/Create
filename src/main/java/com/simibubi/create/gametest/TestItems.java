package com.simibubi.create.gametest;

import com.simibubi.create.Create;

import net.minecraft.gametest.framework.GameTest;

public class TestItems extends CreateTestBase {
	public static final String PATH = Create.ID + ":items/";

	@GameTest(template = PATH + "arm_purgatory")
	public static void armPurgatory(CreateGameTestHelper helper) {
		helper.fail("NYI");
	}

	@GameTest(template = PATH + "content_observer_counting")
	public static void contentObserverCounting(CreateGameTestHelper helper) {
		helper.fail("NYI");
	}

	@GameTest(template = PATH + "storages")
	public static void storages(CreateGameTestHelper helper) {
		helper.fail("NYI");
	}

	@GameTest(template = PATH + "vault_comparator_output")
	public static void vaultComparatorOutput(CreateGameTestHelper helper) {
		helper.fail("NYI");
	}
}
