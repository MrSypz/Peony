package sypztep.peony.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModKeybindings {
    public static final String CATEGORY = "key.categories.peony";
    
    public static final KeyMapping OPEN_STATS_SCREEN = new KeyMapping(
            "key.peony.open_stats",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,  // Default to 'K' key
            CATEGORY
    );
    
    public static KeyMapping[] getAllKeybindings() {
        return new KeyMapping[] {
            OPEN_STATS_SCREEN
        };
    }
}