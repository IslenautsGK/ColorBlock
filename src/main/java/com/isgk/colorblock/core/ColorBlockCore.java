package com.isgk.colorblock.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class ColorBlockCore implements IFMLLoadingPlugin {

	private Map<String, String> config;
	public static int maxParticleCount = 65536;
	public static boolean parallelParticleUpdate = false;
	public static boolean jniParticleUpdate = false;
	public static boolean debug;

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "com.isgk.colorblock.core.transformer.ColorBlockTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return "com.isgk.colorblock.core.container.ColorBlockContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		debug = !(Boolean) data.get("runtimeDeobfuscationEnabled");
		try {
			File configFile = new File((File) data.get("mcLocation"), "config/color_block_core.cfg");
			ZipFile modFile = debug ? null : new ZipFile((File) data.get("coremodLocation"));
			loadConfig(configFile, modFile);
			if (modFile != null) {
				modFile.close();
			}
			if (config.containsKey("MaxParticleCount")) {
				try {
					maxParticleCount = Integer.parseInt(config.get("MaxParticleCount"));
				} catch (NumberFormatException e) {
					maxParticleCount = 65536;
				}
			} else {
				maxParticleCount = 65536;
			}
			if (config.containsKey("ParallelParticleUpdate")) {
				parallelParticleUpdate = Boolean.parseBoolean(config.get("ParallelParticleUpdate"));
			} else {
				parallelParticleUpdate = false;
			}
			if (config.containsKey("JNIParticleUpdate")) {
				jniParticleUpdate = Boolean.parseBoolean(config.get("JNIParticleUpdate"));
			} else {
				jniParticleUpdate = false;
			}
			if (jniParticleUpdate) {
				File dllFile = new File(((File) data.get("coremodLocation")).getParentFile(),
						"ColorBlock/ColorBlockJNI.dll");
				if (!dllFile.exists()) {
					dllFile.getParentFile().mkdirs();
					try (InputStream is = getClass().getResourceAsStream("/ColorBlockJNI.dll");
							FileOutputStream fos = new FileOutputStream(dllFile);) {
						byte[] buf = new byte[8192];
						int len;
						while ((len = is.read(buf)) != -1) {
							fos.write(buf, 0, len);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.load(dllFile.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	private void loadConfig(File configFile, ZipFile modFile) throws IOException {
		if (!configFile.exists()) {
			ZipEntry ze = modFile.getEntry("assets/color_block/config/color_block_core.cfg");
			if (ze != null) {
				InputStream is = modFile.getInputStream(ze);
				OutputStream os = new FileOutputStream(configFile);
				byte[] buf = new byte[16];
				int len = 0;
				while ((len = is.read(buf)) > 0) {
					os.write(buf, 0, len);
				}
				is.close();
				os.close();
			}
		}
		config = new HashMap<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
		String line = null;
		while ((line = br.readLine()) != null) {
			int index = line.indexOf('#');
			if (index >= 0) {
				line = line.substring(0, index);
			}
			if (line.length() == 0) {
				continue;
			}
			String[] keyValue = line.split(":");
			if (keyValue.length == 2) {
				config.put(keyValue[0], keyValue[1]);
			}
		}
		br.close();
	}

}
