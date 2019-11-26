package com.isgk.colorblock.core.container;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.isgk.colorblock.core.ColorBlockCore;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ColorBlockContainer extends DummyModContainer {

	private Logger log;

	public ColorBlockContainer() {
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "color_block_core";
		meta.name = "Color Block Core";
		meta.version = "0.0.8";
		meta.authorList = Arrays.asList("Is_GK");
		meta.description = "Color Block Core";
		meta.url = "www.isgk.com";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		log = event.getModLog();
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		log.info("debug is " + ColorBlockCore.debug);
		log.info("Set Max Particle Count To " + ColorBlockCore.maxParticleCount);
		log.info("Color Block Core Fix Loaded");
	}

}
