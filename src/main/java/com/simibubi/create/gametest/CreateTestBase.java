package com.simibubi.create.gametest;

import java.lang.reflect.Method;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * A base class for test classes to extend from, adding additional functionality
 * Tests in subclasses should take a {@link CreateGameTestHelper} instead of a {@link GameTestHelper}
 */
public class CreateTestBase implements FabricGameTest {
	public static final int TICKS_PER_SECOND = 20;
	public static final int TEN_SECONDS = 10 * TICKS_PER_SECOND;
	public static final int TWENTY_SECONDS = 20 * TICKS_PER_SECOND;
	public static final int FORTY_SECONDS = 40 * TICKS_PER_SECOND;

	@Override
	public void invokeTestMethod(GameTestHelper context, Method method) {
		context = CreateGameTestHelper.make(context);
		FabricGameTest.super.invokeTestMethod(context, method);
	}
}
