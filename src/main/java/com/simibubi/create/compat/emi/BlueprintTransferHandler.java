package com.simibubi.create.compat.emi;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.curiosities.tools.BlueprintAssignCompleteRecipePacket;
import com.simibubi.create.content.curiosities.tools.BlueprintContainer;
import com.simibubi.create.foundation.networking.AllPackets;

import dev.emi.emi.api.EmiFillAction;
import dev.emi.emi.api.EmiRecipeHandler;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlueprintTransferHandler implements EmiRecipeHandler<BlueprintContainer> {

	@Override
	public List<Slot> getInputSources(BlueprintContainer handler) {
		return Collections.emptyList();
	}

	@Override
	public List<Slot> getCraftingSlots(BlueprintContainer handler) {
		return Collections.emptyList();
	}

	@Override
	public boolean supportsRecipe(EmiRecipe recipe) {
		return recipe instanceof EmiCraftingRecipe e && e.getId() != null;
	}

	@Override
	public boolean canCraft(EmiRecipe recipe, EmiPlayerInventory inventory, AbstractContainerScreen<BlueprintContainer> screen) {
		return true;
	}

	@Override
	public boolean performFill(EmiRecipe recipe, AbstractContainerScreen<BlueprintContainer> screen, EmiFillAction action, int amount) {
		if (recipe instanceof EmiCraftingRecipe craftingRecipe) {
			Minecraft.getInstance().setScreen(screen);
			AllPackets.channel.sendToServer(new BlueprintAssignCompleteRecipePacket(craftingRecipe.getId()));
			return true;
		}
		return false;
	}
}
