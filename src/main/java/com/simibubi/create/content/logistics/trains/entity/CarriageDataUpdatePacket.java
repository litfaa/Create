package com.simibubi.create.content.logistics.trains.entity;

import java.util.function.Supplier;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class CarriageDataUpdatePacket extends SimplePacketBase {

	private int entity;
	private CarriageSyncData data;

	public CarriageDataUpdatePacket(CarriageContraptionEntity entity) {
		this.entity = entity.getId();
		this.data = entity.carriageData;
	}

	public CarriageDataUpdatePacket(FriendlyByteBuf buf) {
		this.entity = buf.readVarInt();
		this.data = new CarriageSyncData();
		this.data.read(buf);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeVarInt(entity);
		this.data.write(buffer);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.level.getEntity(this.entity);
			if (entity instanceof CarriageContraptionEntity carriage) {
				carriage.onCarriageDataUpdate(this.data);
			} else {
				Create.LOGGER.error("Invalid CarriageDataUpdatePacket for non-carriage entity: " + entity);
			}
		});
	}
}
