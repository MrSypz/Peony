package sypztep.peony.module.level.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;
import sypztep.peony.common.data.BaseMobStatsEntry;
import sypztep.peony.common.init.ModAttachments;

public class DamageXpCalculator {
    public static int calculateXpReward(Player player, LivingEntity killedEntity, float damagePercentage) {
        BaseMobStatsEntry mobStats = BaseMobStatsEntry.getStatsFor(killedEntity.getType());
        int baseXp = mobStats.exp();

        LivingEntityStatsAttachment playerStats = player.getData(ModAttachments.LIVINGSTATS.get());
        int playerLevel = playerStats.getLevel();
        int mobLevel = mobStats.lvl();

        float levelMod = calculateLevelModifier(playerLevel, mobLevel);

        float damageMod = Math.max(0.05f, damagePercentage); // Minimum 5% XP even for minimal damage

        int finalXp = Math.round(baseXp * levelMod * damageMod);

        return Math.max(1, finalXp); // Always give at least 1 XP
    }

    public static float calculateLevelModifier(int playerLevel, int mobLevel) {
        int levelDiff = mobLevel - playerLevel;

        if (levelDiff >= 16) return 0.40f;
        if (levelDiff == 15) return 1.15f;
        if (levelDiff == 14) return 1.20f;
        if (levelDiff == 13) return 1.25f;
        if (levelDiff == 12) return 1.30f;
        if (levelDiff == 11) return 1.35f;
        if (levelDiff == 10) return 1.40f;
        if (levelDiff == 9) return 1.35f;
        if (levelDiff == 8) return 1.30f;
        if (levelDiff == 7) return 1.25f;
        if (levelDiff == 6) return 1.20f;
        if (levelDiff == 5) return 1.15f;
        if (levelDiff == 4) return 1.10f;
        if (levelDiff == 3) return 1.05f;
        if (levelDiff == 2) return 1.00f;
        if (levelDiff == 1) return 1.00f;
        if (levelDiff == 0) return 1.00f;
        if (levelDiff == -1) return 1.00f;
        if (levelDiff == -2) return 1.00f;
        if (levelDiff == -3) return 1.00f;
        if (levelDiff == -4) return 1.00f;
        if (levelDiff == -5) return 1.00f;
        if (levelDiff == -6) return 0.95f;
        if (levelDiff == -7) return 0.95f;
        if (levelDiff == -8) return 0.95f;
        if (levelDiff == -9) return 0.95f;
        if (levelDiff == -10) return 0.95f;
        if (levelDiff == -11) return 0.90f;
        if (levelDiff == -12) return 0.90f;
        if (levelDiff == -13) return 0.90f;
        if (levelDiff == -14) return 0.90f;
        if (levelDiff == -15) return 0.90f;
        if (levelDiff == -16) return 0.85f;
        if (levelDiff == -17) return 0.85f;
        if (levelDiff == -18) return 0.85f;
        if (levelDiff == -19) return 0.85f;
        if (levelDiff == -20) return 0.85f;
        if (levelDiff == -21) return 0.60f;
        if (levelDiff == -22) return 0.60f;
        if (levelDiff == -23) return 0.60f;
        if (levelDiff == -24) return 0.60f;
        if (levelDiff == -25) return 0.60f;
        if (levelDiff == -26) return 0.35f;
        if (levelDiff == -27) return 0.35f;
        if (levelDiff == -28) return 0.35f;
        if (levelDiff == -29) return 0.35f;
        if (levelDiff == -30) return 0.35f;

        return 0.10f;
    }
}
