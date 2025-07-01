package sypztep.peony.common.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import sypztep.peony.client.payload.SyncStatsPayloadS2C;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;

/**
 * Centralized stats synchronization management.
 * Handles all network synchronization for player stats.
 */
public class StatsSync {
    
    /**
     * Synchronizes all stats to tracking players and the entity itself if it's a player.
     */
    public static void syncAll(LivingEntity entity, LivingEntityStatsAttachment stats) {
        if (entity.level().isClientSide()) return;

        SyncStatsPayloadS2C packet = new SyncStatsPayloadS2C.Builder(entity.getId())
                .all(stats.getLevel(), stats.getXp(), stats.getXpToNextLevel(), stats.getStatPoints())
                .build();

        sendToRelevantPlayers(entity, packet);
    }
    
    /**
     * Synchronizes only experience and experience to next level.
     */
    public static void syncXp(LivingEntity entity, LivingEntityStatsAttachment stats) {
        if (entity.level().isClientSide()) return;

        SyncStatsPayloadS2C packet = new SyncStatsPayloadS2C.Builder(entity.getId())
                .xp(stats.getXp())
                .xpToNext(stats.getXpToNextLevel())
                .build();

        sendToRelevantPlayers(entity, packet);
    }
    
    /**
     * Synchronizes only level.
     */
    public static void syncLevel(LivingEntity entity, LivingEntityStatsAttachment stats) {
        if (entity.level().isClientSide()) return;

        SyncStatsPayloadS2C packet = new SyncStatsPayloadS2C.Builder(entity.getId())
                .level(stats.getLevel())
                .build();

        sendToRelevantPlayers(entity, packet);
    }
    
    /**
     * Synchronizes only stat points.
     */
    public static void syncStatPoints(LivingEntity entity, LivingEntityStatsAttachment stats) {
        if (entity.level().isClientSide()) return;

        SyncStatsPayloadS2C packet = new SyncStatsPayloadS2C.Builder(entity.getId())
                .statPoints(stats.getStatPoints())
                .build();

        sendToRelevantPlayers(entity, packet);
    }
    
    /**
     * Smart synchronization - only syncs values that have changed.
     */
    public static void smartSync(LivingEntity entity, LivingEntityStatsAttachment stats, 
                                int oldLevel, int oldXp, int oldStatPoints) {
        if (entity.level().isClientSide()) return;

        SyncStatsPayloadS2C.Builder builder = new SyncStatsPayloadS2C.Builder(entity.getId());

        if (stats.getLevel() != oldLevel)
            builder.level(stats.getLevel());

        if (stats.getXp() != oldXp)
            builder.xp(stats.getXp()).xpToNext(stats.getXpToNextLevel());

        if (stats.getStatPoints() != oldStatPoints)
            builder.statPoints(stats.getStatPoints());

        SyncStatsPayloadS2C packet = builder.build();
        if (packet.syncMask() != 0) { // Only send if something changed
            sendToRelevantPlayers(entity, packet);
        }
    }
    
    /**
     * Synchronizes stats to a specific player only.
     */
    public static void syncToPlayer(ServerPlayer player, LivingEntityStatsAttachment stats) {
        if (player.level().isClientSide()) return;

        SyncStatsPayloadS2C packet = new SyncStatsPayloadS2C.Builder(player.getId())
                .all(stats.getLevel(), stats.getXp(), stats.getXpToNextLevel(), stats.getStatPoints())
                .build();

        PacketDistributor.sendToPlayer(player, packet);
    }
    
    /**
     * Sends packet to both the entity (if it's a player) and tracking players.
     */
    private static void sendToRelevantPlayers(LivingEntity entity, SyncStatsPayloadS2C packet) {
        if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, packet);
        }
        PacketDistributor.sendToPlayersTrackingEntity(entity, packet);
    }
}