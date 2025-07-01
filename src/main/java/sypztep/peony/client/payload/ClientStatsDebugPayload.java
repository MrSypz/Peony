package sypztep.peony.client.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sypztep.peony.Peony;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;
import sypztep.peony.common.init.ModAttachments;

public record ClientStatsDebugPayload(
        int entityId,
        int serverLevel,
        int serverXp,
        int serverXpToNext,
        int serverStatPoints,
        boolean serverMaxLevel
) implements CustomPacketPayload {

    public static final Type<ClientStatsDebugPayload> TYPE = new Type<>(Peony.id("client_stats_debug"));

    public static final StreamCodec<ByteBuf, ClientStatsDebugPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientStatsDebugPayload::entityId,
            ByteBufCodecs.VAR_INT, ClientStatsDebugPayload::serverLevel,
            ByteBufCodecs.VAR_INT, ClientStatsDebugPayload::serverXp,
            ByteBufCodecs.VAR_INT, ClientStatsDebugPayload::serverXpToNext,
            ByteBufCodecs.VAR_INT, ClientStatsDebugPayload::serverStatPoints,
            ByteBufCodecs.BOOL, ClientStatsDebugPayload::serverMaxLevel,
            ClientStatsDebugPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static void handleClientStatsDebug(ClientStatsDebugPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.getId() != payload.entityId()) return;

            // Get client-side stats
            LivingEntityStatsAttachment clientStats = player.getData(ModAttachments.LIVINGSTATS.get());

            // Send comparison table
            sendComparisonTable(player, payload, clientStats);
        });
    }

    private static void sendComparisonTable(LocalPlayer player, ClientStatsDebugPayload serverData,
                                            LivingEntityStatsAttachment clientStats) {

        player.sendSystemMessage(Component.literal("§6════════════ Stats Debug Table ════════════"));
        player.sendSystemMessage(Component.literal("§a| Field       | Server Value | Client Value | Synced? |"));
        player.sendSystemMessage(Component.literal("§7|-------------|--------------|--------------|---------|"));

        // Level comparison
        boolean levelSynced = serverData.serverLevel() == clientStats.getLevel();
        player.sendSystemMessage(Component.literal(String.format("§f| Level       | %-12d | %-12d | %s |",
                serverData.serverLevel(),
                clientStats.getLevel(),
                levelSynced ? "§a✓" : "§c✗")));

        // XP comparison
        boolean xpSynced = serverData.serverXp() == clientStats.getXp();
        player.sendSystemMessage(Component.literal(String.format("§f| XP          | %-12s | %-12s | %s |",
                LivingEntityStatsAttachment.formatNumber(serverData.serverXp()),
                LivingEntityStatsAttachment.formatNumber(clientStats.getXp()),
                xpSynced ? "§a✓" : "§c✗")));

        // XP To Next comparison
        boolean xpToNextSynced = serverData.serverXpToNext() == clientStats.getXpToNextLevel();
        player.sendSystemMessage(Component.literal(String.format("§f| XP To Next  | %-12s | %-12s | %s |",
                LivingEntityStatsAttachment.formatNumber(serverData.serverXpToNext()),
                LivingEntityStatsAttachment.formatNumber(clientStats.getXpToNextLevel()),
                xpToNextSynced ? "§a✓" : "§c✗")));

        // Stat Points comparison
        boolean statPointsSynced = serverData.serverStatPoints() == clientStats.getStatPoints();
        player.sendSystemMessage(Component.literal(String.format("§f| Stat Points | %-12d | %-12d | %s |",
                serverData.serverStatPoints(),
                clientStats.getStatPoints(),
                statPointsSynced ? "§a✓" : "§c✗")));

        // Max Level comparison
        boolean maxLevelSynced = serverData.serverMaxLevel() == clientStats.getLevelSystem().isAtMaxLevel();
        player.sendSystemMessage(Component.literal(String.format("§f| Max Level?  | %-12s | %-12s | %s |",
                serverData.serverMaxLevel() ? "Yes" : "No",
                clientStats.getLevelSystem().isAtMaxLevel() ? "Yes" : "No",
                maxLevelSynced ? "§a✓" : "§c✗")));

        player.sendSystemMessage(Component.literal("§6═══════════════════════════════════════════"));

        // Summary
        boolean allSynced = levelSynced && xpSynced && xpToNextSynced && statPointsSynced && maxLevelSynced;
        if (allSynced) {
            player.sendSystemMessage(Component.literal("§a✓ All values are synchronized!"));
        } else {
            player.sendSystemMessage(Component.literal("§c⚠ Some values are out of sync!"));
        }
    }
}