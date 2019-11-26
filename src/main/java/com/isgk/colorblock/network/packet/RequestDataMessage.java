package com.isgk.colorblock.network.packet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.isgk.colorblock.network.packet.ParticleMessage.FunctionType;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RequestDataMessage implements IMessage {

	private FunctionType fileType;
	private String fileName;
	private ParticleMessage message;

	public RequestDataMessage() {
	}

	public RequestDataMessage(FunctionType fileType, String fileName, ParticleMessage message) {
		this.fileType = fileType;
		this.fileName = fileName;
		this.message = message;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		fileType = FunctionType.getById(buf.readInt());
		fileName = ByteBufUtils.readUTF8String(buf);
		message = new ParticleMessage();
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
		buf.writeBoolean(message != null);
		if (message != null) {
			message.toBytes(buf);
		}
	}

	public static class MessageHandler implements IMessageHandler<RequestDataMessage, ResponseDataMessage> {

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
		public ResponseDataMessage onMessage(RequestDataMessage message, MessageContext ctx) {
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
				ctx.getServerHandler().player
						.sendMessage(new TextComponentString("Function Type Error:" + message.fileType.getName()));
				return null;
			}
			if (file.exists() && file.isFile()) {
				try (FileInputStream fis = new FileInputStream(file);) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buf = new byte[8192];
					int len = 0;
					while ((len = fis.read(buf)) != -1) {
						baos.write(buf, 0, len);
					}
					return new ResponseDataMessage(message.fileType, message.fileName, baos.toByteArray(),
							message.message);
				} catch (IOException e) {
					ctx.getServerHandler().player.sendMessage(new TextComponentString(e.getMessage()));
				}
			} else {
				ctx.getServerHandler().player
						.sendMessage(new TextComponentString("File Not Fount:" + message.fileName));
			}
			return null;
		}

	}

}
