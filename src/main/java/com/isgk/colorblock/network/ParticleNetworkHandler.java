package com.isgk.colorblock.network;

import com.isgk.colorblock.ColorBlockMain;
import com.isgk.colorblock.network.packet.ParticleMessage;
import com.isgk.colorblock.network.packet.RequestDataMessage;
import com.isgk.colorblock.network.packet.ResponseDataMessage;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public enum ParticleNetworkHandler {

	INSTANCE;

	private final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(ColorBlockMain.MODID);

	private ParticleNetworkHandler() {
		int index = 0;
		this.channel.registerMessage(ParticleMessage.MessageHandler.class, ParticleMessage.class, index++, Side.CLIENT);
		this.channel.registerMessage(RequestDataMessage.MessageHandler.class, RequestDataMessage.class, index++,
				Side.SERVER);
		this.channel.registerMessage(ResponseDataMessage.MessageHandler.class, ResponseDataMessage.class, index++,
				Side.CLIENT);
	}

	public void sendMessageToDim(IMessage msg, int dim) {
		channel.sendToDimension(msg, dim);
	}

	public void sendMessageAroundPos(IMessage msg, int dim, BlockPos pos) {
		channel.sendToAllAround(msg, new NetworkRegistry.TargetPoint(dim, pos.getX(), pos.getY(), pos.getZ(), 2.0D));
	}

	public void sendMessageToPlayer(IMessage msg, EntityPlayerMP player) {
		channel.sendTo(msg, player);
	}

	public void sendMessageToAll(IMessage msg) {
		channel.sendToAll(msg);
	}

	public void sendMessageToServer(IMessage msg) {
		channel.sendToServer(msg);
	}

}
