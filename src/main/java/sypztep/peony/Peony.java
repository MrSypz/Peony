package sypztep.peony;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import sypztep.peony.common.init.ModPayload;
import sypztep.peony.module.combat.event.CombatEventHandler;

@Mod(Peony.MODID)
public class Peony {
    public static final String MODID = "peony";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ResourceLocation id (String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    public Peony(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(ModPayload::initPayload);

        modContainer.registerConfig(ModConfig.Type.COMMON, PeonyConfig.SPEC); // Register Config Screen

        NeoForge.EVENT_BUS.register(CombatEventHandler.class);

    }
}
