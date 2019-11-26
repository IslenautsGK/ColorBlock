package com.isgk.colorblock.client;

import org.apache.logging.log4j.Logger;

import com.isgk.colorblock.client.key.KeyEvent;
import com.isgk.colorblock.client.render.block.BlockColor;
import com.isgk.colorblock.common.CommonProxy;
import com.isgk.colorblock.common.block.BlockInitializer;
import com.isgk.colorblock.network.packet.ParticleMessage;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	public static Logger log;

	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		log = event.getModLog();
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
		registerBlockColorHandler();
		KeyEvent.init();
	}

	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	private void registerBlockColorHandler() {
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockColor(),
				BlockInitializer.colorBlock);
	}

}
