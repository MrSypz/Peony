package sypztep.peony.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import sypztep.peony.Peony;
import sypztep.peony.client.hud.XpHudOverlay;
import sypztep.peony.client.screen.StatsUpdateScreen;
import sypztep.peony.client.util.ModKeybindings;

@Mod(value = Peony.MODID, dist = Dist.CLIENT)
public class PeonyClient  {
    public PeonyClient(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.register(this);
        
        // Register client tick event for keybinding handling
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    @SubscribeEvent
    public void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Peony.id("xp_hud"), new XpHudOverlay());
    }
    
    @SubscribeEvent
    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        for (var keyMapping : ModKeybindings.getAllKeybindings()) {
            event.register(keyMapping);
        }
    }
    
    public void onClientTick(ClientTickEvent.Post event) {
        // Handle stats screen opening
        if (ModKeybindings.OPEN_STATS_SCREEN.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen == null) {
                minecraft.setScreen(new StatsUpdateScreen());
            }
        }
    }

}
