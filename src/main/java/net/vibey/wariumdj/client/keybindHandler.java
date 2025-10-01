package net.vibey.wariumdj.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "wariumdj", bus = Mod.EventBusSubscriber.Bus.MOD)
public class keybindHandler {

    private static KeyMapping OPEN_GUI;

    // Register the keybind (shows up in Controls menu)
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        OPEN_GUI = new KeyMapping(
                "key.wariumdj.open_gui", // translation key
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,            // default key is F8
                "key.categories.misc"        // category in Controls
        );
        event.register(OPEN_GUI);
    }

    // Handle input every frame (client event bus, not MOD bus!)
    @Mod.EventBusSubscriber(modid = "wariumdj")
    public static class ClientEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (OPEN_GUI != null && OPEN_GUI.consumeClick()) {
                Minecraft.getInstance().setScreen(new wariumdjGui());
            }
        }
    }
}
