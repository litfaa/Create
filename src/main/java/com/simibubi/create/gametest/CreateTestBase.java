package com.simibubi.create.gametest;

import java.lang.reflect.Method;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * A base class for test classes to extend from, adding additional functionality
 * Tests in subclasses should take a {@link CreateGameTestHelper} instead of a {@link GameTestHelper}
 */
public class CreateTestBase implements FabricGameTest {
	@Override
	public void invokeTestMethod(GameTestHelper context, Method method) {
		context = CreateGameTestHelper.make(context);
		FabricGameTest.super.invokeTestMethod(context, method);
	}
}
