package sypztep.peony.module.level.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import sypztep.peony.Peony;
import sypztep.peony.common.attachment.DamageTrackingAttachment;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;
import sypztep.peony.common.init.ModAttachments;
import sypztep.peony.module.level.util.DamageXpCalculator;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;

public class DamageXpEvent {
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("0.0");

    // ================ Damage Tracking ================

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        DamageSource damageSource = event.getSource();

        if (target.level().isClientSide()) return;

        Player attacker = extractPlayerFromDamageSource(damageSource);
        if (attacker == null) return;

        if (attacker == target || target instanceof Player) return;

        // Get or initialize damage tracking
        DamageTrackingAttachment tracking = target.getData(ModAttachments.DAMAGE_TRACKING.get());
        float damageDealt = event.getNewDamage();
        tracking.addDamage(attacker.getUUID(), damageDealt);
    }

    // ================ Death and XP Distribution ================

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity deadEntity = event.getEntity();
        if (deadEntity.level().isClientSide()) return;

        if (deadEntity instanceof Player) return;

        if (!deadEntity.hasData(ModAttachments.DAMAGE_TRACKING.get())) return;

        DamageTrackingAttachment tracking = deadEntity.getData(ModAttachments.DAMAGE_TRACKING.get());
        if (!tracking.hasParticipants()) return;

        Map<UUID, Float> participants = tracking.getAllDamageDealt();
        distributeXpToParticipants(deadEntity, tracking, participants);

        tracking.reset();
    }

    // ================ Helper Methods ================

    private static Player extractPlayerFromDamageSource(DamageSource damageSource) {
        if (damageSource.getEntity() instanceof Player player) return player;

        if (damageSource.getDirectEntity() instanceof Player player) return player;

        if (damageSource.getDirectEntity() instanceof OwnableEntity ownable)
            if (ownable.getOwner() instanceof Player player) return player;

        return null;
    }

    /**
     * Distributes XP to all participants based on their damage contribution.
     */
    private static void distributeXpToParticipants(LivingEntity deadEntity, DamageTrackingAttachment tracking,
                                                   Map<UUID, Float> participants) {
        for (Map.Entry<UUID, Float> entry : participants.entrySet()) {
            UUID playerId = entry.getKey();

            Player player = deadEntity.level().getPlayerByUUID(playerId);
            if (!(player instanceof ServerPlayer serverPlayer)) {
                Peony.LOGGER.warn("Could not find player {} for XP distribution", playerId);
                continue;
            }

            float damagePercentage = tracking.getDamagePercentage(playerId);
            int xpReward = DamageXpCalculator.calculateXpReward(player, deadEntity, damagePercentage);

            if (xpReward > 0) giveXpToPlayer(serverPlayer, xpReward, deadEntity, damagePercentage);
        }
    }

    private static void giveXpToPlayer(ServerPlayer player, int xpAmount, LivingEntity killedEntity, float damagePercentage) {
        LivingEntityStatsAttachment stats = player.getData(ModAttachments.LIVINGSTATS.get());

        int oldLevel = stats.getLevel();
        
        // Use the new flattened API that handles sync automatically
        boolean xpGained = stats.addExperience(player, xpAmount);

        if (!xpGained) {
            if (stats.getLevelSystem().isAtMaxLevel()) sendMaxLevelMessage(player, killedEntity);
            return;
        }

        boolean leveledUp = stats.getLevel() > oldLevel;
        sendXpGainMessage(player, xpAmount, killedEntity, damagePercentage, leveledUp);
    }

    private static void sendXpGainMessage(ServerPlayer player, int xpAmount, LivingEntity killedEntity,
                                          float damagePercentage, boolean leveledUp) {
        String entityName = killedEntity.getDisplayName().getString();
        String formattedXp = LivingEntityStatsAttachment.formatNumber(xpAmount);
        String percentageStr = PERCENTAGE_FORMAT.format(damagePercentage * 100);

        // Color based on damage contribution
        String damageColor;
        if (damagePercentage >= 0.8f) {
            damageColor = "§a"; // Green for high contribution
        } else if (damagePercentage >= 0.3f) {
            damageColor = "§e"; // Yellow for medium contribution
        } else {
            damageColor = "§6"; // Orange for low contribution
        }

        Component xpMessage = Component.literal(String.format("§a+%s XP §7from §f%s §7(%s%s%%§7)",
                formattedXp, entityName, damageColor, percentageStr));

        player.sendSystemMessage(xpMessage);

        // Level up message
        if (leveledUp) {
            LivingEntityStatsAttachment stats = player.getData(ModAttachments.LIVINGSTATS.get());
            int currentLevel = stats.getLevel();
            int statPointsGained = stats.getLevelSystem().getGainStatPointsForLevel(currentLevel);

            Component levelMessage = Component.literal(String.format("§6§lLEVEL UP! §r§aLevel %d §7(+%d stat points)",
                    currentLevel, statPointsGained));
            player.sendSystemMessage(levelMessage);
//TODO: Replace with new sound? 2/7/2025
            player.playNotifySound(net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                    net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.5f);
        }
    }

    private static void sendMaxLevelMessage(ServerPlayer player, LivingEntity killedEntity) {
        Component message = Component.literal(String.format(
                "§6§lMAX LEVEL §r§7Killed %s §8(No XP gained)",
                killedEntity.getDisplayName().getString()
        ));
        player.sendSystemMessage(message);
    }
}