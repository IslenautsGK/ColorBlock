package com.isgk.colorblock.common.command;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.isgk.colorblock.network.ParticleNetworkHandler;
import com.isgk.colorblock.network.packet.ParticleMessage;
import com.isgk.colorblock.network.packet.ParticleMessage.ChangeType;
import com.isgk.colorblock.network.packet.ParticleMessage.FunctionType;
import com.isgk.colorblock.util.expression.IExpression;
import com.isgk.colorblock.util.expression.IExpression.Function;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;

public class CommandParticleEx extends CommandBase {

	@Override
	public String getName() {
		return "particleex";
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}

	public String getUsage(ICommandSender sender) {
		return "commands.particleex.usage";
	}

	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 5 && args[0].equals("group")) {
			Vec3d vec3d = sender.getPositionVector();
			double x = parseDouble(vec3d.x, args[1], true);
			double y = parseDouble(vec3d.y, args[2], true);
			double z = parseDouble(vec3d.z, args[3], true);
			String group = args[4];
			switch (args[5]) {
			case "remove": {
				String expression = null;
				if (args.length > 6) {
					expression = args[6];
				}
				if (args.length > 7) {
					throw new WrongUsageException("commands.particleex.group.remove.usage");
				}
				ParticleNetworkHandler.INSTANCE.sendMessageToAll(new ParticleMessage(x, y, z, group, expression));
				break;
			}
			case "change": {
				if (args.length < 8) {
					throw new WrongUsageException("commands.particleex.group.change.usage");
				}
				ChangeType changeType = ChangeType.getByName(args[6]);
				if (changeType == null) {
					throw new WrongUsageException("commands.particleex.group.change.usage");
				}
				String changeExpression = args[7];
				String expression = null;
				if (args.length > 8) {
					expression = args[8];
				}
				if (args.length > 9) {
					throw new WrongUsageException("commands.particleex.group.change.usage");
				}
				ParticleNetworkHandler.INSTANCE.sendMessageToAll(
						new ParticleMessage(x, y, z, group, changeType, changeExpression, expression));
				break;
			}
			default:
				throw new WrongUsageException("commands.particleex.usage");
			}
		} else if (args.length > 4) {
			ParticleNetworkHandler.INSTANCE.sendMessageToAll(parseArgs(args, sender.getPositionVector()));
		} else if (args.length == 1) {
			if (args[0].equals("funlist")) {
				StringBuilder sb = new StringBuilder();
				for (Function fun : IExpression.Function.values()) {
					sb.append(fun.getName());
					if (fun.getParamCount() == 1) {
						sb.append("(x),");
					} else {
						sb.append("(x,y),");
					}
				}
				sender.sendMessage(new TextComponentString(sb.substring(0, sb.length() - 1)));
			} else if (args[0].equals("clearcache")) {
				ParticleNetworkHandler.INSTANCE.sendMessageToAll(new ParticleMessage(-1));
			} else if (args[0].equals("clearparticle")) {
				ParticleNetworkHandler.INSTANCE.sendMessageToAll(new ParticleMessage(-2));
			}
		} else {
			throw new WrongUsageException("commands.particleex.usage");
		}
	}

	public static ParticleMessage parseArgs(String argsStr, double x, double y, double z) throws CommandException {
		return parseArgs(argsStr.split(" "), new Vec3d(x, y, z));
	}

	private static ParticleMessage parseArgs(String[] args, Vec3d vec3d) throws CommandException {
		EnumParticleTypes enumparticletypes = EnumParticleTypes.getByName(args[0]);
		if (enumparticletypes == null) {
			throw new CommandException("commands.particle.notFound", args[0]);
		}
		FunctionType functionType = FunctionType.getByName(args[4]);
		int minLength;
		int maxLength;
		int argumentCount = enumparticletypes.getArgumentCount();
		if (functionType == null) {
			throw new CommandException("commands.functionType.notFound", args[4]);
		}
		switch (functionType) {
		case NORMAL:
			minLength = 17;
			maxLength = 21;
			break;
		case FUNCTION:
			minLength = 17;
			maxLength = 22;
			break;
		case PARAMETER:
		case POLARPARAMETER:
			minLength = 16;
			maxLength = 21;
			break;
		case TICKPARAMETER:
		case TICKPOLARPARAMETER:
			minLength = 16;
			maxLength = 22;
			break;
		case PARAMETERRGB:
		case POLARPARAMETERRGB:
			minLength = 8;
			maxLength = 13;
			break;
		case TICKPARAMETERRGB:
		case TICKPOLARPARAMETERRGB:
			minLength = 8;
			maxLength = 14;
			break;
		case IMAGEFILEXY:
		case IMAGEFILEZY:
		case IMAGEFILEXZ:
			minLength = 6;
			maxLength = 18;
			break;
		case IMAGEFILE:
			minLength = 6;
			maxLength = 17;
			break;
		case MGFILE:
			minLength = 6;
			maxLength = 10;
			break;
		case POSFILE:
			minLength = 6;
			maxLength = 8;
			break;
		default:
			throw new CommandException("commands.functionType.notFound", args[4]);
		}
		if (args.length >= minLength && args.length <= maxLength || args.length == maxLength + argumentCount) {
			double x = parseDouble(vec3d.x, args[1], true);
			double y = parseDouble(vec3d.y, args[2], true);
			double z = parseDouble(vec3d.z, args[3], true);
			int[] arguments;
			if (args.length > maxLength) {
				arguments = new int[argumentCount];
				for (int i = 0; i < argumentCount; i++) {
					arguments[i] = parseInt(args[13 + i], 0);
				}
			} else {
				arguments = new int[0];
			}
			ParticleMessage message = null;
			switch (functionType) {
			case NORMAL: {
				float red = (float) parseDouble(args[5], 0, 1);
				float green = (float) parseDouble(args[6], 0, 1);
				float blue = (float) parseDouble(args[7], 0, 1);
				float alpha = (float) parseDouble(args[8], 0, 1);
				int brightness = parseInt(args[9], 0, 255);
				double vx = parseDouble(args[10]);
				double vy = parseDouble(args[11]);
				double vz = parseDouble(args[12]);
				double dx = parseDouble(args[13], 0);
				double dy = parseDouble(args[14], 0);
				double dz = parseDouble(args[15], 0);
				int count = parseInt(args[16], 0);
				int maxAge = 0;
				if (args.length > 17) {
					maxAge = parseInt(args[17], -1);
				}
				String speedExpression = null;
				if (args.length > 18) {
					speedExpression = args[18];
				}
				double speedStep = 0.1;
				if (args.length > 19) {
					speedStep = parseDouble(args[19], 0);
				}
				String group = null;
				if (args.length > 20) {
					group = args[20];
				}
				message = new ParticleMessage(enumparticletypes.getParticleID(), x, y, z, functionType, red, green,
						blue, alpha, brightness, vx, vy, vz, dx, dy, dz, count, maxAge, speedExpression, speedStep,
						group, arguments);
				break;
			}
			case FUNCTION: {
				float red = (float) parseDouble(args[5], 0, 1);
				float green = (float) parseDouble(args[6], 0, 1);
				float blue = (float) parseDouble(args[7], 0, 1);
				float alpha = (float) parseDouble(args[8], 0, 1);
				int brightness = parseInt(args[9], 0, 255);
				double vx = parseDouble(args[10]);
				double vy = parseDouble(args[11]);
				double vz = parseDouble(args[12]);
				double dx = parseDouble(args[13], 0);
				double dy = parseDouble(args[14], 0);
				double dz = parseDouble(args[15], 0);
				String expression = args[16];
				double step = 0.1;
				if (args.length > 17) {
					step = parseDouble(args[17], 0);
				}
				if (step <= 0) {
					throw new CommandException("commands.argValue.error", "step", Double.toString(step));
				}
				int maxAge = 0;
				if (args.length > 18) {
					maxAge = parseInt(args[18], -1);
				}
				String speedExpression = null;
				if (args.length > 19) {
					speedExpression = args[19];
				}
				double speedStep = 0.1;
				if (args.length > 20) {
					speedStep = parseDouble(args[20], 0);
				}
				String group = null;
				if (args.length > 21) {
					group = args[21];
				}
				message = new ParticleMessage(enumparticletypes.getParticleID(), x, y, z, functionType, red, green,
						blue, alpha, brightness, vx, vy, vz, dx, dy, dz, expression, step, maxAge, speedExpression,
						speedStep, group, arguments);
				break;
			}
			case PARAMETER:
			case POLARPARAMETER: {
				float red = (float) parseDouble(args[5], 0, 1);
				float green = (float) parseDouble(args[6], 0, 1);
				float blue = (float) parseDouble(args[7], 0, 1);
				float alpha = (float) parseDouble(args[8], 0, 1);
				int brightness = parseInt(args[9], 0, 255);
				double vx = parseDouble(args[10]);
				double vy = parseDouble(args[11]);
				double vz = parseDouble(args[12]);
				double tStart = parseDouble(args[13]);
				double tEnd = parseDouble(args[14]);
				String expression = args[15];
				double step = 0.1;
				if (args.length > 16) {
					step = parseDouble(args[16], 0);
				}
				if (step <= 0) {
					throw new CommandException("commands.argValue.error", "step", Double.toString(step));
				}
				int maxAge = 0;
				if (args.length > 17) {
					maxAge = parseInt(args[17], -1);
				}
				String speedExpression = null;
				if (args.length > 18) {
					speedExpression = args[18];
				}
				double speedStep = 0.1;
				if (args.length > 19) {
					speedStep = parseDouble(args[19], 0);
				}
				String group = null;
				if (args.length > 20) {
					group = args[20];
				}
				message = new ParticleMessage(enumparticletypes.getParticleID(), x, y, z, functionType, red, green,
						blue, alpha, brightness, vx, vy, vz, tStart, tEnd, expression, step, maxAge, speedExpression,
						speedStep, group, arguments);
				break;
			}
			case TICKPARAMETER:
			case TICKPOLARPARAMETER: {
				float red = (float) parseDouble(args[5], 0, 1);
				float green = (float) parseDouble(args[6], 0, 1);
				float blue = (float) parseDouble(args[7], 0, 1);
				float alpha = (float) parseDouble(args[8], 0, 1);
				int brightness = parseInt(args[9], 0, 255);
				double vx = parseDouble(args[10]);
				double vy = parseDouble(args[11]);
				double vz = parseDouble(args[12]);
				double tStart = parseDouble(args[13]);
				double tEnd = parseDouble(args[14]);
				String expression = args[15];
				double step = 0.1;
				if (args.length > 16) {
					step = parseDouble(args[16], 0);
				}
				if (step <= 0) {
					throw new CommandException("commands.argValue.error", "step", Double.toString(step));
				}
				int cpt = 10;
				if (args.length > 17) {
					cpt = parseInt(args[17], 1);
				}
				int maxAge = 0;
				if (args.length > 18) {
					maxAge = parseInt(args[18], -1);
				}
				String speedExpression = null;
				if (args.length > 19) {
					speedExpression = args[19];
				}
				double speedStep = 0.1;
				if (args.length > 20) {
					speedStep = parseDouble(args[20], 0);
				}
				String group = null;
				if (args.length > 21) {
					group = args[21];
				}
				message = new ParticleMessage(enumparticletypes.getParticleID(), x, y, z, functionType, red, green,
						blue, alpha, brightness, vx, vy, vz, tStart, tEnd, expression, step, cpt, maxAge,
						speedExpression, speedStep, group, arguments);
				break;
			}
			case PARAMETERRGB:
			case POLARPARAMETERRGB: {
				double tStart = parseDouble(args[5]);
				double tEnd = parseDouble(args[6]);
				String expression = args[7];
				double step = 0.1;
				if (args.length > 8) {
					step = parseDouble(args[8], 0);
				}
				if (step <= 0) {
					throw new CommandException("commands.argValue.error", "step", Double.toString(step));
				}
				int maxAge = 0;
				if (args.length > 9) {
					maxAge = parseInt(args[9], -1);
				}
				String speedExpression = null;
				if (args.length > 10) {
					speedExpression = args[10];
				}
				double speedStep = 0.1;
				if (args.length > 11) {
					speedStep = parseDouble(args[11], 0);
				}
				String group = null;
				if (args.length > 12) {
					group = args[12];
				}
				message = new ParticleMessage(enumparticletypes.getParticleID(), x, y, z, functionType, tStart, tEnd,
						expression, step, maxAge, speedExpression, speedStep, group, arguments);
				break;
			}
			case TICKPARAMETERRGB:
			case TICKPOLARPARAMETERRGB: {
				double tStart = parseDouble(args[5]);
				double tEnd = parseDouble(args[6]);
				String expression = args[7];
				double step = 0.1;
				if (args.length > 8) {
					step = parseDouble(args[8], 0);
				}
				if (step <= 0) {
					throw new CommandException("commands.argValue.error", "step", Double.toString(step));
				}
				int cpt = 10;
				if (args.length > 9) {
					cpt = parseInt(args[9], 1);
				}
				int maxAge = 0;
				if (args.length > 10) {
					maxAge = parseInt(args[10], -1);
				}
				String speedExpression = null;
				if (args.length > 11) {
					speedExpression = args[11];
				}
				double speedStep = 0.1;
				if (args.length > 12) {
					speedStep = parseDouble(args[12], 0);
				}
				String group = null;
				if (args.length > 13) {
					group = args[13];
				}
				message = new ParticleMessage(enumparticletypes.getParticleID(), x, y, z, functionType, tStart, tEnd,
						expression, step, cpt, maxAge, speedExpression, speedStep, group, arguments);
				break;
			}
			case IMAGEFILEXY:
			case IMAGEFILEZY:
			case IMAGEFILEXZ: {
				String imagePath = args[5];
				double proportion = 1;
				if (args.length > 6) {
					proportion = parseDouble(args[6], 0);
				}
				int rotate = 0;
				if (args.length > 7) {
					rotate = parseInt(args[7], 0, 3);
				}
				int overturn = 0;
				if (args.length > 8) {
					overturn = parseInt(args[8], 0, 2);
				}
				double step = 0.1;
				if (args.length > 9) {
					step = parseDouble(args[9], 0);
				}
				int brightness = 240;
				if (args.length > 10) {
					brightness = parseInt(args[10], 0, 255);
				}
				double vx = 0;
				if (args.length > 11) {
					vx = parseDouble(args[11]);
				}
				double vy = 0;
				if (args.length > 12) {
					vy = parseDouble(args[12]);
				}
				double vz = 0;
				if (args.length > 13) {
					vz = parseDouble(args[13]);
				}
				if (step <= 0) {
					throw new CommandException("commands.argValue.error", "step", Double.toString(step));
				}
				int maxAge = 0;
				if (args.length > 14) {
					maxAge = parseInt(args[14], -1);
				}
				String speedExpression = null;
				if (args.length > 15) {
					speedExpression = args[15];
				}
				double speedStep = 0.1;
				if (args.length > 16) {
					speedStep = parseDouble(args[16], 0);
				}
				String group = null;
				if (args.length > 17) {
					group = args[17];
				}
				message = new ParticleMessage(enumparticletypes.getParticleID(), x, y, z, functionType, imagePath,
						proportion, rotate, overturn, step, brightness, vx, vy, vz, maxAge, speedExpression, speedStep,
						group, arguments);
				break;
			}
			case IMAGEFILE: {
				String imagePath = args[5];
				double proportion = 1;
				if (args.length > 6) {
					proportion = parseDouble(args[6], 0);
				}
				String matrix = null;
				if (args.length > 7) {
					matrix = args[7];
				}
				double step = 0.1;
				if (args.length > 8) {
					step = parseDouble(args[8], 0);
				}
				int brightness = 240;
				if (args.length > 9) {
					brightness = parseInt(args[9], 0, 255);
				}
				double vx = 0;
				if (args.length > 10) {
					vx = parseDouble(args[10]);
				}
				double vy = 0;
				if (args.length > 11) {
					vy = parseDouble(args[11]);
				}
				double vz = 0;
				if (args.length > 12) {
					vz = parseDouble(args[12]);
				}
				if (step <= 0) {
					throw new CommandException("commands.argValue.error", "step", Double.toString(step));
				}
				int maxAge = 0;
				if (args.length > 13) {
					maxAge = parseInt(args[13], -1);
				}
				String speedExpression = null;
				if (args.length > 14) {
					speedExpression = args[14];
				}
				double speedStep = 0.1;
				if (args.length > 15) {
					speedStep = parseDouble(args[15], 0);
				}
				String group = null;
				if (args.length > 16) {
					group = args[16];
				}
				message = new ParticleMessage(enumparticletypes.getParticleID(), x, y, z, functionType, imagePath,
						proportion, matrix, step, brightness, vx, vy, vz, maxAge, speedExpression, speedStep, group,
						arguments);
				break;
			}
			case MGFILE: {
				String mgPath = args[5];
				String matrix = null;
				if (args.length > 6) {
					matrix = args[6];
				}
				double step = 0.1;
				if (args.length > 7) {
					step = parseDouble(args[7], 0);
				}
				int brightness = 240;
				if (args.length > 8) {
					brightness = parseInt(args[8], 0, 255);
				}
				int auto = 0;
				if (args.length > 9) {
					auto = parseInt(args[9], 0, 1);
				}
				message = new ParticleMessage(enumparticletypes.getParticleID(), x, y, z, functionType, mgPath, matrix,
						step, brightness, auto, arguments);
				break;
			}
			case POSFILE: {
				String posPath = args[5];
				String matrix = null;
				if (args.length > 6) {
					matrix = args[6];
				}
				String group = null;
				if (args.length > 7) {
					group = args[7];
				}
				message = new ParticleMessage(enumparticletypes.getParticleID(), x, y, z, functionType, posPath, matrix,
						group, arguments);
				break;
			}
			default:
				break;
			}
			return message;
		} else {
			throw new WrongUsageException("commands.particleex." + functionType.getName() + ".usage", new Object[0]);
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			@Nullable BlockPos targetPos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, getList());
		} else if (args.length > 1 && args.length <= 4) {
			return getTabCompletionCoordinate(args, 1, targetPos);
		} else if (args.length == 5) {
			switch (args[0]) {
			case "funlist":
			case "clearcache":
			case "clearparticle":
			case "group":
				return Collections.emptyList();
			default:
				return getListOfStringsMatchingLastWord(args, FunctionType.getNames());
			}
		} else if (args.length == 6) {
			if (args[0].equals("group")) {
				List<String> list = Lists.newArrayList();
				list.add("remove");
				list.add("change");
				return getListOfStringsMatchingLastWord(args, list);
			}
		} else if (args.length == 7) {
			if (args[0].equals("group") && args[5].equals("change")) {
				return getListOfStringsMatchingLastWord(args, ChangeType.getNames());
			}
		}
		return Collections.emptyList();
	}

	private List<String> getList() {
		Set<String> names = EnumParticleTypes.getParticleNames();
		List<String> list = Lists.newArrayList();
		list.addAll(names);
		list.add("funlist");
		list.add("clearcache");
		list.add("clearparticle");
		list.add("group");
		return list;
	}

	public boolean isUsernameIndex(String[] args, int index) {
		return index == 10;
	}

}
