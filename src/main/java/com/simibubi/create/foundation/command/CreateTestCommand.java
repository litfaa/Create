package com.simibubi.create.foundation.command;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.schematics.client.SchematicAndQuillHandler;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.simibubi.create.foundation.utility.Components;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class CreateTestCommand {
	public static final DynamicCommandExceptionType INVALID_NAME = new DynamicCommandExceptionType(c -> Components.literal("Invalid character in name: " + c));
	public static final Component INFO_MISSING = Components.literal("Something went wrong! No info found. Try again?");
	public static final Component ERROR_SAVING = Components.literal("Error saving structure! Check the log for details.");
	public static final Component USE_INFO = Components.literal(
			"Use a Schematic and Quill to select an area. run /create test export [folder1] [folder2] <name> to save the selected area as a gametest."
	);
	public static final Path EXPORT_PATH = getPathForEnv();
	private static final Map<UUID, ExportInfo> INFO_IN_TRANSIT = new HashMap<>();

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return literal("test")
				.requires(cs -> AllCommands.SOURCE_IS_PLAYER.test(cs) && cs.hasPermission(2))
				.then(literal("export")
						.then(argument("folder1", StringArgumentType.word())
								.then(argument("name", StringArgumentType.word())
										.executes(ctx -> {
											startExport(
													StringArgumentType.getString(ctx, "name"),
													StringArgumentType.getString(ctx, "folder1"),
													null,
													ctx.getSource().getPlayerOrException()
											);
											return 0;
										})
								)
								.then(argument("folder2", StringArgumentType.word())
										.then(argument("name", StringArgumentType.word())
												.executes(ctx -> {
													startExport(
															StringArgumentType.getString(ctx, "name"),
															StringArgumentType.getString(ctx, "folder1"),
															StringArgumentType.getString(ctx, "folder2"),
															ctx.getSource().getPlayerOrException()
													);
													return 0;
												})
										)
								)
						)
						.then(argument("name", StringArgumentType.word())
								.executes(ctx -> {
									startExport(
											StringArgumentType.getString(ctx, "name"),
											null,
											null,
											ctx.getSource().getPlayerOrException()
									);
									return 0;
								})
						)
				)
				.executes(ctx -> {
					sendUseInfo(ctx.getSource().getPlayerOrException());
					return 0;
				});
	}

	private static void startExport(String name, @Nullable String folder1, @Nullable String folder2, ServerPlayer player) throws CommandSyntaxException {
		verifyName(name);
		AllPackets.channel.sendToClient(new TestExportCommandS2C(), player);
		send(player, Components.literal("Awaiting data from client..."));
		ExportInfo info = new ExportInfo(name, folder1, folder2);
		INFO_IN_TRANSIT.put(player.getUUID(), info);
	}

	private static void onDataReceived(ServerPlayer player, BlockPos firstPos, BlockPos secondPos) {
		ExportInfo info = INFO_IN_TRANSIT.remove(player.getUUID());
		if (info == null) {
			send(player, INFO_MISSING);
			return;
		}

		BlockPos lowCorner = new BlockPos(
				Math.min(firstPos.getX(), secondPos.getX()),
				Math.min(firstPos.getY(), secondPos.getY()),
				Math.min(firstPos.getZ(), secondPos.getZ())
		);
		BlockPos highCorner = new BlockPos(
				Math.max(firstPos.getX(), secondPos.getX()) + 1,
				Math.max(firstPos.getY(), secondPos.getY()) + 1,
				Math.max(firstPos.getZ(), secondPos.getZ()) + 1
		);
		BlockPos size = highCorner.subtract(lowCorner);

		ServerLevel level = player.getLevel();
		StructureManager structureManager = level.getStructureManager();
		StructureTemplate structure = structureManager.getOrCreate(Create.asResource(info.name));
		structure.fillFromWorld(level, lowCorner, size, true, Blocks.STRUCTURE_VOID);
		CompoundTag data = structure.save(new CompoundTag());
		String snbt = NbtUtils.structureToSnbt(data);
		Path exported = getExportPath(info);
		try {
			NbtToSnbt.writeSnbt(exported, snbt);
			send(player, Components.literal("Successfully exported '" + info.name + "' to: " + exported));
		} catch (IOException e) {
			send(player, ERROR_SAVING);
			Create.LOGGER.throwing(e);
		}
	}

	private static Path getExportPath(ExportInfo info) {
		Path exported = EXPORT_PATH;
		if (info.folder1 != null) {
			exported = exported.resolve(info.folder1);
			if (info.folder2 != null) {
				exported = exported.resolve(info.folder2);
			}
		}
		exported = exported.resolve(info.name + ".snbt");
		return exported;
	}

	private static void send(Player player, Component message) {
		player.sendMessage(message, Util.NIL_UUID);
	}

	private static void verifyName(String name) throws CommandSyntaxException {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!ResourceLocation.validPathChar(c)) {
				throw INVALID_NAME.create(c);
			}
		}
	}

	private static Path getPathForEnv() {
		FabricLoader loader = FabricLoader.getInstance();
		Path export;
		if (loader.isDevelopmentEnvironment()) {
			export = loader.getGameDir()
					.getParent()
					.resolve("src")
					.resolve("main")
					.resolve("resources")
					.resolve("data")
					.resolve("create")
					.resolve("gametest")
					.resolve("structures");
		} else {
			export = loader.getGameDir().resolve("create_gametests");
		}
		File file = export.toFile();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return export;
	}

	private static void sendUseInfo(Player player) {
		send(player, USE_INFO);
	}

	private record ExportInfo(String name, String folder1, String folder2) {
	}

	public static class TestExportCommandS2C extends SimplePacketBase {

		public TestExportCommandS2C() {
		}

		public TestExportCommandS2C(FriendlyByteBuf buf) {
		}

		@Override
		public void write(FriendlyByteBuf buffer) {
		}

		@Override
		public void handle(Supplier<Context> context) {
			SchematicAndQuillHandler handler = CreateClient.SCHEMATIC_AND_QUILL_HANDLER;
			if (handler.firstPos != null && handler.secondPos != null) {
				AllPackets.channel.sendToServer(new TestExportCommandC2S());
			} else {
				sendUseInfo(Minecraft.getInstance().player);
			}
		}
	}

	public static class TestExportCommandC2S extends SimplePacketBase {
		private final BlockPos firstPos;
		private final BlockPos secondPos;

		public TestExportCommandC2S() {
			SchematicAndQuillHandler handler = CreateClient.SCHEMATIC_AND_QUILL_HANDLER;
			this.firstPos = handler.firstPos;
			this.secondPos = handler.secondPos;
		}

		public TestExportCommandC2S(FriendlyByteBuf buffer) {
			this.firstPos = buffer.readBlockPos();
			this.secondPos = buffer.readBlockPos();
		}

		@Override
		public void write(FriendlyByteBuf buffer) {
			buffer.writeBlockPos(firstPos);
			buffer.writeBlockPos(secondPos);
		}

		@Override
		public void handle(Supplier<Context> context) {
			Context ctx = context.get();
			ctx.enqueueWork(() -> {
				onDataReceived(ctx.sender(), firstPos, secondPos);
			});
		}
	}
}
