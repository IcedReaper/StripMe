package com.IcedReaper.StripMe;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class StripMeClient implements ClientModInitializer {
	private static KeyBinding stripBinding;
	private static KeyBinding dressBinding;

	@Override
	public void onInitializeClient() {
		System.out.println("Client init");

		stripBinding = new KeyBinding("com.IcedReaper.StripMe.key.strip", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DELETE, "com.IcedReaper.StripMe.category");
		dressBinding = new KeyBinding("com.IcedReaper.StripMe.key.dress", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_INSERT, "com.IcedReaper.StripMe.category");

		KeyBindingHelper.registerKeyBinding(stripBinding);
		KeyBindingHelper.registerKeyBinding(dressBinding);

		ClientTickCallback.EVENT.register(client -> {
			if(stripBinding.wasPressed()) {
				PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
				passedData.writeBlockPos(client.player.getBlockPos());
				ClientSidePacketRegistry.INSTANCE.sendToServer(StripMe.STRIP_ID, passedData);
			}

			if(dressBinding.wasPressed()) {
				PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
				passedData.writeBlockPos(client.player.getBlockPos());
				ClientSidePacketRegistry.INSTANCE.sendToServer(StripMe.DRESS_ID, passedData);
			}
		});
	}
}
