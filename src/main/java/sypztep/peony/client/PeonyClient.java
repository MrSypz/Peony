package sypztep.peony.client;

import net.fabricmc.api.ClientModInitializer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import sypztep.peony.Peony;

@Mod(value = Peony.MODID, dist = Dist.CLIENT)
public class PeonyClient implements ClientModInitializer {
    public PeonyClient(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @Override
    public void onInitializeClient() {
    }
}
