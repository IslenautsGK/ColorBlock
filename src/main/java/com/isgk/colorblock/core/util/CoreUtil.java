package com.isgk.colorblock.core.util;

import java.util.ArrayDeque;

import com.isgk.colorblock.core.ColorBlockCore;
import com.isgk.colorblock.util.client.component.ClientMessageUtil;

import net.minecraft.client.particle.Particle;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;

public class CoreUtil {

	public static void move(Particle particle) {
		try {
			if (particle.exe != null) {
				if (particle.moveT == 0) {
					particle.exe.put("PI", Math.PI);
					particle.exe.put("E", Math.E);
					particle.exe.put("vx", particle.motionX);
					particle.exe.put("vy", particle.motionY);
					particle.exe.put("vz", particle.motionZ);
					particle.exe.put("cx", particle.centerX);
					particle.exe.put("cy", particle.centerY);
					particle.exe.put("cz", particle.centerZ);
					particle.exe.put("dx", particle.posX - particle.centerX);
					particle.exe.put("dy", particle.posY - particle.centerY);
					particle.exe.put("dz", particle.posZ - particle.centerZ);
					particle.exe.put("dr",
							Math.sqrt((particle.posX - particle.centerX) * (particle.posX - particle.centerX)
									+ (particle.posY - particle.centerY) * (particle.posY - particle.centerY)
									+ (particle.posZ - particle.centerZ) * (particle.posZ - particle.centerZ)));
					particle.exe.put("dθ1",
							Math.atan2(particle.posZ - particle.centerZ, particle.posX - particle.centerX));
					particle.exe.put("dθ2", Math.atan2(particle.posY - particle.centerY,
							Math.hypot(particle.posX - particle.centerX, particle.posZ - particle.centerZ)));
					particle.exe.put("red", (double) particle.getRedColorF());
					particle.exe.put("green", (double) particle.getGreenColorF());
					particle.exe.put("blue", (double) particle.getBlueColorF());
					particle.exe.put("alpha", (double) particle.particleAlpha);
					particle.exe.put("brightness", (double) particle.brightness);
					if (!particle.autoDestory) {
						particle.exe.put("destory", 0D);
					}
				}
				if (particle.pictures != null) {
					if (particle.pictureIndex >= particle.pictures.length) {
						particle.setExpired();
						return;
					}
					double alpha = ((particle.pictures[particle.pictureIndex] & 0xff000000) >>> 24) / 255D;
					double red = ((particle.pictures[particle.pictureIndex] & 0xff0000) >>> 16) / 255D;
					double green = ((particle.pictures[particle.pictureIndex] & 0xff00) >>> 8) / 255D;
					double blue = (particle.pictures[particle.pictureIndex] & 0xff) / 255D;
					particle.exe.put("red", red);
					particle.exe.put("green", green);
					particle.exe.put("blue", blue);
					particle.exe.put("alpha", alpha);
					particle.pictureIndex++;
				}
				particle.exe.put("x", particle.posX - particle.centerX);
				particle.exe.put("y", particle.posY - particle.centerY);
				particle.exe.put("z", particle.posZ - particle.centerZ);
				particle.exe.put("r",
						Math.sqrt((particle.posX - particle.centerX) * (particle.posX - particle.centerX)
								+ (particle.posY - particle.centerY) * (particle.posY - particle.centerY)
								+ (particle.posZ - particle.centerZ) * (particle.posZ - particle.centerZ)));
				particle.exe.put("θ1", Math.atan2(particle.posZ - particle.centerZ, particle.posX - particle.centerX));
				particle.exe.put("θ2", Math.atan2(particle.posY - particle.centerY,
						Math.hypot(particle.posX - particle.centerX, particle.posZ - particle.centerZ)));
				particle.exe.put("t", particle.moveT);
				particle.moveT += particle.step;
				particle.exe.invoke();
				if ((double) particle.exe.get("destory") != 0) {
					particle.setExpired();
					return;
				}
				double vx = (double) particle.exe.get("vx");
				double vy = (double) particle.exe.get("vy");
				double vz = (double) particle.exe.get("vz");
				if (particle.matrix != null) {
					double[] pos = particle.matrix.transform(vx, vy, vz);
					vx = pos[0];
					vy = pos[1];
					vz = pos[2];
				}
				if (ColorBlockCore.debug) {
					particle.move2(vx, vy, vz);
				} else {
					particle.a2(vx, vy, vz);
				}
				particle.setRBGColorF((float) (double) particle.exe.get("red"),
						(float) (double) particle.exe.get("green"), (float) (double) particle.exe.get("blue"));
				particle.particleAlpha = (float) (double) particle.exe.get("alpha");
				particle.brightness = (int) Math.round((double) particle.exe.get("brightness"));
			} else if (particle.pictures != null) {
				if (particle.pictureIndex >= particle.pictures.length) {
					particle.setExpired();
					return;
				}
				float alpha = ((particle.pictures[particle.pictureIndex] & 0xff000000) >>> 24) / 255F;
				float red = ((particle.pictures[particle.pictureIndex] & 0xff0000) >>> 16) / 255F;
				float green = ((particle.pictures[particle.pictureIndex] & 0xff00) >>> 8) / 255F;
				float blue = (particle.pictures[particle.pictureIndex] & 0xff) / 255F;
				particle.pictureIndex++;
				particle.setRBGColorF(red, green, blue);
				particle.particleAlpha = alpha;
				if (!particle.stop) {
					if (ColorBlockCore.debug) {
						particle.move2(particle.motionX, particle.motionY, particle.motionZ);
					} else {
						particle.a2(particle.motionX, particle.motionY, particle.motionZ);
					}
				}
			}
		} catch (RuntimeException e) {
			ClientMessageUtil.addChatMessage(e.getMessage());
		}
	}

	public static void tickParticleList(ArrayDeque<Particle> particles) {
		particles.parallelStream().forEach(CoreUtil::tickParticle);
		particles.removeIf(particle -> !particle.isAlive());
	}

	private static void tickParticle(Particle particle) {
		try {
			particle.onUpdate();
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking Particle");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
			final int i = particle.getFXLayer();
			crashreportcategory.addDetail("Particle", new ICrashReportDetail<String>() {
				public String call() throws Exception {
					return particle.toString();
				}
			});
			crashreportcategory.addDetail("Particle Type", new ICrashReportDetail<String>() {
				public String call() throws Exception {
					if (i == 0) {
						return "MISC_TEXTURE";
					} else if (i == 1) {
						return "TERRAIN_TEXTURE";
					} else {
						return i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i;
					}
				}
			});
			throw new ReportedException(crashreport);
		}
	}

}
