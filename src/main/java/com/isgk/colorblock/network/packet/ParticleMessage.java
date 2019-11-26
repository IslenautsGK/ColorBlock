package com.isgk.colorblock.network.packet;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.isgk.colorblock.common.command.CommandParticleEx;
import com.isgk.colorblock.core.ColorBlockCore;
import com.isgk.colorblock.network.ParticleNetworkHandler;
import com.isgk.colorblock.util.client.component.ClientMessageUtil;
import com.isgk.colorblock.util.commoninterface.IExecutable;
import com.isgk.colorblock.util.expression.ExpressionUtil;
import com.isgk.colorblock.util.matrix.Matrix;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.command.CommandException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ParticleMessage implements IMessage {

	private int id;
	private double xCoord;
	private double yCoord;
	private double zCoord;
	private float red;
	private float green;
	private float blue;
	private float alpha;
	private int brightness;
	private double motionX;
	private double motionY;
	private double motionZ;
	private FunctionType functionType;
	private double xOffset;
	private double yOffset;
	private double zOffset;
	private double tStart;
	private double tEnd;
	private int particleCount;
	private String expression;
	private double step;
	private int cpt;
	private String imagePath;
	private double proportion;
	private int rotate;
	private int overturn;
	private int maxAge;
	private String speedExpression;
	private double speedStep;
	private String matrixStr;
	private Matrix matrix;
	private int[] arguments;
	private String group;
	private ChangeType changeType;

	public ParticleMessage() {
		matrix = Matrix.E3;
	}

	public ParticleMessage(double xCoord, double yCoord, double zCoord, String group, ChangeType changeType,
			String changeExpression, String expression) {
		this(-4, xCoord, yCoord, zCoord, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0, changeExpression, 0, 0, null,
				0, 0, 0, 0, expression, 0, null, group, null);
		this.changeType = changeType;
	}

	public ParticleMessage(double xCoord, double yCoord, double zCoord, String group, String expression) {
		this(-3, xCoord, yCoord, zCoord, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0, expression, 0, 0, null, 0, 0,
				0, 0, null, 0, null, group, null);
	}

	public ParticleMessage(int id) {
		this(id, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0, null, 0, 0, null, 0, 0, 0, 0, null, 0, null,
				null, null);
	}

	public ParticleMessage(int id, double xCoord, double yCoord, double zCoord, FunctionType functionType,
			String posPath, String matrix, String group, int[] arguments) {
		this(id, xCoord, yCoord, zCoord, 0, 0, 0, 0, 0, 0, 0, 0, functionType, 0, 0, 0, 0, 0, 0, null, 0, 0, posPath, 0,
				0, 0, 0, null, 0, matrix, group, arguments);
	}

	public ParticleMessage(int id, double xCoord, double yCoord, double zCoord, FunctionType functionType,
			String mgPath, String matrix, double step, int brightness, int auto, int[] arguments) {
		this(id, xCoord, yCoord, zCoord, 0, 0, 0, 0, brightness, 0, 0, 0, functionType, 0, 0, 0, 0, 0, 0, null, step,
				auto, mgPath, 0, 0, 0, 0, null, 0, matrix, null, arguments);
	}

	public ParticleMessage(int id, double xCoord, double yCoord, double zCoord, FunctionType functionType,
			String imagePath, double proportion, String matrix, double step, int brightness, double motionX,
			double motionY, double motionZ, int maxAge, String speedExpression, double speedStep, String group,
			int[] arguments) {
		this(id, xCoord, yCoord, zCoord, 0, 0, 0, 0, brightness, motionX, motionY, motionZ, functionType, 0, 0, 0, 0, 0,
				0, null, step, 0, imagePath, proportion, 0, 0, maxAge, speedExpression, speedStep, matrix, group,
				arguments);
	}

	public ParticleMessage(int id, double xCoord, double yCoord, double zCoord, FunctionType functionType,
			String imagePath, double proportion, int rotate, int overturn, double step, int brightness, double motionX,
			double motionY, double motionZ, int maxAge, String speedExpression, double speedStep, String group,
			int[] arguments) {
		this(id, xCoord, yCoord, zCoord, 0, 0, 0, 0, brightness, motionX, motionY, motionZ, functionType, 0, 0, 0, 0, 0,
				0, null, step, 0, imagePath, proportion, rotate, overturn, maxAge, speedExpression, speedStep, null,
				group, arguments);
	}

	public ParticleMessage(int id, double xCoord, double yCoord, double zCoord, FunctionType functionType,
			double tStart, double tEnd, String expression, double step, int cpt, int maxAge, String speedExpression,
			double speedStep, String group, int[] arguments) {
		this(id, xCoord, yCoord, zCoord, 0, 0, 0, 0, 0, 0, 0, 0, functionType, 0, 0, 0, tStart, tEnd, 0, expression,
				step, cpt, null, 0, 0, 0, maxAge, speedExpression, speedStep, null, group, arguments);
	}

	public ParticleMessage(int id, double xCoord, double yCoord, double zCoord, FunctionType functionType,
			double tStart, double tEnd, String expression, double step, int maxAge, String speedExpression,
			double speedStep, String group, int[] arguments) {
		this(id, xCoord, yCoord, zCoord, 0, 0, 0, 0, 0, 0, 0, 0, functionType, 0, 0, 0, tStart, tEnd, 0, expression,
				step, 0, null, 0, 0, 0, maxAge, speedExpression, speedStep, null, group, arguments);
	}

	public ParticleMessage(int id, double xCoord, double yCoord, double zCoord, FunctionType functionType, float red,
			float green, float blue, float alpha, int brightness, double motionX, double motionY, double motionZ,
			double tStart, double tEnd, String expression, double step, int cpt, int maxAge, String speedExpression,
			double speedStep, String group, int[] arguments) {
		this(id, xCoord, yCoord, zCoord, red, green, blue, alpha, brightness, motionX, motionY, motionZ, functionType,
				0, 0, 0, tStart, tEnd, 0, expression, step, cpt, null, 0, 0, 0, maxAge, speedExpression, speedStep,
				null, group, arguments);
	}

	public ParticleMessage(int id, double xCoord, double yCoord, double zCoord, FunctionType functionType, float red,
			float green, float blue, float alpha, int brightness, double motionX, double motionY, double motionZ,
			double tStart, double tEnd, String expression, double step, int maxAge, String speedExpression,
			double speedStep, String group, int[] arguments) {
		this(id, xCoord, yCoord, zCoord, red, green, blue, alpha, brightness, motionX, motionY, motionZ, functionType,
				0, 0, 0, tStart, tEnd, 0, expression, step, 0, null, 0, 0, 0, maxAge, speedExpression, speedStep, null,
				group, arguments);
	}

	public ParticleMessage(int id, double xCoord, double yCoord, double zCoord, FunctionType functionType, float red,
			float green, float blue, float alpha, int brightness, double motionX, double motionY, double motionZ,
			double xOffset, double yOffset, double zOffset, String expression, double step, int maxAge,
			String speedExpression, double speedStep, String group, int[] arguments) {
		this(id, xCoord, yCoord, zCoord, red, green, blue, alpha, brightness, motionX, motionY, motionZ, functionType,
				xOffset, yOffset, zOffset, 0, 0, 0, expression, step, 0, null, 0, 0, 0, maxAge, speedExpression,
				speedStep, null, group, arguments);
	}

	public ParticleMessage(int id, double xCoord, double yCoord, double zCoord, FunctionType functionType, float red,
			float green, float blue, float alpha, int brightness, double motionX, double motionY, double motionZ,
			double xOffset, double yOffset, double zOffset, int particleCount, int maxAge, String speedExpression,
			double speedStep, String group, int[] arguments) {
		this(id, xCoord, yCoord, zCoord, red, green, blue, alpha, brightness, motionX, motionY, motionZ, functionType,
				xOffset, yOffset, zOffset, 0, 0, particleCount, null, 0, 0, null, 0, 0, 0, maxAge, speedExpression,
				speedStep, null, group, arguments);
	}

	public ParticleMessage(ParticleMessage message) {
		this(message.id, message.xCoord, message.yCoord, message.zCoord, message.red, message.green, message.blue,
				message.alpha, message.brightness, message.motionX, message.motionY, message.motionZ,
				message.functionType, message.xOffset, message.yOffset, message.zOffset, message.tStart, message.tEnd,
				message.particleCount, message.expression, message.step, message.cpt, message.imagePath,
				message.proportion, message.rotate, message.overturn, message.maxAge, message.speedExpression,
				message.speedStep, message.matrixStr, message.group, message.arguments);
		this.matrix = message.matrix;
	}

	public ParticleMessage(int id, double xCoord, double yCoord, double zCoord, float red, float green, float blue,
			float alpha, int brightness, double motionX, double motionY, double motionZ, FunctionType functionType,
			double xOffset, double yOffset, double zOffset, double tStart, double tEnd, int particleCount,
			String expression, double step, int cpt, String imagePath, double proportion, int rotate, int overturn,
			int maxAge, String speedExpression, double speedStep, String matrix, String group, int[] arguments) {
		this.id = id;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.brightness = brightness;
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		this.functionType = functionType;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.tStart = tStart;
		this.tEnd = tEnd;
		this.particleCount = particleCount;
		this.expression = expression;
		this.step = step;
		this.cpt = cpt;
		this.imagePath = imagePath;
		this.proportion = proportion;
		this.rotate = rotate;
		this.overturn = overturn;
		this.maxAge = maxAge;
		this.speedExpression = speedExpression;
		this.speedStep = speedStep;
		this.matrixStr = matrix;
		this.matrix = new Matrix(matrix);
		this.group = group;
		this.arguments = arguments == null ? new int[0] : arguments;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		if (id < 0) {
			switch (id) {
			case -3:
				xCoord = buf.readDouble();
				yCoord = buf.readDouble();
				zCoord = buf.readDouble();
				group = ByteBufUtils.readUTF8String(buf);
				if (buf.readBoolean()) {
					expression = ByteBufUtils.readUTF8String(buf);
				}
				break;
			case -4:
				xCoord = buf.readDouble();
				yCoord = buf.readDouble();
				zCoord = buf.readDouble();
				group = ByteBufUtils.readUTF8String(buf);
				changeType = ChangeType.getById(buf.readInt());
				expression = ByteBufUtils.readUTF8String(buf);
				if (buf.readBoolean()) {
					speedExpression = ByteBufUtils.readUTF8String(buf);
				}
				break;
			}
			return;
		}
		xCoord = buf.readDouble();
		yCoord = buf.readDouble();
		zCoord = buf.readDouble();
		functionType = FunctionType.getById(buf.readInt());
		switch (functionType) {
		case NORMAL:
			red = buf.readFloat();
			green = buf.readFloat();
			blue = buf.readFloat();
			alpha = buf.readFloat();
			brightness = buf.readInt();
			motionX = buf.readDouble();
			motionY = buf.readDouble();
			motionZ = buf.readDouble();
			xOffset = buf.readDouble();
			yOffset = buf.readDouble();
			zOffset = buf.readDouble();
			particleCount = buf.readInt();
			break;
		case FUNCTION:
			red = buf.readFloat();
			green = buf.readFloat();
			blue = buf.readFloat();
			alpha = buf.readFloat();
			brightness = buf.readInt();
			motionX = buf.readDouble();
			motionY = buf.readDouble();
			motionZ = buf.readDouble();
			xOffset = buf.readDouble();
			yOffset = buf.readDouble();
			zOffset = buf.readDouble();
			expression = ByteBufUtils.readUTF8String(buf);
			step = buf.readDouble();
			break;
		case PARAMETER:
		case POLARPARAMETER:
			red = buf.readFloat();
			green = buf.readFloat();
			blue = buf.readFloat();
			alpha = buf.readFloat();
			brightness = buf.readInt();
			motionX = buf.readDouble();
			motionY = buf.readDouble();
			motionZ = buf.readDouble();
			tStart = buf.readDouble();
			tEnd = buf.readDouble();
			expression = ByteBufUtils.readUTF8String(buf);
			step = buf.readDouble();
			break;
		case TICKPARAMETER:
		case TICKPOLARPARAMETER:
			red = buf.readFloat();
			green = buf.readFloat();
			blue = buf.readFloat();
			alpha = buf.readFloat();
			brightness = buf.readInt();
			motionX = buf.readDouble();
			motionY = buf.readDouble();
			motionZ = buf.readDouble();
			tStart = buf.readDouble();
			tEnd = buf.readDouble();
			expression = ByteBufUtils.readUTF8String(buf);
			step = buf.readDouble();
			cpt = buf.readInt();
			break;
		case PARAMETERRGB:
		case POLARPARAMETERRGB:
			tStart = buf.readDouble();
			tEnd = buf.readDouble();
			expression = ByteBufUtils.readUTF8String(buf);
			step = buf.readDouble();
			break;
		case TICKPARAMETERRGB:
		case TICKPOLARPARAMETERRGB:
			tStart = buf.readDouble();
			tEnd = buf.readDouble();
			expression = ByteBufUtils.readUTF8String(buf);
			step = buf.readDouble();
			cpt = buf.readInt();
			break;
		case IMAGEFILEXY:
		case IMAGEFILEZY:
		case IMAGEFILEXZ:
			imagePath = ByteBufUtils.readUTF8String(buf);
			proportion = buf.readDouble();
			rotate = buf.readInt();
			overturn = buf.readInt();
			step = buf.readDouble();
			brightness = buf.readInt();
			motionX = buf.readDouble();
			motionY = buf.readDouble();
			motionZ = buf.readDouble();
			break;
		case IMAGEFILE:
			imagePath = ByteBufUtils.readUTF8String(buf);
			proportion = buf.readDouble();
			if (buf.readBoolean()) {
				matrixStr = ByteBufUtils.readUTF8String(buf);
				matrix = new Matrix(matrixStr);
			}
			step = buf.readDouble();
			brightness = buf.readInt();
			motionX = buf.readDouble();
			motionY = buf.readDouble();
			motionZ = buf.readDouble();
			break;
		case MGFILE:
			imagePath = ByteBufUtils.readUTF8String(buf);
			if (buf.readBoolean()) {
				matrixStr = ByteBufUtils.readUTF8String(buf);
				matrix = new Matrix(matrixStr);
			}
			step = buf.readDouble();
			brightness = buf.readInt();
			cpt = buf.readInt();
			break;
		case POSFILE:
			imagePath = ByteBufUtils.readUTF8String(buf);
			if (buf.readBoolean()) {
				matrixStr = ByteBufUtils.readUTF8String(buf);
				matrix = new Matrix(matrixStr);
			}
		default:
			break;
		}
		maxAge = buf.readInt();
		if (buf.readBoolean()) {
			speedExpression = ByteBufUtils.readUTF8String(buf);
			speedStep = buf.readDouble();
		}
		if (buf.readBoolean()) {
			group = ByteBufUtils.readUTF8String(buf);
		}
		arguments = new int[buf.readInt()];
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = buf.readInt();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		if (id < 0) {
			switch (id) {
			case -3:
				buf.writeDouble(xCoord);
				buf.writeDouble(yCoord);
				buf.writeDouble(zCoord);
				ByteBufUtils.writeUTF8String(buf, group);
				buf.writeBoolean(expression != null);
				if (expression != null) {
					ByteBufUtils.writeUTF8String(buf, expression);
				}
				break;
			case -4:
				buf.writeDouble(xCoord);
				buf.writeDouble(yCoord);
				buf.writeDouble(zCoord);
				ByteBufUtils.writeUTF8String(buf, group);
				buf.writeInt(changeType.getId());
				ByteBufUtils.writeUTF8String(buf, expression);
				buf.writeBoolean(speedExpression != null);
				if (speedExpression != null) {
					ByteBufUtils.writeUTF8String(buf, speedExpression);
				}
				break;
			}
			return;
		}
		buf.writeDouble(xCoord);
		buf.writeDouble(yCoord);
		buf.writeDouble(zCoord);
		buf.writeInt(functionType.getId());
		switch (functionType) {
		case NORMAL:
			buf.writeFloat(red);
			buf.writeFloat(green);
			buf.writeFloat(blue);
			buf.writeFloat(alpha);
			buf.writeInt(brightness);
			buf.writeDouble(motionX);
			buf.writeDouble(motionY);
			buf.writeDouble(motionZ);
			buf.writeDouble(xOffset);
			buf.writeDouble(yOffset);
			buf.writeDouble(zOffset);
			buf.writeInt(particleCount);
			break;
		case FUNCTION:
			buf.writeFloat(red);
			buf.writeFloat(green);
			buf.writeFloat(blue);
			buf.writeFloat(alpha);
			buf.writeInt(brightness);
			buf.writeDouble(motionX);
			buf.writeDouble(motionY);
			buf.writeDouble(motionZ);
			buf.writeDouble(xOffset);
			buf.writeDouble(yOffset);
			buf.writeDouble(zOffset);
			ByteBufUtils.writeUTF8String(buf, expression);
			buf.writeDouble(step);
			break;
		case PARAMETER:
		case POLARPARAMETER:
			buf.writeFloat(red);
			buf.writeFloat(green);
			buf.writeFloat(blue);
			buf.writeFloat(alpha);
			buf.writeInt(brightness);
			buf.writeDouble(motionX);
			buf.writeDouble(motionY);
			buf.writeDouble(motionZ);
			buf.writeDouble(tStart);
			buf.writeDouble(tEnd);
			ByteBufUtils.writeUTF8String(buf, expression);
			buf.writeDouble(step);
			break;
		case TICKPARAMETER:
		case TICKPOLARPARAMETER:
			buf.writeFloat(red);
			buf.writeFloat(green);
			buf.writeFloat(blue);
			buf.writeFloat(alpha);
			buf.writeInt(brightness);
			buf.writeDouble(motionX);
			buf.writeDouble(motionY);
			buf.writeDouble(motionZ);
			buf.writeDouble(tStart);
			buf.writeDouble(tEnd);
			ByteBufUtils.writeUTF8String(buf, expression);
			buf.writeDouble(step);
			buf.writeInt(cpt);
			break;
		case PARAMETERRGB:
		case POLARPARAMETERRGB:
			buf.writeDouble(tStart);
			buf.writeDouble(tEnd);
			ByteBufUtils.writeUTF8String(buf, expression);
			buf.writeDouble(step);
			break;
		case TICKPARAMETERRGB:
		case TICKPOLARPARAMETERRGB:
			buf.writeDouble(tStart);
			buf.writeDouble(tEnd);
			ByteBufUtils.writeUTF8String(buf, expression);
			buf.writeDouble(step);
			buf.writeInt(cpt);
			break;
		case IMAGEFILEXY:
		case IMAGEFILEZY:
		case IMAGEFILEXZ:
			ByteBufUtils.writeUTF8String(buf, imagePath);
			buf.writeDouble(proportion);
			buf.writeInt(rotate);
			buf.writeInt(overturn);
			buf.writeDouble(step);
			buf.writeInt(brightness);
			buf.writeDouble(motionX);
			buf.writeDouble(motionY);
			buf.writeDouble(motionZ);
			break;
		case IMAGEFILE:
			ByteBufUtils.writeUTF8String(buf, imagePath);
			buf.writeDouble(proportion);
			buf.writeBoolean(matrixStr != null);
			if (matrixStr != null) {
				ByteBufUtils.writeUTF8String(buf, matrixStr);
			}
			buf.writeDouble(step);
			buf.writeInt(brightness);
			buf.writeDouble(motionX);
			buf.writeDouble(motionY);
			buf.writeDouble(motionZ);
			break;
		case MGFILE:
			ByteBufUtils.writeUTF8String(buf, imagePath);
			buf.writeBoolean(matrixStr != null);
			if (matrixStr != null) {
				ByteBufUtils.writeUTF8String(buf, matrixStr);
			}
			buf.writeDouble(step);
			buf.writeInt(brightness);
			buf.writeInt(cpt);
			break;
		case POSFILE:
			ByteBufUtils.writeUTF8String(buf, imagePath);
			buf.writeBoolean(matrixStr != null);
			if (matrixStr != null) {
				ByteBufUtils.writeUTF8String(buf, matrixStr);
			}
		default:
			break;
		}
		buf.writeInt(maxAge);
		buf.writeBoolean(speedExpression != null);
		if (speedExpression != null) {
			ByteBufUtils.writeUTF8String(buf, speedExpression);
			buf.writeDouble(speedStep);
		}
		buf.writeBoolean(group != null && !group.equals("null"));
		if (group != null && !group.equals("null")) {
			ByteBufUtils.writeUTF8String(buf, group);
		}
		buf.writeInt(arguments.length);
		for (int i = 0; i < arguments.length; i++) {
			buf.writeInt(arguments[i]);
		}
	}

	@EventBusSubscriber(modid = "color_block")
	public static class MessageHandler implements IMessageHandler<ParticleMessage, IMessage> {

		public static MessageHandler instance;
		private static Set<MG> mgSet = Sets.newHashSet();

		private final Random avRandomizer = new Random();
		private final Map<File, Map<Double, BufferedImage>> imageBuf = Maps.newHashMap();
		private final Map<File, Map<Double, int[][]>> imagesBuf = Maps.newHashMap();
		private final Map<File, Map<Double, int[]>> imagesWHBuf = Maps.newHashMap();
		private final Map<String, List<Particle>> groups = Maps.newHashMap();

		private File imageDir;
		private File mgDir;
		private File posDir;

		public MessageHandler() {
			if (instance == null) {
				instance = this;
			}
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
		// @SuppressWarnings("unchecked")
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(ParticleMessage message, MessageContext ctx) {
			return parseMessage(message, true);
		}

		@SideOnly(Side.CLIENT)
		public void parseMessage(ParticleMessage message) {
			parseMessage(message, false);
		}

		@SideOnly(Side.CLIENT)
		private IMessage parseMessage(ParticleMessage message, boolean ones) {
			if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
				Minecraft.getMinecraft().addScheduledTask(new Runnable() {
					public void run() {
						MessageHandler.this.parseMessage(message, true);
					}
				});
			} else if (message.id < 0) {
				switch (message.id) {
				case -1:
					clearCache();
					break;
				case -2:
					clearParticle();
					break;
				case -3:
					removeParticles(message.xCoord, message.yCoord, message.zCoord, message.group, message.expression);
					break;
				case -4:
					changeParticles(message.xCoord, message.yCoord, message.zCoord, message.group, message.changeType,
							message.expression, message.speedExpression);
					break;
				}
			} else {
				switch (message.functionType) {
				case NORMAL: {
					for (int i = 0; i < message.particleCount; i++) {
						double dx = this.avRandomizer.nextGaussian() * message.xOffset;
						double dy = this.avRandomizer.nextGaussian() * message.yOffset;
						double dz = this.avRandomizer.nextGaussian() * message.zOffset;
						spawnParticle(message.id, message.xCoord, message.yCoord, message.zCoord, dx, dy, dz,
								message.matrix, message.red, message.green, message.blue, message.alpha,
								message.brightness, message.motionX, message.motionY, message.motionZ, message.maxAge,
								message.speedExpression, message.speedStep, null, message.group, message.arguments);
					}
					break;
				}
				case FUNCTION: {
					IExecutable exe = ExpressionUtil.prase(message.expression, true);
					exe.put("PI", Math.PI);
					exe.put("E", Math.E);
					for (double dx = -message.xOffset; dx <= message.xOffset; dx += message.step) {
						for (double dy = -message.yOffset; dy <= message.yOffset; dy += message.step) {
							for (double dz = -message.zOffset; dz <= message.zOffset; dz += message.step) {
								exe.put("x", dx);
								exe.put("y", dy);
								exe.put("z", dz);
								exe.put("θ1", Math.atan2(dz, dx));
								exe.put("θ2", Math.atan2(dy, Math.hypot(dx, dz)));
								exe.put("r", Math.sqrt(dx * dx + dy * dy + dz * dz));
								if ((Boolean) exe.invoke()) {
									spawnParticle(message.id, message.xCoord, message.yCoord, message.zCoord, dx, dy,
											dz, message.matrix, message.red, message.green, message.blue, message.alpha,
											message.brightness, message.motionX, message.motionY, message.motionZ,
											message.maxAge, message.speedExpression, message.speedStep, null,
											message.group, message.arguments);
								}
							}
						}
					}
					break;
				}
				case PARAMETER: {
					IExecutable exe = ExpressionUtil.prase(message.expression, false);
					exe.put("PI", Math.PI);
					exe.put("E", Math.E);
					exe.put("x", 0D);
					exe.put("y", 0D);
					exe.put("z", 0D);
					for (double t = message.tStart; t <= message.tEnd; t += message.step) {
						exe.put("t", t);
						exe.invoke();
						double dx = (double) exe.get("x");
						double dy = (double) exe.get("y");
						double dz = (double) exe.get("z");
						spawnParticle(message.id, message.xCoord, message.yCoord, message.zCoord, dx, dy, dz,
								message.matrix, message.red, message.green, message.blue, message.alpha,
								message.brightness, message.motionX, message.motionY, message.motionZ, message.maxAge,
								message.speedExpression, message.speedStep, null, message.group, message.arguments);
					}
					break;
				}
				case POLARPARAMETER: {
					IExecutable exe = ExpressionUtil.prase(message.expression, false);
					exe.put("PI", Math.PI);
					exe.put("E", Math.E);
					exe.put("θ1", 0D);
					exe.put("θ2", 0D);
					exe.put("r", 0D);
					for (double t = message.tStart; t <= message.tEnd; t += message.step) {
						exe.put("t", t);
						exe.invoke();
						double θ1 = (double) exe.get("θ1");
						double θ2 = (double) exe.get("θ2");
						double dr = (double) exe.get("r");
						double dx = dr * Math.cos(θ2) * Math.cos(θ1);
						double dy = dr * Math.sin(θ2);
						double dz = dr * Math.cos(θ2) * Math.sin(θ1);
						spawnParticle(message.id, message.xCoord, message.yCoord, message.zCoord, dx, dy, dz,
								message.matrix, message.red, message.green, message.blue, message.alpha,
								message.brightness, message.motionX, message.motionY, message.motionZ, message.maxAge,
								message.speedExpression, message.speedStep, null, message.group, message.arguments);
					}
					break;
				}
				case TICKPARAMETER: {
					new TickParticleTask(message, false, false).run();
					break;
				}
				case TICKPOLARPARAMETER: {
					new TickParticleTask(message, true, false).run();
					break;
				}
				case PARAMETERRGB: {
					IExecutable exe = ExpressionUtil.prase(message.expression, false);
					exe.put("PI", Math.PI);
					exe.put("E", Math.E);
					exe.put("x", 0D);
					exe.put("y", 0D);
					exe.put("z", 0D);
					exe.put("red", 1D);
					exe.put("green", 1D);
					exe.put("blue", 1D);
					exe.put("alpha", 1D);
					exe.put("brightness", 240D);
					exe.put("vx", 0D);
					exe.put("vy", 0D);
					exe.put("vz", 0D);
					for (double t = message.tStart; t <= message.tEnd; t += message.step) {
						exe.put("t", t);
						exe.invoke();
						double dx = (double) exe.get("x");
						double dy = (double) exe.get("y");
						double dz = (double) exe.get("z");
						double red = (double) exe.get("red");
						double green = (double) exe.get("green");
						double blue = (double) exe.get("blue");
						double alpha = (double) exe.get("alpha");
						double brightness = (double) exe.get("brightness");
						double vx = (double) exe.get("vx");
						double vy = (double) exe.get("vy");
						double vz = (double) exe.get("vz");
						spawnParticle(message.id, message.xCoord, message.yCoord, message.zCoord, dx, dy, dz,
								message.matrix, (float) red, (float) green, (float) blue, (float) alpha,
								(int) Math.round(brightness), vx, vy, vz, message.maxAge, message.speedExpression,
								message.speedStep, null, message.group, message.arguments);
					}
					break;
				}
				case POLARPARAMETERRGB: {
					IExecutable exe = ExpressionUtil.prase(message.expression, false);
					exe.put("PI", Math.PI);
					exe.put("E", Math.E);
					exe.put("θ1", 0D);
					exe.put("θ2", 0D);
					exe.put("r", 0D);
					exe.put("red", 1D);
					exe.put("green", 1D);
					exe.put("blue", 1D);
					exe.put("alpha", 1D);
					exe.put("brightness", 240D);
					exe.put("vx", 0D);
					exe.put("vy", 0D);
					exe.put("vz", 0D);
					for (double t = message.tStart; t <= message.tEnd; t += message.step) {
						exe.put("t", t);
						exe.invoke();
						double θ1 = (double) exe.get("θ1");
						double θ2 = (double) exe.get("θ2");
						double dr = (double) exe.get("r");
						double red = (double) exe.get("red");
						double green = (double) exe.get("green");
						double blue = (double) exe.get("blue");
						double alpha = (double) exe.get("alpha");
						double brightness = (double) exe.get("brightness");
						double vx = (double) exe.get("vx");
						double vy = (double) exe.get("vy");
						double vz = (double) exe.get("vz");
						double dx = dr * Math.cos(θ2) * Math.cos(θ1);
						double dy = dr * Math.sin(θ2);
						double dz = dr * Math.cos(θ2) * Math.sin(θ1);
						spawnParticle(message.id, message.xCoord, message.yCoord, message.zCoord, dx, dy, dz,
								message.matrix, (float) red, (float) green, (float) blue, (float) alpha,
								(int) Math.round(brightness), vx, vy, vz, message.maxAge, message.speedExpression,
								message.speedStep, null, message.group, message.arguments);
					}
					break;
				}
				case TICKPARAMETERRGB: {
					new TickParticleTask(message, false, true).run();
					break;
				}
				case TICKPOLARPARAMETERRGB: {
					new TickParticleTask(message, true, true).run();
					break;
				}
				case IMAGEFILEXY:
				case IMAGEFILEZY:
				case IMAGEFILEXZ:
				case IMAGEFILE: {
					if (new File(imageDir, message.imagePath).exists()) {
						spawnImage(message.id, message.xCoord, message.yCoord, message.zCoord, message.functionType,
								message.imagePath, message.proportion, message.rotate, message.overturn, message.matrix,
								message.step, message.brightness, message.motionX, message.motionY, message.motionZ,
								message.maxAge, message.speedExpression, message.speedStep, message.group,
								message.arguments);
					} else if (ones) {
						ParticleNetworkHandler.INSTANCE.sendMessageToServer(
								new RequestDataMessage(message.functionType, message.imagePath, message));
					} else {
						ClientMessageUtil.addChatMessage("File Not Found:" + message.imagePath);
					}
					break;
				}
				case MGFILE: {
					File mgFile = new File(mgDir, message.imagePath);
					if (mgFile.exists()) {
						if (mgFile.isFile()) {
							Map<String, String> cfg = Maps.newHashMap();
							Queue<MGControl> controls = Queues.newArrayDeque();
							try (BufferedReader br = new BufferedReader(
									new InputStreamReader(new FileInputStream(mgFile)));) {
								String line;
								while ((line = br.readLine()) != null) {
									if (line.startsWith("#") || line.length() == 0) {
										continue;
									}
									int index = line.indexOf('=');
									if (index != -1) {
										cfg.put(line.substring(0, index), line.substring(index + 1));
									} else {
										String[] strs = line.split(" ");
										MGControl mgControl = null;
										if (strs.length < 3) {
											ClientMessageUtil.addChatMessage("ERROR:" + line);
										} else {
											int tick = Integer.parseInt(strs[0]);
											MGType type = MGType.getByName(strs[1]);
											if (type != null) {
												int rail = Integer.parseInt(strs[2]);
												int toneCount = (strs.length - 3) / 2;
												String[] tones = new String[toneCount];
												float[] volumes = new float[toneCount];
												for (int i = 0; i < toneCount; i++) {
													tones[i] = strs[3 + 2 * i];
													if (strs.length > 4 + 2 * i) {
														volumes[i] = Float.parseFloat(strs[4 + 2 * i]);
													} else {
														volumes[i] = 1F;
													}
												}
												mgControl = new MGControl(tick, type, rail, tones, volumes);
											}
											if (mgControl != null) {
												controls.add(mgControl);
											} else {
												ClientMessageUtil.addChatMessage("ERROR:" + line);
											}
										}
									}
								}
							} catch (IOException e) {
								ClientMessageUtil.addChatMessage(e.getMessage());
								e.printStackTrace();
							}
							if (cfg.containsKey("soundName") && cfg.containsKey("overallTick")
									&& cfg.containsKey("railCount") && cfg.containsKey("background")
									&& cfg.containsKey("dropBlock") && cfg.containsKey("raildxu")
									&& cfg.containsKey("raildxd") && cfg.containsKey("raildyu")
									&& cfg.containsKey("raildyd") && cfg.containsKey("keyDown")
									&& cfg.containsKey("perfect") && cfg.containsKey("miss") && cfg.containsKey("textX")
									&& cfg.containsKey("textY")) {
								String soundName = cfg.get("soundName");
								float volume = cfg.containsKey("volume") ? Float.parseFloat(cfg.get("volume")) : 1F;
								int overallTick = Integer.parseInt(cfg.get("overallTick"));
								int startTick = cfg.containsKey("startTick") ? Integer.parseInt(cfg.get("startTick"))
										: 0;
								int railCount = Integer.parseInt(cfg.get("railCount"));
								int dropSpeed = cfg.containsKey("dropSpeed") ? Integer.parseInt(cfg.get("dropSpeed"))
										: 20;
								String background = cfg.get("background");
								String dropBlock = cfg.get("dropBlock");
								String dropStripStart = cfg.containsKey("dropStripStart") ? cfg.get("dropStripStart")
										: dropBlock;
								String dropStripMid = cfg.containsKey("dropStripMid") ? cfg.get("dropStripMid")
										: dropBlock;
								String dropStripEnd = cfg.containsKey("dropStripEnd") ? cfg.get("dropStripEnd")
										: dropBlock;
								String keyDown = cfg.get("keyDown");
								String perfect = cfg.get("perfect");
								String good = cfg.containsKey("good") ? cfg.get("good") : perfect;
								String normal = cfg.containsKey("normal") ? cfg.get("normal") : perfect;
								String bad = cfg.containsKey("bad") ? cfg.get("bad") : perfect;
								String miss = cfg.get("miss");
								String[] numStr = parseS(cfg.get("nums"), 10);
								double[] raildxu = parseD(cfg.get("raildxu"), railCount);
								double[] raildxd = parseD(cfg.get("raildxd"), railCount);
								double[] raildyu = parseD(cfg.get("raildyu"), railCount);
								double[] raildyd = parseD(cfg.get("raildyd"), railCount);
								double[] raildzu = cfg.containsKey("raildzu") ? parseD(cfg.get("raildzu"), railCount)
										: new double[railCount];
								double[] raildzd = cfg.containsKey("raildzd") ? parseD(cfg.get("raildzd"), railCount)
										: new double[railCount];
								double[] textX = parseD(cfg.get("textX"), 8);
								double[] textY = parseD(cfg.get("textY"), 8);
								double[] textZ = cfg.containsKey("textZ") ? parseD(cfg.get("textZ"), 8) : new double[8];
								MG mg = new MG(soundName, volume, overallTick, startTick, railCount, raildxu, raildxd,
										raildyu, raildyd, raildzu, raildzd, dropSpeed, background, dropBlock,
										dropStripStart, dropStripMid, dropStripEnd, keyDown, perfect, good, normal, bad,
										miss, numStr, message.id, message.xCoord, message.yCoord, message.zCoord, textX,
										textY, textZ, message.matrix, message.step, message.brightness, controls,
										message.cpt != 0);
								new MusicGameTask(mg).run();
							} else {
								ClientMessageUtil.addChatMessage("Bad File:" + message.imagePath);
							}
						} else {
							ClientMessageUtil.addChatMessage("Not A File:" + message.imagePath);
						}
					} else {
						ClientMessageUtil.addChatMessage("File Not Found:" + message.imagePath);
					}
					break;
				}
				case POSFILE: {
					File posFile = new File(posDir, message.imagePath);
					if (posFile.exists()) {
						if (posFile.isFile()) {
							boolean binaryFile = message.imagePath.endsWith(".particles");
							if (message.imagePath.endsWith(".particles")) {
								try (DataInputStream dis = new DataInputStream(new FileInputStream(posFile));) {
									int count = dis.readInt();
									for (int i = 0; i < count; i++) {
										byte argCount = dis.readByte();
										if (argCount >= 3 && argCount <= 14) {
											double x = dis.readDouble();
											double y = dis.readDouble();
											double z = dis.readDouble();
											float red = argCount > 3 ? dis.readFloat() : 1F;
											float green = argCount > 4 ? dis.readFloat() : 1F;
											float blue = argCount > 5 ? dis.readFloat() : 1F;
											float alpha = argCount > 6 ? dis.readFloat() : 1F;
											int brightness = argCount > 7 ? dis.readInt() : 240;
											double vx = argCount > 8 ? dis.readDouble() : 0D;
											double vy = argCount > 9 ? dis.readDouble() : 0D;
											double vz = argCount > 10 ? dis.readDouble() : 0D;
											int maxAge = argCount > 11 ? dis.readInt() : 0;
											String speedExpression = argCount > 12 ? dis.readUTF() : null;
											double speedStep = argCount > 13 ? dis.readDouble() : 0D;
											spawnParticle(message.id, message.xCoord, message.yCoord, message.zCoord, x,
													y, z, message.matrix, red, green, blue, alpha, brightness, vx, vy,
													vz, maxAge, speedExpression, speedStep, null, message.group,
													message.arguments);
										} else {
											ClientMessageUtil.addChatMessage("Bad File:" + message.imagePath);
										}
									}
								} catch (IOException e) {
									ClientMessageUtil.addChatMessage(e.getMessage());
									e.printStackTrace();
								}
							} else {
								try (BufferedReader br = new BufferedReader(
										new InputStreamReader(new FileInputStream(posFile)));) {
									String line;
									while ((line = br.readLine()) != null) {
										if (line.startsWith("#") || line.length() == 0) {
											continue;
										}
										String[] strs = line.split(" ");
										int argCount = strs.length;
										if (argCount >= 3 && argCount <= 14) {
											double x = Double.parseDouble(strs[0]);
											double y = Double.parseDouble(strs[1]);
											double z = Double.parseDouble(strs[2]);
											float red = argCount > 3 ? Float.parseFloat(strs[3]) : 1F;
											float green = argCount > 4 ? Float.parseFloat(strs[4]) : 1F;
											float blue = argCount > 5 ? Float.parseFloat(strs[5]) : 1F;
											float alpha = argCount > 6 ? Float.parseFloat(strs[6]) : 1F;
											int brightness = argCount > 7 ? Integer.parseInt(strs[7]) : 240;
											double vx = argCount > 8 ? Double.parseDouble(strs[8]) : 0D;
											double vy = argCount > 9 ? Double.parseDouble(strs[9]) : 0D;
											double vz = argCount > 10 ? Double.parseDouble(strs[10]) : 0D;
											int maxAge = argCount > 11 ? Integer.parseInt(strs[11]) : 0;
											String speedExpression = argCount > 12 ? strs[12] : null;
											double speedStep = argCount > 13 ? Double.parseDouble(strs[13]) : 0D;
											spawnParticle(message.id, message.xCoord, message.yCoord, message.zCoord, x,
													y, z, message.matrix, red, green, blue, alpha, brightness, vx, vy,
													vz, maxAge, speedExpression, speedStep, null, message.group,
													message.arguments);
										} else {
											ClientMessageUtil.addChatMessage("Bad File:" + message.imagePath);
										}
									}
								} catch (IOException e) {
									ClientMessageUtil.addChatMessage(e.getMessage());
									e.printStackTrace();
								}
							}
						} else {
							ClientMessageUtil.addChatMessage("Not A File:" + message.imagePath);
						}
					} else if (ones) {
						ParticleNetworkHandler.INSTANCE.sendMessageToServer(
								new RequestDataMessage(message.functionType, message.imagePath, message));
					} else {
						ClientMessageUtil.addChatMessage("File Not Found:" + message.imagePath);
					}
					break;
				}
				default:
					ClientMessageUtil.addChatMessage("Function Type Error");
					break;
				}
			}
			return null;
		}

		private double[] parseD(String str, int count) {
			double[] ds = new double[count];
			String[] strs = str.split("@");
			if (strs.length != count) {
				ClientMessageUtil.addChatMessage("ERROR:" + str);
			} else {
				for (int i = 0; i < count; i++) {
					ds[i] = Double.parseDouble(strs[i]);
				}
			}
			return ds;
		}

		private String[] parseS(String str, int count) {
			String[] strs = str.split("@");
			if (strs.length != count) {
				ClientMessageUtil.addChatMessage("ERROR:" + str);
				return new String[count];
			}
			return strs;
		}

		@SideOnly(Side.CLIENT)
		private void spawnImage(int id, double xCoord, double yCoord, double zCoord, FunctionType functionType,
				String imagePath, double proportion, int rotate, int overturn, Matrix matrix, double step,
				int brightness, double motionX, double motionY, double motionZ, int maxAge, String speedExpression,
				double speedStep, String group, int... arguments) {
			File imageFile = new File(imageDir, imagePath);
			if (imageFile.exists()) {
				if (imageFile.isFile()) {
					try {
						BufferedImage dbi = readImage(imageFile, proportion, true);
						int dw = dbi.getWidth();
						int dh = dbi.getHeight();
						for (int row = 0; row < dh; row++) {
							for (int col = 0; col < dw; col++) {
								int pixel = dbi.getRGB(col, row);
								float alpha = getAlpha(pixel);
								float red = getRed(pixel);
								float green = getGreen(pixel);
								float blue = getBlue(pixel);
								double[] pos = getPos(dw, dh, row, col, step, functionType, rotate, overturn);
								double dx = pos[0];
								double dy = pos[1];
								double dz = pos[2];
								if (alpha != 0) {
									spawnParticle(id, xCoord, yCoord, zCoord, dx, dy, dz, matrix, red, green, blue,
											alpha, brightness, motionX, motionY, motionZ, maxAge, speedExpression,
											speedStep, null, group, arguments);
								}
							}
						}
					} catch (IOException e) {
						ClientMessageUtil.addChatMessage(e.getMessage());
						e.printStackTrace();
					}
				} else if (imageFile.isDirectory()) {
					int[][] picturess = null;
					int w = -1;
					int h = -1;
					if (imagesBuf.containsKey(imageFile) && imagesBuf.get(imageFile).containsKey(proportion)) {
						picturess = imagesBuf.get(imageFile).get(proportion);
						int[] wh = imagesWHBuf.get(imageFile).get(proportion);
						w = wh[0];
						h = wh[1];
					} else {
						File[] imageFiles = imageFile.listFiles(file -> file.isFile());
						for (int i = 0; i < imageFiles.length; i++) {
							try {
								BufferedImage dbi = readImage(imageFiles[i], proportion, false);
								int dw = dbi.getWidth();
								int dh = dbi.getHeight();
								if (w == -1) {
									w = dw;
								} else if (w != dw) {
									ClientMessageUtil.addChatMessage("Bad Images:" + imagePath + "(disunify width)");
									continue;
								}
								if (h == -1) {
									h = dh;
								} else if (h != dh) {
									ClientMessageUtil.addChatMessage("Bad Images:" + imagePath + "(disunify height)");
									continue;
								}
								if (picturess == null) {
									picturess = new int[w * h][];
									for (int j = 0; j < picturess.length; j++) {
										picturess[j] = new int[imageFiles.length];
									}
								}
								for (int row = 0; row < dh; row++) {
									for (int col = 0; col < dw; col++) {
										picturess[row * w + col][i] = dbi.getRGB(col, row);
									}
								}
							} catch (IOException e) {
								ClientMessageUtil.addChatMessage(e.getMessage());
								e.printStackTrace();
							}
						}
						if (imagesBuf.containsKey(imageFile)) {
							imagesBuf.get(imageFile).put(proportion, picturess);
							imagesWHBuf.get(imageFile).put(proportion, new int[] { w, h });
						} else {
							Map<Double, int[][]> b = Maps.newHashMap();
							Map<Double, int[]> whb = Maps.newHashMap();
							b.put(proportion, picturess);
							whb.put(proportion, new int[] { w, h });
							imagesBuf.put(imageFile, b);
							imagesWHBuf.put(imageFile, whb);
						}
					}
					for (int row = 0; row < h; row++) {
						for (int col = 0; col < w; col++) {
							int pixel = picturess[row * w + col][0];
							float alpha = getAlpha(pixel);
							float red = getRed(pixel);
							float green = getGreen(pixel);
							float blue = getBlue(pixel);
							double[] pos = getPos(w, h, row, col, step, functionType, rotate, overturn);
							double dx = pos[0];
							double dy = pos[1];
							double dz = pos[2];
							spawnParticle(id, xCoord, yCoord, zCoord, dx, dy, dz, matrix, red, green, blue, alpha,
									brightness, motionX, motionY, motionZ, maxAge, speedExpression, speedStep,
									picturess[row * w + col], group, arguments);
						}
					}
				}
			} else {
				ParticleNetworkHandler.INSTANCE
						.sendMessageToServer(new RequestDataMessage(functionType, imagePath, null));
				// ClientMessageUtil.addChatMessage("File Not Found:" + imagePath);
			}
		}

		private float getAlpha(int pixel) {
			return ((pixel & 0xff000000) >>> 24) / 255F;
		}

		private float getRed(int pixel) {
			return ((pixel & 0xff0000) >>> 16) / 255F;
		}

		private float getGreen(int pixel) {
			return ((pixel & 0xff00) >>> 8) / 255F;
		}

		private float getBlue(int pixel) {
			return (pixel & 0xff) / 255F;
		}

		private double[] getPos(int dw, int dh, int row, int col, double step, FunctionType functionType, int rotate,
				int overturn) {
			double x = col * step;
			double y = (dh - 1 - row) * step;
			double z = 0;
			double temp;
			switch (rotate) {
			case 1:
				temp = x;
				x = (dh - 1) * step - y;
				y = temp;
				break;
			case 2:
				x = (dw - 1) * step - x;
				y = (dh - 1) * step - y;
				break;
			case 3:
				temp = x;
				x = y;
				y = (dw - 1) * step - temp;
				break;
			}
			switch (overturn) {
			case 1:
				x = (dw - 1) * step - x;
				break;
			case 2:
				y = (dh - 1) * step - y;
				break;
			}
			if (functionType == FunctionType.IMAGEFILEXZ) {
				z = y;
				y = 0;
			} else if (functionType == FunctionType.IMAGEFILEZY) {
				z = x;
				x = 0;
			}
			return new double[] { x, y, z };
		}

		@SideOnly(Side.CLIENT)
		private BufferedImage readImage(File imageFile, double proportion, boolean buffered) throws IOException {
			BufferedImage dbi;
			if (buffered) {
				if (imageBuf.containsKey(imageFile)) {
					Map<Double, BufferedImage> b = imageBuf.get(imageFile);
					if (b.containsKey(proportion)) {
						dbi = b.get(proportion);
					} else {
						BufferedImage bi = b.get(1D);
						int width = bi.getWidth();
						int height = bi.getHeight();
						int dw = (int) (width * proportion);
						int dh = (int) (height * proportion);
						dbi = new BufferedImage(dw, dh, bi.getType());
						Graphics2D g = dbi.createGraphics();
						g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
								RenderingHints.VALUE_INTERPOLATION_BILINEAR);
						g.drawImage(bi, 0, 0, dw, dh, 0, 0, width, height, null);
						g.dispose();
						b.put(proportion, dbi);
					}
				} else {
					Map<Double, BufferedImage> b = Maps.newHashMap();
					BufferedImage bi = ImageIO.read(imageFile);
					if (proportion == 1) {
						dbi = bi;
						b.put(1D, dbi);
					} else {
						int width = bi.getWidth();
						int height = bi.getHeight();
						int dw = (int) (width * proportion);
						int dh = (int) (height * proportion);
						dbi = new BufferedImage(dw, dh, bi.getType());
						Graphics2D g = dbi.createGraphics();
						g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
								RenderingHints.VALUE_INTERPOLATION_BILINEAR);
						g.drawImage(bi, 0, 0, dw, dh, 0, 0, width, height, null);
						g.dispose();
						b.put(proportion, dbi);
						b.put(1D, bi);
						b.put(proportion, dbi);
					}
					imageBuf.put(imageFile, b);
				}
			} else {
				BufferedImage bi = ImageIO.read(imageFile);
				int width = bi.getWidth();
				int height = bi.getHeight();
				int dw = (int) (width * proportion);
				int dh = (int) (height * proportion);
				dbi = new BufferedImage(dw, dh, bi.getType());
				Graphics2D g = dbi.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(bi, 0, 0, dw, dh, 0, 0, width, height, null);
				g.dispose();
			}
			return dbi;
		}

		@SideOnly(Side.CLIENT)
		private void spawnParticle(int id, double bx, double by, double bz, double dx, double dy, double dz,
				Matrix matrix, float red, float green, float blue, float alpha, int brightness, double vx, double vy,
				double vz, int maxAge, String speedExpression, double speedStep, int[] pictures, String group,
				int... arguments) {
			try {
				IParticleFactory iparticlefactory = Minecraft.getMinecraft().effectRenderer.particleTypes.get(id);
				if (iparticlefactory != null) {
					double[] pos = matrix.transform(dx, dy, dz);
					Particle particle = iparticlefactory.createParticle(id, Minecraft.getMinecraft().world, bx + pos[0],
							by + pos[1], bz + pos[2], 0, 0, 0, arguments);
					if (particle != null) {
						particle.setRBGColorF(red, green, blue);
						particle.setAlphaF(alpha);
						particle.brightness = brightness << 16 | brightness;
						if (vx == 0 && vy == 0 && vz == 0) {
							particle.stop = true;
						} else {
							particle.stop = false;
							double vpos[] = matrix.transform(vx, vy, vz);
							particle.motionX = vpos[0];
							particle.motionY = vpos[1];
							particle.motionZ = vpos[2];
						}
						particle.centerX = bx;
						particle.centerY = by;
						particle.centerZ = bz;
						particle.matrix = matrix;
						if (maxAge > 0) {
							particle.particleMaxAge = maxAge;
						} else if (maxAge == -1) {
							particle.particleMaxAge = Integer.MAX_VALUE;
							particle.autoDestory = false;
						}
						if (speedExpression != null && !speedExpression.equals("null")) {
							particle.exe = ExpressionUtil.prase(speedExpression, false);
							particle.step = speedStep;
							particle.customMove = true;
						}
						if (pictures != null) {
							particle.pictures = pictures;
							particle.customMove = true;
						}
						Minecraft.getMinecraft().effectRenderer.addEffect(particle);
						if (group != null) {
							String[] strs = group.split(",");
							for (String str : strs) {
								if (!groups.containsKey(str)) {
									groups.put(str, Lists.newArrayList());
								}
								groups.get(str).add(particle);
							}
						}
					}
				}
			} catch (RuntimeException e) {
				ClientMessageUtil.addChatMessage(e.getMessage());
			}
		}

		@SideOnly(Side.CLIENT)
		private void clearCache() {
			imageBuf.clear();
			imagesBuf.clear();
			imagesWHBuf.clear();
		}

		@SideOnly(Side.CLIENT)
		private void clearParticle() {
			for (MG mg : mgSet) {
				mg.setFinish();
			}
			mgSet.clear();
			for (List<Particle> particles : groups.values()) {
				particles.clear();
			}
			groups.clear();
			Minecraft.getMinecraft().effectRenderer.clearEffects(Minecraft.getMinecraft().world);
		}

		@SideOnly(Side.CLIENT)
		private void removeParticles(double cx, double cy, double cz, String group, String expression) {
			if (!groups.containsKey(group)) {
				return;
			}
			List<Particle> particles = groups.get(group);
			if (expression != null) {
				IExecutable exe = ExpressionUtil.prase(expression, true);
				exe.put("PI", Math.PI);
				exe.put("E", Math.E);
				for (Particle particle : particles) {
					if (particle.isAlive()) {
						double dx = particle.posX - cx;
						double dy = particle.posY - cy;
						double dz = particle.posZ - cz;
						exe.put("x", dx);
						exe.put("y", dy);
						exe.put("z", dz);
						exe.put("θ1", Math.atan2(dz, dx));
						exe.put("θ2", Math.atan2(dy, Math.hypot(dx, dz)));
						exe.put("r", Math.sqrt(dx * dx + dy * dy + dz * dz));
						exe.put("age", particle.pAge);
						if ((Boolean) exe.invoke()) {
							particle.setExpired();
						}
					}
				}
				particles.removeIf(particle -> !particle.isAlive());
			} else {
				for (Particle particle : particles) {
					particle.setExpired();
				}
				particles.clear();
			}
		}

		@SideOnly(Side.CLIENT)
		private void changeParticles(double cx, double cy, double cz, String group, ChangeType changeType,
				String changeExpression, String expression) {
			if (!groups.containsKey(group)) {
				return;
			}
			List<Particle> particles = groups.get(group);
			particles.removeIf(particle -> !particle.isAlive());
			IExecutable cexe = ExpressionUtil.prase(changeExpression, false);
			if (cexe != null) {
				cexe.put("PI", Math.PI);
				cexe.put("E", Math.E);
			}
			IExecutable exe = ExpressionUtil.prase(expression, true);
			if (exe != null) {
				exe.put("PI", Math.PI);
				exe.put("E", Math.E);
			}
			for (Particle particle : particles) {
				if (exe != null) {
					double dx = particle.posX - cx;
					double dy = particle.posY - cy;
					double dz = particle.posZ - cz;
					exe.put("x", dx);
					exe.put("y", dy);
					exe.put("z", dz);
					exe.put("θ1", Math.atan2(dz, dx));
					exe.put("θ2", Math.atan2(dy, Math.hypot(dx, dz)));
					exe.put("r", Math.sqrt(dx * dx + dy * dy + dz * dz));
					if ((Boolean) exe.invoke()) {
						changeParticle(particle, cx, cy, cz, changeType, cexe);
					}
				} else {
					changeParticle(particle, cx, cy, cz, changeType, cexe);
				}
			}
		}

		private void changeParticle(Particle particle, double cx, double cy, double cz, ChangeType changeType,
				IExecutable cexe) {
			if (cexe != null) {
				cexe.put("x", particle.posX - cx);
				cexe.put("y", particle.posY - cy);
				cexe.put("z", particle.posZ - cz);
				cexe.put("vx", particle.motionX);
				cexe.put("vy", particle.motionY);
				cexe.put("vz", particle.motionZ);
				cexe.put("cx", particle.centerX);
				cexe.put("cy", particle.centerY);
				cexe.put("cz", particle.centerZ);
				cexe.put("red", (double) particle.getRedColorF());
				cexe.put("green", (double) particle.getGreenColorF());
				cexe.put("blue", (double) particle.getBlueColorF());
				cexe.put("alpha", (double) particle.particleAlpha);
				cexe.put("brightness", (double) particle.brightness);
				cexe.invoke();
			}
			switch (changeType) {
			case POS: {
				if (ColorBlockCore.debug) {
					particle.move2((double) cexe.get("x") - particle.posX + cx,
							(double) cexe.get("y") - particle.posY + cy, (double) cexe.get("z") - particle.posZ + cz);
				} else {
					particle.a2((double) cexe.get("x") - particle.posX + cx,
							(double) cexe.get("y") - particle.posY + cy, (double) cexe.get("z") - particle.posZ + cz);
				}
				break;
			}
			case CENTERPOS: {
				particle.centerX = cx + (double) cexe.get("x");
				particle.centerY = cy + (double) cexe.get("y");
				particle.centerZ = cz + (double) cexe.get("z");
				break;
			}
			case COLOR: {
				particle.setRBGColorF((float) (double) cexe.get("red"), (float) (double) cexe.get("green"),
						(float) (double) cexe.get("blue"));
				particle.particleAlpha = (float) (double) cexe.get("alpha");
				particle.brightness = (int) Math.round((double) cexe.get("brightness"));
				break;
			}
			case SPEED: {
				double vx = (double) cexe.get("vx");
				double vy = (double) cexe.get("vy");
				double vz = (double) cexe.get("vz");
				if (vx == 0 && vy == 0 && vz == 0) {
					particle.stop = true;
				} else {
					particle.stop = false;
				}
				particle.motionX = vx;
				particle.motionY = vy;
				particle.motionZ = vz;
				break;
			}
			case SPEEDEXPRESSION: {
				particle.customMove = cexe != null;
				particle.exe = cexe;
				break;
			}
			default:
				break;
			}
		}

		@SideOnly(Side.CLIENT)
		public static void keyDown(int rail) {
			for (MG mg : mgSet) {
				mg.keyDown(rail);
			}
		}

		@SideOnly(Side.CLIENT)
		public static void keyUp(int rail) {
			for (MG mg : mgSet) {
				mg.keyUp(rail);
			}
		}

		private class MG {

			private final String[] KEYGROUPNAMES = { "_mgkg0", "_mgkg1", "_mgkg2", "_mgkg3", "_mgkg4", "_mgkg5",
					"_mgkg6", "_mgkg7" };

			private String soundName;
			private float volume;
			private int overallTick;
			private int startTick;
			private int railCount;
			private double[] raildxuu;
			private double[] raildxdu;
			private double[] raildyuu;
			private double[] raildydu;
			private double[] raildzuu;
			private double[] raildzdu;
			private double[] raildxu;
			private double[] raildxd;
			private double[] raildyu;
			private double[] raildyd;
			private double[] raildzu;
			private double[] raildzd;
			private int dropSpeed;
			private String background;
			private ParticleMessage backgroundMessage;
			private String dropBlock;
			private ParticleMessage dropBlockMessage;
			private String dropStripStart;
			private ParticleMessage dropStripStartMessage;
			private String dropStripMid;
			private ParticleMessage dropStripMidMessage;
			private String dropStripEnd;
			private ParticleMessage dropStripEndMessage;
			private String keyDown;
			private ParticleMessage keyDownMessage;
			private String perfect;
			private ParticleMessage perfectMessage;
			private String good;
			private ParticleMessage goodMessage;
			private String normal;
			private ParticleMessage normalMessage;
			private String bad;
			private ParticleMessage badMessage;
			private String miss;
			private ParticleMessage missMessage;
			private String[] numStr;
			private ParticleMessage[] numStrMessage;
			private int particleId;
			private double x;
			private double y;
			private double z;
			private double[] textXu;
			private double[] textYu;
			private double[] textZu;
			private double[] textX;
			private double[] textY;
			private double[] textZ;
			private Matrix matrix;
			private double dpb;
			private int brightness;
			private Queue<MGControl> controls;
			private boolean auto;
			private int curTick;
			private boolean[] railStrip;
			private boolean[] railStripNext;
			private boolean[] railStripActive;
			private Queue<MGDroping> droping;
			private int doubleHit;
			private int activeCount;
			private boolean[] key;
			private boolean finish;
			private boolean drawNum;
			private boolean clearNum;

			public MG(String soundName, float volume, int overallTick, int startTick, int railCount, double[] raildxu,
					double[] raildxd, double[] raildyu, double[] raildyd, double[] raildzu, double[] raildzd,
					int dropSpeed, String background, String dropBlock, String dropStripStart, String dropStripMid,
					String dropStripEnd, String keyDown, String perfect, String good, String normal, String bad,
					String miss, String[] numStr, int particleId, double x, double y, double z, double[] textX,
					double[] textY, double[] textZ, Matrix matrix, double dpb, int brightness,
					Queue<MGControl> controls, boolean auto) {
				for (MGControl mgc : controls) {
					if (mgc.rail >= railCount) {
						mgc.rail = mgc.rail % railCount;
					}
				}
				this.soundName = soundName;
				this.volume = volume;
				this.overallTick = overallTick;
				this.startTick = startTick;
				this.railCount = railCount;
				this.raildxuu = raildxu;
				this.raildxdu = raildxd;
				this.raildyuu = raildyu;
				this.raildydu = raildyd;
				this.raildzuu = raildzu;
				this.raildzdu = raildzd;
				this.dropSpeed = dropSpeed;
				this.background = background;
				this.backgroundMessage = parseMessage(background, matrix);
				this.dropBlock = dropBlock;
				this.dropBlockMessage = parseMessage(dropBlock, matrix);
				this.dropStripStart = dropStripStart;
				this.dropStripStartMessage = parseMessage(dropStripStart, matrix);
				this.dropStripMid = dropStripMid;
				this.dropStripMidMessage = parseMessage(dropStripMid, matrix);
				this.dropStripEnd = dropStripEnd;
				this.dropStripEndMessage = parseMessage(dropStripEnd, matrix);
				this.keyDown = keyDown;
				this.keyDownMessage = parseMessage(keyDown, matrix);
				this.perfect = perfect;
				this.perfectMessage = parseMessage(perfect, matrix);
				this.good = good;
				this.goodMessage = parseMessage(good, matrix);
				this.normal = normal;
				this.normalMessage = parseMessage(normal, matrix);
				this.bad = bad;
				this.badMessage = parseMessage(bad, matrix);
				this.miss = miss;
				this.missMessage = parseMessage(miss, matrix);
				this.numStr = numStr;
				this.numStrMessage = parseMessage(numStr, matrix);
				this.particleId = particleId;
				this.x = x;
				this.y = y;
				this.z = z;
				this.textXu = textX;
				this.textYu = textY;
				this.textZu = textZ;
				this.matrix = matrix;
				this.dpb = dpb;
				this.brightness = brightness;
				this.controls = controls;
				this.auto = auto;
				this.curTick = startTick;
				this.railStrip = new boolean[railCount];
				this.railStripNext = new boolean[railCount];
				this.railStripActive = new boolean[railCount];
				this.droping = Queues.newArrayDeque();
				this.doubleHit = 0;
				this.activeCount = 0;
				this.drawNum = false;
				this.key = new boolean[railCount];
				this.finish = false;
				this.raildxu = new double[railCount];
				this.raildxd = new double[railCount];
				this.raildyu = new double[railCount];
				this.raildyd = new double[railCount];
				this.raildzu = new double[railCount];
				this.raildzd = new double[railCount];
				this.textX = new double[8];
				this.textY = new double[8];
				this.textZ = new double[8];
				transformPoss(this.raildxuu, this.raildyuu, this.raildzuu, this.raildxu, this.raildyu, this.raildzu,
						matrix);
				transformPoss(this.raildxdu, this.raildydu, this.raildzdu, this.raildxd, this.raildyd, this.raildzd,
						matrix);
				transformPoss(this.textXu, this.textYu, this.textZu, this.textX, this.textY, this.textZ, matrix);
				mgSet.add(this);
			}

			@SideOnly(Side.CLIENT)
			private ParticleMessage[] parseMessage(String[] strs, Matrix matrix) {
				ParticleMessage[] messages = new ParticleMessage[strs.length];
				for (int i = 0; i < strs.length; i++) {
					messages[i] = parseMessage(strs[i], matrix);
				}
				return messages;
			}

			@SideOnly(Side.CLIENT)
			private ParticleMessage parseMessage(String str, Matrix matrix) {
				if (str != null && str.startsWith("/particleex")) {
					try {
						ParticleMessage message = CommandParticleEx.parseArgs(str.substring(12), 0, 0, 0);
						if (message.functionType != FunctionType.MGFILE) {
							message.matrix = matrix.transform(message.matrix);
							double[] pos = matrix.transform(message.xCoord, message.yCoord, message.zCoord);
							message.xCoord = pos[0];
							message.yCoord = pos[1];
							message.zCoord = pos[2];
							return message;
						}
					} catch (CommandException e) {
						ClientMessageUtil.addChatMessage(e.getMessage());
					}
				}
				return null;
			}

			@SideOnly(Side.CLIENT)
			private void transformPoss(double[] xs, double[] ys, double[] zs, double[] oxs, double[] oys, double[] ozs,
					Matrix matrix) {
				if (xs.length == ys.length && ys.length == zs.length && oxs.length == xs.length
						&& oys.length == ys.length && ozs.length == zs.length) {
					for (int i = 0; i < xs.length; i++) {
						double[] transformed = matrix.transform(xs[i], ys[i], zs[i]);
						oxs[i] = transformed[0];
						oys[i] = transformed[1];
						ozs[i] = transformed[2];
					}
				}
			}

			@SideOnly(Side.CLIENT)
			private ParticleMessage transformMessagePos(ParticleMessage message, double x, double y, double z,
					String group) {
				ParticleMessage transformed = new ParticleMessage(message);
				transformed.xCoord += x;
				transformed.yCoord += y;
				transformed.zCoord += z;
				transformed.group = group;
				return transformed;
			}

			@SideOnly(Side.CLIENT)
			public void runTick() {
				if (finish) {
					mgSet.remove(this);
					return;
				}
				if (curTick == startTick) {
					drawBG();
				}
				if (clearNum) {
					MessageHandler.this.removeParticles(0, 0, 0, "_mgnum", "age>=1");
					clearNum = false;
				}
				drawBlock();
				drawStrip();
				drawKey();
				drawStripText();
				Queue<MGDroping> droped = Queues.newArrayDeque();
				int[] keyType = new int[railCount];
				while (!droping.isEmpty()) {
					MGDroping mgd = droping.poll();
					if (mgd.drop()) {
						droped.add(mgd);
					} else {
						if (mgd.type == MGType.CONTINUOUSEND) {
							railStripActive[mgd.rail] = false;
						} else if (!mgd.actived) {
							clearDoubleHit();
							MessageHandler.this.removeParticles(0, 0, 0, "_mgtext", "age>=1");
							if (missMessage != null) {
								MessageHandler.this.onMessage(transformMessagePos(missMessage, x + textX[4],
										y + textY[4], z + textZ[4], "_mgtext"), null);
							} else {
								spawnImage(particleId, x + textX[4], y + textY[4], z + textZ[4], FunctionType.IMAGEFILE,
										miss, 1, 0, 0, matrix, dpb, brightness, 0, 0, 0, 10, "vy=0", 1, "_mgtext");
							}
						}
					}
					if (auto && mgd.perfect()) {
						switch (mgd.type) {
						case SINGLE:
							keyType[mgd.rail] = 1;
							break;
						case CONTINUOUSSTART:
							keyType[mgd.rail] = 2;
							break;
						case CONTINUOUSEND:
							keyType[mgd.rail] = 3;
							break;
						}
					}
				}
				droping = droped;
				if (auto) {
					for (int i = 0; i < railCount; i++) {
						switch (keyType[i]) {
						case 1:
							down(i);
							up(i);
							break;
						case 2:
							down(i);
							break;
						case 3:
							up(i);
							break;
						}
					}
				}
				for (int i = 0; i < railCount; i++) {
					railStrip[i] = railStripNext[i];
				}
				if (drawNum) {
					drawNum(doubleHit, 10, false);
					drawNum = false;
					clearNum = true;
				}
				curTick++;
				if (curTick >= overallTick) {
					drawNum(activeCount, 100, true);
					mgSet.remove(this);
				}
			}

			@SideOnly(Side.CLIENT)
			private void drawBG() {
				if (backgroundMessage != null) {
					MessageHandler.this.onMessage(transformMessagePos(backgroundMessage, x, y, z, "_mgbg"), null);
				} else {
					spawnImage(particleId, x, y, z, FunctionType.IMAGEFILE, background, 1, 0, 0, matrix, dpb,
							brightness, 0, 0, 0, -1, "destory=floor(t/" + (overallTick + 3) + ");vy=0", 1, "_mgbg");
				}
				playSound(soundName, volume);
			}

			@SideOnly(Side.CLIENT)
			private void drawBlock() {
				while (!controls.isEmpty() && controls.peek().tick <= curTick + dropSpeed + 1) {
					MGControl mgc = controls.poll();
					droping.add(new MGDroping(dropSpeed + 2, mgc.type, mgc.rail, mgc.tones, mgc.volumes));
					double vx = (raildxdu[mgc.rail] - raildxuu[mgc.rail]) / dropSpeed;
					double vy = (raildydu[mgc.rail] - raildyuu[mgc.rail]) / dropSpeed;
					double vz = (raildzdu[mgc.rail] - raildzuu[mgc.rail]) / dropSpeed;
					switch (mgc.type) {
					case SINGLE: {
						if (dropBlockMessage != null) {
							MessageHandler.this.onMessage(transformMessagePos(dropBlockMessage, x + raildxu[mgc.rail],
									y + raildyu[mgc.rail], z + raildzu[mgc.rail], null), null);
						} else {
							spawnImage(particleId, x + raildxu[mgc.rail], y + raildyu[mgc.rail], z + raildzu[mgc.rail],
									FunctionType.IMAGEFILE, dropBlock, 1, 0, 0, matrix, dpb, brightness, 0, 0, 0, -1,
									"destory=floor(t/" + (dropSpeed + 3) + ");vx=" + vx + ";vy=" + vy + ";vz=" + vz, 1,
									null);
						}
						break;
					}
					case CONTINUOUSSTART: {
						railStripNext[mgc.rail] = true;
						if (dropStripStartMessage != null) {
							MessageHandler.this.onMessage(transformMessagePos(dropStripStartMessage,
									x + raildxu[mgc.rail], y + raildyu[mgc.rail], z + raildzu[mgc.rail], null), null);
						} else {
							spawnImage(particleId, x + raildxu[mgc.rail], y + raildyu[mgc.rail], z + raildzu[mgc.rail],
									FunctionType.IMAGEFILE, dropStripStart, 1, 0, 0, matrix, dpb, brightness, 0, 0, 0,
									-1, "destory=floor(t/" + (dropSpeed + 3) + ");vx=" + vx + ";vy=" + vy + ";vz=" + vz,
									1, null);
						}
						break;
					}
					case CONTINUOUSEND: {
						railStrip[mgc.rail] = false;
						railStripNext[mgc.rail] = false;
						if (dropStripEndMessage != null) {
							MessageHandler.this.onMessage(transformMessagePos(dropStripEndMessage,
									x + raildxu[mgc.rail], y + raildyu[mgc.rail], z + raildzu[mgc.rail], null), null);
						} else {
							spawnImage(particleId, x + raildxu[mgc.rail], y + raildyu[mgc.rail], z + raildzu[mgc.rail],
									FunctionType.IMAGEFILE, dropStripEnd, 1, 0, 0, matrix, dpb, brightness, 0, 0, 0, -1,
									"destory=floor(t/" + (dropSpeed + 3) + ");vx=" + vx + ";vy=" + vy + ";vz=" + vz, 1,
									null);
						}
						break;
					}
					default:
						break;
					}
				}
			}

			@SideOnly(Side.CLIENT)
			private void drawStrip() {
				for (int i = 0; i < railCount; i++) {
					if (railStrip[i]) {
						double vx = (raildxdu[i] - raildxuu[i]) / dropSpeed;
						double vy = (raildydu[i] - raildyuu[i]) / dropSpeed;
						double vz = (raildzdu[i] - raildzuu[i]) / dropSpeed;
						if (dropStripMidMessage != null) {
							MessageHandler.this.onMessage(transformMessagePos(dropStripMidMessage, x + raildxu[i],
									y + raildyu[i], z + raildzu[i], null), null);
						} else {
							spawnImage(particleId, x + raildxu[i], y + raildyu[i], z + raildzu[i],
									FunctionType.IMAGEFILE, dropStripMid, 1, 0, 0, matrix, dpb, brightness, 0, vy, 0,
									-1, "destory=floor(t/" + (dropSpeed + 3) + ");vx=" + vx + ";vy=" + vy + ";vz=" + vz,
									1, null);
						}
					}
				}
			}

			@SideOnly(Side.CLIENT)
			private void drawKey() {
				for (int i = 0; i < railCount; i++) {
					if (!key[i]) {
						MessageHandler.this.removeParticles(0, 0, 0, KEYGROUPNAMES[i], "age>=1");
					}
				}
			}

			@SideOnly(Side.CLIENT)
			private void drawStripText() {
				for (int i = 0; i < railCount; i++) {
					if (key[i] && railStripActive[i]) {
						addDoubleHit();
						MessageHandler.this.removeParticles(0, 0, 0, "_mgtext", "age>=1");
						if (perfectMessage != null) {
							MessageHandler.this.onMessage(transformMessagePos(perfectMessage, x + textX[0],
									y + textY[0], z + textZ[0], "_mgtext"), null);
						} else {
							spawnImage(particleId, x + textX[0], y + textY[0], z + textZ[0], FunctionType.IMAGEFILE,
									perfect, 1, 0, 0, matrix, dpb, brightness, 0, 0, 0, 10, "vy=0", 1, "_mgtext");
						}
					}
				}
			}

			@SideOnly(Side.CLIENT)
			private void drawNum(int num, int tick, boolean over) {
				if (num < 0) {
					return;
				}
				MessageHandler.this.removeParticles(0, 0, 0, "_mgnum", "age>=1");
				String nums = String.valueOf(num);
				for (int i = 0; i < nums.length(); i++) {
					double nx = over ? textX[6] : textX[5];
					double ny = over ? textY[6] : textY[5];
					double nz = over ? textZ[6] : textZ[5];
					if (numStrMessage[nums.charAt(i) - '0'] != null) {
						MessageHandler.this.onMessage(transformMessagePos(numStrMessage[nums.charAt(i) - '0'],
								x + nx + textX[7] * i, y + ny + textY[7] * i, z + nz + textZ[7] * i, "_mgnum"), null);
					} else {
						spawnImage(particleId, x + nx + textX[7] * i, y + ny + textY[7] * i, z + nz + textZ[7] * i,
								FunctionType.IMAGEFILE, numStr[nums.charAt(i) - '0'], 1, 0, 0, matrix, dpb, brightness,
								0, 0, 0, tick, "vy=0", 1, "_mgnum");
					}
				}
			}

			@SideOnly(Side.CLIENT)
			private void playSound(String soundName, float volume) {
				if (soundName != null) {
					float x = (float) Minecraft.getMinecraft().player.posX;
					float y = (float) Minecraft.getMinecraft().player.posY;
					float z = (float) Minecraft.getMinecraft().player.posZ;
					Minecraft.getMinecraft().getSoundHandler()
							.playSound(new PositionedSoundRecord(new ResourceLocation(soundName), SoundCategory.PLAYERS,
									volume, 1F, false, 0, ISound.AttenuationType.LINEAR, x, y, z));
				}
			}

			@SideOnly(Side.CLIENT)
			public void setFinish() {
				finish = true;
			}

			@SideOnly(Side.CLIENT)
			public boolean timeOver() {
				return curTick >= overallTick || finish;
			}

			@SideOnly(Side.CLIENT)
			public void keyDown(int rail) {
				if (!auto) {
					down(rail);
				}
			}

			@SideOnly(Side.CLIENT)
			private void down(int rail) {
				if (rail < railCount) {
					key[rail] = true;
					if (keyDownMessage != null) {
						ParticleMessage keyMessage = transformMessagePos(keyDownMessage, x + raildxd[rail],
								y + raildyd[rail], z + raildzd[rail], KEYGROUPNAMES[rail]);
						MessageHandler.this.onMessage(keyMessage, null);
					} else {
						spawnImage(particleId, x + raildxd[rail], y + raildyd[rail], z + raildzd[rail],
								FunctionType.IMAGEFILE, keyDown, 1, 0, 0, matrix, dpb, brightness, 0, 0, 0, 2, "vy=0",
								1, KEYGROUPNAMES[rail]);
					}
					for (MGDroping mgd : droping) {
						if (mgd.rail == rail && mgd.canActive()) {
							addDoubleHit();
							String pictureName;
							ParticleMessage message;
							int pictureIndex = mgd.active();
							switch (pictureIndex) {
							case 0:
								pictureName = perfect;
								message = perfectMessage;
								break;
							case 1:
								pictureName = good;
								message = goodMessage;
								break;
							case 2:
								pictureName = normal;
								message = normalMessage;
								break;
							case 3:
							default:
								pictureName = bad;
								message = badMessage;
								break;
							}
							if (mgd.type == MGType.CONTINUOUSSTART) {
								railStripActive[mgd.rail] = true;
							}
							MessageHandler.this.removeParticles(0, 0, 0, "_mgtext", "age>=1");
							if (message != null) {
								MessageHandler.this.onMessage(transformMessagePos(message, x + textX[pictureIndex],
										y + textY[pictureIndex], z + textZ[pictureIndex], "_mgtext"), null);
							} else {
								spawnImage(particleId, x + textX[pictureIndex], y + textY[pictureIndex],
										z + textZ[pictureIndex], FunctionType.IMAGEFILE, pictureName, 1, 0, 0, matrix,
										dpb, brightness, 0, 0, 0, 10, "vy=0", 1, "_mgtext");
							}
							for (int i = 0; i < mgd.tones.length; i++) {
								playSound(mgd.tones[i], mgd.volumes[i]);
							}
							break;
						}
					}
				}
			}

			@SideOnly(Side.CLIENT)
			public void keyUp(int rail) {
				if (!auto) {
					up(rail);
				}
			}

			@SideOnly(Side.CLIENT)
			private void up(int rail) {
				if (rail < railCount) {
					key[rail] = false;
					MessageHandler.this.removeParticles(0, 0, 0, KEYGROUPNAMES[rail], "age>=1");
					if (railStripActive[rail]) {
						clearDoubleHit();
						railStripActive[rail] = false;
						MessageHandler.this.removeParticles(0, 0, 0, "_mgtext", "age>=1");
						if (missMessage != null) {
							MessageHandler.this.onMessage(transformMessagePos(missMessage, x + textX[4], y + textY[4],
									z + textZ[4], "_mgtext"), null);
						} else {
							spawnImage(particleId, x + textX[4], y + textY[4], z + textZ[4], FunctionType.IMAGEFILE,
									miss, 1, 0, 0, matrix, dpb, brightness, 0, 0, 0, 10, "vy=0", 1, "_mgtext");
						}
					}
				}
			}

			@SideOnly(Side.CLIENT)
			private void addDoubleHit() {
				doubleHit++;
				activeCount++;
				drawNum = true;
			}

			@SideOnly(Side.CLIENT)
			private void clearDoubleHit() {
				doubleHit = 0;
				drawNum = true;
			}

		}

		private class MGDroping {

			private int tick;
			private MGType type;
			private int rail;
			private String[] tones;
			private float[] volumes;
			private boolean actived;

			public MGDroping(int tick, MGType type, int rail, String[] tones, float[] volumes) {
				this.tick = tick;
				this.type = type;
				this.rail = rail;
				this.tones = tones;
				this.volumes = volumes;
				this.actived = false;
			}

			public boolean drop() {
				tick--;
				return type == MGType.CONTINUOUSEND ? tick >= 0 : tick >= -3;
			}

			public boolean perfect() {
				return type == MGType.CONTINUOUSEND ? tick == -1 : tick == 0;
			}

			public boolean canActive() {
				return !actived && tick >= -3 && tick <= 3;
			}

			public int active() {
				actived = true;
				return Math.abs(tick);
			}

		}

		private class MGControl {

			private int tick;
			private MGType type;
			private int rail;
			private String[] tones;
			private float[] volumes;

			public MGControl(int tick, MGType type, int rail, String[] tones, float[] volumes) {
				this.tick = tick;
				this.type = type;
				this.rail = rail;
				this.tones = tones;
				this.volumes = volumes;
			}

		}

		private enum MGType {

			SINGLE("single"), CONTINUOUSSTART("continuousstart"), CONTINUOUSEND("continuousend");

			private static final Map<String, MGType> BY_NAME = Maps.newHashMap();

			private String name;

			private MGType(String name) {
				this.name = name;
			}

			public String getName() {
				return name;
			}

			public static MGType getByName(String name) {
				return BY_NAME.get(name);
			}

			static {
				for (MGType type : values()) {
					BY_NAME.put(type.getName(), type);
				}
			}

		}

		private class TickParticleTask extends TimerTask {

			private boolean isPolar;
			private boolean isRGB;
			private int id;
			private double x;
			private double y;
			private double z;
			private Matrix matrix;
			private float red;
			private float green;
			private float blue;
			private float alpha;
			private int brightness;
			private double vx;
			private double vy;
			private double vz;
			private IExecutable exe;
			private double step;
			private int cpt;
			private double t;
			private double tEnd;
			private int maxAge;
			private String speedExpression;
			private double speedStep;
			private String group;
			private int[] arguments;

			public TickParticleTask(ParticleMessage message, boolean isPolar, boolean isRGB) {
				this.isPolar = isPolar;
				this.isRGB = isRGB;
				this.id = message.id;
				this.x = message.xCoord;
				this.y = message.yCoord;
				this.z = message.zCoord;
				this.matrix = message.matrix;
				this.step = message.step;
				this.cpt = message.cpt;
				this.t = message.tStart;
				this.tEnd = message.tEnd;
				this.maxAge = message.maxAge;
				this.speedExpression = message.speedExpression;
				this.speedStep = message.speedStep;
				this.group = message.group;
				this.arguments = message.arguments;
				this.exe = ExpressionUtil.prase(message.expression, false);
				this.exe.put("PI", Math.PI);
				this.exe.put("E", Math.E);
				if (isPolar) {
					this.exe.put("θ1", 0D);
					this.exe.put("θ2", 0D);
					this.exe.put("r", 0D);
				} else {
					this.exe.put("x", 0D);
					this.exe.put("y", 0D);
					this.exe.put("z", 0D);
				}
				if (isRGB) {
					exe.put("red", 1D);
					exe.put("green", 1D);
					exe.put("blue", 1D);
					exe.put("alpha", 1D);
					exe.put("brightness", 240D);
					exe.put("vx", 0D);
					exe.put("vy", 0D);
					exe.put("vz", 0D);
				} else {
					this.red = message.red;
					this.green = message.green;
					this.blue = message.blue;
					this.alpha = message.alpha;
					this.brightness = message.brightness;
					this.vx = message.motionX;
					this.vy = message.motionY;
					this.vz = message.motionZ;
				}
			}

			@Override
			public void run() {
				for (int i = 0; i < cpt && t <= tEnd; i++, t += step) {
					exe.put("t", t);
					exe.invoke();
					double dx;
					double dy;
					double dz;
					if (isPolar) {
						double θ1 = (double) exe.get("θ1");
						double θ2 = (double) exe.get("θ2");
						double dr = (double) exe.get("r");
						dx = dr * Math.cos(θ2) * Math.cos(θ1);
						dy = dr * Math.sin(θ2);
						dz = dr * Math.cos(θ2) * Math.sin(θ1);
					} else {
						dx = (double) exe.get("x");
						dy = (double) exe.get("y");
						dz = (double) exe.get("z");
					}
					if (isRGB) {
						double red = (double) exe.get("red");
						double green = (double) exe.get("green");
						double blue = (double) exe.get("blue");
						double alpha = (double) exe.get("alpha");
						double brightness = (double) exe.get("brightness");
						double vx = (double) exe.get("vx");
						double vy = (double) exe.get("vy");
						double vz = (double) exe.get("vz");
						spawnParticle(id, x, y, z, dx, dy, dz, matrix, (float) red, (float) green, (float) blue,
								(float) alpha, (int) Math.round(brightness), vx, vy, vz, maxAge, speedExpression,
								speedStep, null, group, arguments);
					} else {
						spawnParticle(id, x, y, z, dx, dy, dz, matrix, red, green, blue, alpha,
								(int) Math.round(brightness), vx, vy, vz, maxAge, speedExpression, speedStep, null,
								group, arguments);
					}
				}
				if (t <= tEnd) {
					addTask(new TickEndTask(this), Phase.END);
				}
			}

		}

		private class MusicGameTask extends TimerTask {

			private MG mg;

			public MusicGameTask(MG mg) {
				this.mg = mg;
			}

			@Override
			public void run() {
				mg.runTick();
				if (!mg.timeOver()) {
					addTask(new TickEndTask(this), Phase.END);
				}
			}

		}

		private class TickEndTask extends TimerTask {

			private TimerTask nextTask;

			public TickEndTask(TimerTask nextTask) {
				this.nextTask = nextTask;
			}

			@Override
			public void run() {
				addTask(nextTask, Phase.START);
			}

		}

		private static final Queue<TimerTask> tickStartTasks = Queues.newArrayDeque();
		private static final Queue<TimerTask> tickEndTasks = Queues.newArrayDeque();

		@SideOnly(Side.CLIENT)
		private static void addTask(TimerTask task, Phase phase) {
			if (phase == Phase.START) {
				synchronized (tickStartTasks) {
					tickStartTasks.add(task);
				}
			} else {
				synchronized (tickEndTasks) {
					tickEndTasks.add(task);
				}
			}
		}

		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void onClientTick(ClientTickEvent event) {
			if (event.phase == Phase.START) {
				synchronized (tickStartTasks) {
					while (!tickStartTasks.isEmpty()) {
						tickStartTasks.poll().run();
					}
				}
			} else {
				synchronized (tickEndTasks) {
					while (!tickEndTasks.isEmpty()) {
						tickEndTasks.poll().run();
					}
				}
			}
		}

	}

	public static enum FunctionType {

		NORMAL(0, "normal"), FUNCTION(1, "function"), PARAMETER(2, "parameter"), POLARPARAMETER(3,
				"polarParameter"), TICKPARAMETER(4, "tickParameter"), TICKPOLARPARAMETER(5,
						"tickPolarParameter"), PARAMETERRGB(6, "parameterRGB"), POLARPARAMETERRGB(7,
								"polarParameterRGB"), TICKPARAMETERRGB(8, "tickParameterRGB"), TICKPOLARPARAMETERRGB(9,
										"tickPolarParameterRGB"), IMAGEFILEXY(10, "imageFileXY"), IMAGEFILEZY(11,
												"imageFileZY"), IMAGEFILEXZ(12, "imageFileXZ"), IMAGEFILE(13,
														"imageFile"), MGFILE(14, "mgFile"), POSFILE(15, "posFile");

		private static final Map<Integer, FunctionType> PARTICLES = Maps.newHashMap();
		private static final Map<String, FunctionType> BY_NAME = Maps.newHashMap();

		private int id;
		private String name;

		private FunctionType(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public static Set<String> getNames() {
			return BY_NAME.keySet();
		}

		public static FunctionType getById(int id) {
			return PARTICLES.get(id);
		}

		public static FunctionType getByName(String name) {
			return BY_NAME.get(name);
		}

		static {
			for (FunctionType type : values()) {
				PARTICLES.put(type.getId(), type);
				BY_NAME.put(type.getName(), type);
			}
		}

	}

	public static enum ChangeType {

		POS(0, "pos"), CENTERPOS(1, "centerPos"), COLOR(2, "color"), SPEED(3, "speed"), SPEEDEXPRESSION(4,
				"speedExpression");

		private static final Map<Integer, ChangeType> PARTICLES = Maps.newHashMap();
		private static final Map<String, ChangeType> BY_NAME = Maps.newHashMap();

		private int id;
		private String name;

		private ChangeType(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public static Set<String> getNames() {
			return BY_NAME.keySet();
		}

		public static ChangeType getById(int id) {
			return PARTICLES.get(id);
		}

		public static ChangeType getByName(String name) {
			return BY_NAME.get(name);
		}

		static {
			for (ChangeType type : values()) {
				PARTICLES.put(type.getId(), type);
				BY_NAME.put(type.getName(), type);
			}
		}

	}

}
