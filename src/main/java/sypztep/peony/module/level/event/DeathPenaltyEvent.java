package sypztep.peony.module.level.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import sypztep.peony.PeonyConfig;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;
import sypztep.peony.common.init.ModAttachments;

public class DeathPenaltyEvent {
    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!PeonyConfig.ENABLE_DEATH_PENALTY.get()) return; // Cofig false return

        LivingEntityStatsAttachment stats = player.getData(ModAttachments.LIVINGSTATS.get());

        if (stats.getLevelSystem().isAtMaxLevel()) {
            sendNoPenaltyMessage(player, "Max level reached");
            return;
        }

        if (stats.getLevel() < PeonyConfig.DEATH_PENALTY_MIN_LEVEL.get()) return;

        // Calculate and apply XP penalty
        int currentXp = stats.getXp();
        double penaltyPercentage = PeonyConfig.DEATH_XP_PENALTY_PERCENTAGE.get();
        int xpLost = (int) Math.floor(currentXp * penaltyPercentage);
        xpLost = Math.min(xpLost, currentXp);

        if (xpLost > 0) {
            // Use the new flattened API with automatic sync
            stats.setXp(player, stats.getXp() - xpLost);

            sendDeathPenaltyMessage(player, xpLost, penaltyPercentage);
        } else sendNoPenaltyMessage(player, "No XP to lose");
    }

    private static void sendDeathPenaltyMessage(ServerPlayer player, int lostXP,double ptc) {
        double penaltyPercentage = ptc * 100;

        Component message = Component.literal("You have lost: ")
                .withStyle(style -> style.withColor(0xFFD700)) // Gold color
                .append(Component.literal(LivingEntityStatsAttachment.formatNumber(lostXP) + " XP")
                        .withStyle(style -> style.withColor(0xFF5555))); // Red color

        Component detailMessage = Component.literal(String.format("(%.1f%% of current level XP)", penaltyPercentage))
                .withStyle(style -> style.withColor(0x888888)); // Gray color

        player.sendSystemMessage(message);
        player.sendSystemMessage(detailMessage);

        player.level().playSound(null, player.blockPosition(),
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,
                0.5f, 0.5f);
    }

    private static void sendNoPenaltyMessage(ServerPlayer player, String reason) {
        Component message = Component.literal("No XP lost on death")
                .withStyle(style -> style.withColor(0x55FF55)); // Green color

        Component reasonMessage = Component.literal("Reason: " + reason)
                .withStyle(style -> style.withColor(0x888888)); // Gray color

        player.sendSystemMessage(message);
        player.sendSystemMessage(reasonMessage);
    }
}