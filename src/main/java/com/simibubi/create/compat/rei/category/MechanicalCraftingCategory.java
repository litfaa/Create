package com.simibubi.create.compat.rei.category;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.rei.category.animations.AnimatedCrafter;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import me.shedaniel.math.FloatingDimension;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class MechanicalCraftingCategory extends CreateRecipeCategory<CraftingRecipe> {

	private final AnimatedCrafter crafter = new AnimatedCrafter();

	public MechanicalCraftingCategory(Info<CraftingRecipe> info) {
		super(info);
	}

	static int maxSize = 100;

	public static float getScale(CraftingRecipe recipe) {
		int w = getWidth(recipe);
		int h = getHeight(recipe);
		return Math.min(1, maxSize / (19f * Math.max(w, h)));
	}

	public static int getYPadding(CraftingRecipe recipe) {
		return 3 + 50 - (int) (getScale(recipe) * getHeight(recipe) * 19 * .5);
	}

	public static int getXPadding(CraftingRecipe recipe) {
		return 3 + 50 - (int) (getScale(recipe) * getWidth(recipe) * 19 * .5);
	}

	private static int getWidth(CraftingRecipe recipe) {
		return recipe instanceof ShapedRecipe ? ((ShapedRecipe)recipe).getWidth() : 1;
	}

	private static int getHeight(CraftingRecipe recipe) {
		return recipe instanceof ShapedRecipe ? ((ShapedRecipe)recipe).getHeight() : 1;
	}

	@Override
	public List<Widget> setupDisplay(CreateDisplay<CraftingRecipe> display, Rectangle pos) {
		List<Widget> widgets = super.setupDisplay(display, pos);
		CraftingRecipe recipe = display.getRecipe();

		Slot result = Widgets.createSlot(new Point( pos.x + 134, pos.y + 81))
				.entries(List.of(EntryStacks.of(recipe.getResultItem())))
				.disableBackground();
		widgets.add(result);

		int x = getXPadding(recipe);
		int y = getYPadding(recipe);
		float scale = getScale(recipe);
		FloatingDimension slotSize = new FloatingDimension(18 * scale, 18 * scale);

		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		for (int i = 0; i < ingredients.size(); i++) {
			float f = 19 * scale;
			int xPosition = (int) (x + 1 + (i % getWidth(recipe)) * f) + pos.x;
			int yPosition = (int) (y + 1 + (i / getWidth(recipe)) * f) + pos.y;

			EntryIngredient ingredient = EntryIngredients.ofIngredient(ingredients.get(i));
			if (ingredient.isEmpty())
				continue;
			Slot slot = Widgets.createSlot(new Point(xPosition, yPosition))
					.disableBackground().markInput().entries(ingredient);
			slot.getBounds().setSize(slotSize);
			widgets.add(slot);
		}

		return widgets;
	}

	@Override
	public void draw(CraftingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		matrixStack.pushPose();
		matrixStack.translate(0, -4, 0); // why?
		matrixStack.pushPose();
		float scale = getScale(recipe);
		matrixStack.translate(getXPadding(recipe), getYPadding(recipe), 0);

		for (int i = 0; i < recipe.getIngredients().size(); i++)
			if (!recipe.getIngredients().get(i).isEmpty()) {
				int row = i / getWidth(recipe);
				int col = i % getWidth(recipe);

				matrixStack.pushPose();
				matrixStack.translate(col * 19 * scale, row * 19 * scale, 0);
				matrixStack.scale(scale, scale, scale);
				AllGuiTextures.JEI_SLOT.render(matrixStack, 0, 0);
				matrixStack.popPose();
			}

		matrixStack.popPose();

		AllGuiTextures.JEI_SLOT.render(matrixStack, 133, 80);
		AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 128, 59);
		crafter.draw(matrixStack, 129, 25);

		matrixStack.pushPose();
		matrixStack.translate(0, 0, 300);

		int amount = 0;
		for (Ingredient ingredient : recipe.getIngredients()) {
			if (Ingredient.EMPTY == ingredient)
				continue;
			amount++;
		}

		Minecraft.getInstance().font.drawShadow(matrixStack, amount + "", 142, 39, 0xFFFFFF);
		matrixStack.popPose();
		matrixStack.popPose();
	}
}
