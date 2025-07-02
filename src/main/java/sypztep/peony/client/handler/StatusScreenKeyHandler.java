package sypztep.peony.client.handler;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import sypztep.peony.Peony;
import sypztep.peony.PeonyConfig;
import sypztep.peony.client.gui.StatusScreen;

/**
 * Handles key bindings and input events for the status screen.
 */
@EventBusSubscriber(modid = Peony.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StatusScreenKeyHandler {
    
    public static final KeyMapping STATUS_SCREEN_KEY = new KeyMapping(
        "key.peony.status_screen",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_C, // Default to 'C' key
        "key.categories.peony"
    );
    
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(STATUS_SCREEN_KEY);
    }
    
    /**
     * Handle key input events on the Forge event bus
     */
    @EventBusSubscriber(modid = Peony.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class KeyInputHandler {
        
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.getInstance();
            
            // Only handle key press events when in-game
            if (minecraft.player == null || minecraft.screen != null) {
                return;
            }
            
            // Check if the status screen key was pressed
            if (STATUS_SCREEN_KEY.consumeClick()) {
                minecraft.setScreen(new StatusScreen());
            }
        }
    }
}