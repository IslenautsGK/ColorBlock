package com.isgk.colorblock.client.key;

import com.isgk.colorblock.network.packet.ParticleMessage;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

@EventBusSubscriber(modid = "color_block")
public final class KeyEvent {

	public static final KeyBinding[] KEYS = {
			new KeyBinding("key.color_block.mgkey_1", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_Z,
					"key.category.color_block"),
			new KeyBinding("key.color_block.mgkey_2", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_X,
					"key.category.color_block"),
			new KeyBinding("key.color_block.mgkey_3", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_C,
					"key.category.color_block"),
			new KeyBinding("key.color_block.mgkey_4", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_V,
					"key.category.color_block"),
			new KeyBinding("key.color_block.mgkey_5", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_B,
					"key.category.color_block"),
			new KeyBinding("key.color_block.mgkey_6", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_N,
					"key.category.color_block"),
			new KeyBinding("key.color_block.mgkey_7", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_M,
					"key.category.color_block"),
			new KeyBinding("key.color_block.mgkey_8", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_COMMA,
					"key.category.color_block") };
	public static boolean[] keyDowning = new boolean[8];

	public static void init() {
		for (KeyBinding key : KEYS) {
			ClientRegistry.registerKeyBinding(key);
		}
	}

	@SubscribeEvent
	public static void onKeyPressed(KeyInputEvent event) {
		for (int i = 0; i < KEYS.length; i++) {
			if (KEYS[i].isPressed()) {
				keyDowning[i] = true;
				ParticleMessage.MessageHandler.keyDown(i);
			} else if (!KEYS[i].isKeyDown() && keyDowning[i]) {
				keyDowning[i] = false;
				ParticleMessage.MessageHandler.keyUp(i);
			}
		}
	}

}
