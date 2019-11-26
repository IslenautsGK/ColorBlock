package com.isgk.colorblock.common.block;

import com.isgk.colorblock.ColorBlockMain;
import com.isgk.colorblock.common.tile.TileColor;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = "color_block")
public final class BlockInitializer {

	public static Block colorBlock;

	@SubscribeEvent
	public static void registerBlock(RegistryEvent.Register<Block> event) {
		event.getRegistry()
				.register(colorBlock = new ColorBlock().setRegistryName(ColorBlockMain.MODID, "color_block"));
		GameRegistry.registerTileEntity(TileColor.class, new ResourceLocation(ColorBlockMain.MODID, "tile_color"));
	}

	@SubscribeEvent
	public static void registerItem(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(colorBlock).setRegistryName(ColorBlockMain.MODID, "color_block"));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModel(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(colorBlock), 0,
				new ModelResourceLocation(new ResourceLocation(ColorBlockMain.MODID, "color_block"), "inventory"));
	}

}
