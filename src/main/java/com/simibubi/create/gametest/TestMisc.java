package com.simibubi.create.gametest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.Create;
import com.simibubi.create.content.schematics.block.SchematicannonTileEntity;
import com.simibubi.create.content.schematics.block.SchematicannonTileEntity.State;
import com.simibubi.create.content.schematics.item.SchematicItem;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class TestMisc extends CreateTestBase {
	public static final String PATH = Create.ID + ":misc/";

	@GameTest(template = PATH + "schematicannon", timeoutTicks = 15 * 20)
	public static void schematicannon(CreateGameTestHelper helper) {
		// load the structure
		BlockPos whiteEndBottom = helper.absolutePos(new BlockPos(5, 2, 1));
		BlockPos redEndTop = helper.absolutePos(new BlockPos(5, 4, 7));
		StructureTemplate structure = new StructureTemplate();
		ServerLevel level = helper.getLevel();
		Vec3i size = redEndTop.subtract(whiteEndBottom).offset(1, 1, 1);
		structure.fillFromWorld(level, whiteEndBottom, size, false, null);
		// make schematic
		CompoundTag structureData = new CompoundTag();
		structure.save(structureData);
		try {
			Path schematics = FabricLoader.getInstance().getGameDir().resolve("schematics");
			Files.createDirectories(schematics);
			Path schematic = schematics.resolve("schematicannon_gametest.nbt");
			Files.deleteIfExists(schematic);
			Files.createFile(schematic);
			try (OutputStream out = Files.newOutputStream(schematic, StandardOpenOption.CREATE)) {
				NbtIo.writeCompressed(structureData, out);
			}
		} catch (IOException e) {
			e.printStackTrace();
			helper.fail("Failed to save schematic");
		}
		ItemStack schematic = SchematicItem.create("schematicannon_gametest.nbt", "?");
		// deploy to pos
		BlockPos anchor = helper.absolutePos(new BlockPos(1, 2, 1));
		schematic.getOrCreateTag().putBoolean("Deployed", true);
		schematic.getOrCreateTag().put("Anchor", NbtUtils.writeBlockPos(anchor));
		// setup cannon
		BlockPos cannonPos = new BlockPos(3, 2, 6);
		SchematicannonTileEntity cannon = helper.getBlockEntity(AllTileEntities.SCHEMATICANNON.get(), cannonPos);
		cannon.inventory.setStackInSlot(0, schematic);
		// run
		cannon.state = State.RUNNING;
		cannon.statusMsg = "running";
		helper.succeedWhen(() -> {
			if (cannon.state != State.STOPPED) {
				helper.fail("Schematicannon not done");
			}
			BlockPos lastBlock = new BlockPos(1, 4, 7);
			helper.assertBlockPresent(Blocks.RED_WOOL, lastBlock);
		});
	}

	@GameTest(template = PATH + "shearing")
	public static void shearing(CreateGameTestHelper helper) {
		BlockPos sheepPos = new BlockPos(2, 1, 2);
		Sheep sheep = helper.getFirstEntity(EntityType.SHEEP, sheepPos);
		sheep.shear(SoundSource.NEUTRAL);
		helper.succeedWhen(() -> {
			helper.assertItemEntityPresent(Items.WHITE_WOOL, sheepPos, 2);
		});
	}
}
