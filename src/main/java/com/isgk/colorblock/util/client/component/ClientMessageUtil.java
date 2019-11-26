package com.isgk.colorblock.util.client.component;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientMessageUtil {

	private static Minecraft mc = Minecraft.getMinecraft();

	public static void addChatMessage(String message) {
		mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
	}

}
