package com.isgk.colorblock;

import com.isgk.colorblock.common.CommonProxy;
import com.isgk.colorblock.common.block.BlockInitializer;
import com.isgk.colorblock.network.ParticleNetworkHandler;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ColorBlockMain.MODID, name = "Color Block", version = "0.0.8", useMetadata = true)
public enum ColorBlockMain {

	INSTANCE;

	public static final String MODID = "color_block";

	@SidedProxy(serverSide = "com.isgk.colorblock.common.CommonProxy", clientSide = "com.isgk.colorblock.client.ClientProxy")
	public static CommonProxy proxy;

	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("color_block") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(BlockInitializer.colorBlock);
		}

	};

	@Mod.InstanceFactory
	public static ColorBlockMain getInstance() {
		return INSTANCE;
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
		ParticleNetworkHandler.INSTANCE.name();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		proxy.onServerStarting(event);
	}

}
