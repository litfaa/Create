package com.simibubi.create.foundation.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;

@Mixin(GameTestHelper.class)
public interface GameTestHelperAccessor {
	@Accessor("testInfo")
	GameTestInfo create$testInfo();
	@Accessor("finalCheckAdded")
	boolean create$finalCheckAdded();
	@Accessor("finalCheckAdded")
	void create$finalCheckAdded(boolean value);
}
