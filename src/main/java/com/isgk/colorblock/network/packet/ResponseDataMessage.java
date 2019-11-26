package com.isgk.colorblock.network.packet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.isgk.colorblock.network.packet.ParticleMessage.FunctionType;
import com.isgk.colorblock.util.client.component.ClientMessageUtil;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ResponseDataMessage implements IMessage {

	private FunctionType fileType;
	private String fileName;
	private byte[] data;
	private ParticleMessage message;

	public ResponseDataMessage() {
	}

	public ResponseDataMessage(FunctionType fileType, String fileName, byte[] data, ParticleMessage message) {
		this.fileType = fileType;
		this.fileName = fileName;
		this.data = data;
		this.message = message;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		fileType = FunctionType.getById(buf.readInt());
		fileName = ByteBufUtils.readUTF8String(buf);
		data = new byte[buf.readInt()];
		buf.readBytes(data);
		if (buf.readBoolean()) {
			message = new ParticleMessage();
			message.fromBytes(buf);
		} else {
			message = null;
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(fileType.getId());
		ByteBufUtils.writeUTF8String(buf, fileName);
		buf.writeInt(data.length);
		buf.writeBytes(data);
		buf.writeBoolean(message != null);
		if (message != null) {
			message.toBytes(buf);
		}
	}

	public static class MessageHandler implements IMessageHandler<ResponseDataMessage, IMessage> {

		private File imageDir;
		private File mgDir;
		private File posDir;

		public MessageHandler() {
			imageDir = new File("./particleImages");
			if (!imageDir.exists()) {
				imageDir.mkdirs();
			}
			mgDir = new File("./mgFiles");
			if (!mgDir.exists()) {
				mgDir.mkdirs();
			}
			posDir = new File("./posFiles");
			if (!posDir.exists()) {
				posDir.mkdirs();
			}
		}

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(ResponseDataMessage message, MessageContext ctx) {
			File file = null;
			switch (message.fileType) {
			case IMAGEFILEXY:
			case IMAGEFILEXZ:
			case IMAGEFILEZY:
			case IMAGEFILE:
				file = new File(imageDir, message.fileName);
				break;
			case MGFILE:
				file = new File(mgDir, message.fileName);
				break;
			case POSFILE:
				file = new File(posDir, message.fileName);
				break;
			default:
				ClientMessageUtil.addChatMessage("Function Type Error:" + message.fileType.getName());
				return null;
			}
			if (!file.exists()) {
				try (FileOutputStream fos = new FileOutputStream(file);) {
					fos.write(message.data);
					if (message != null) {
						ParticleMessage.MessageHandler.instance.parseMessage(message.message);
					}
				} catch (IOException e) {
					ClientMessageUtil.addChatMessage(e.getMessage());
				}
			} else {
				ClientMessageUtil.addChatMessage("File Is Exists:" + message.fileName);
			}
			return null;
		}

	}

}
