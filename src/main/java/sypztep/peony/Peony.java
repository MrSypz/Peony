package sypztep.peony;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;
import sypztep.peony.common.command.StatsDebugCommand;
import sypztep.peony.module.level.event.AttachOnJoinEvent;
import sypztep.peony.common.init.ModAttachments;
import sypztep.peony.common.init.ModPayload;
import sypztep.peony.common.reloadlistener.MobStatsReloadListenr;
import sypztep.peony.module.combat.event.CombatEventHandler;
import sypztep.peony.module.level.event.DamageXpEvent;
import sypztep.peony.module.level.event.DeathPenaltyEvent;

@Mod(Peony.MODID)
public class Peony {
    public static final String MODID = "peony";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    public Peony(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(ModPayload::initPayload);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, PeonyConfig.SPEC);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(CombatEventHandler.class);
        NeoForge.EVENT_BUS.addListener(AttachOnJoinEvent::onEntityJoinWorld);
        NeoForge.EVENT_BUS.addListener(DamageXpEvent::onLivingDamage);
        NeoForge.EVENT_BUS.addListener(DamageXpEvent::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(DeathPenaltyEvent::onPlayerDeath);
    }
    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new MobStatsReloadListenr());
    }
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        StatsDebugCommand.register(event.getDispatcher());
    }
}
