package sypztep.peony.client.util;

import sypztep.peony.module.level.LivingStats;
import sypztep.peony.module.level.StatTypes;

/**
 * Utility class for calculating derived stats from primary stats.
 * Based on Ragnarok Online formulas (with adaptations for Minecraft).
 */
public class DerivedStatsCalculator {

    /**
     * Calculate Attack (ATK) - base damage
     * Formula: STR + weapon base damage (simulated as 1 for display)
     */
    public static int calculateBaseATK(LivingStats stats) {
        return stats.getStat(StatTypes.STRENGTH).getValue();
    }

    /**
     * Calculate Attack bonus from STR
     * Formula: STR / 5 (simplified)
     */
    public static int calculateBonusATK(LivingStats stats) {
        return stats.getStat(StatTypes.STRENGTH).getValue() / 5;
    }

    /**
     * Calculate Magic Attack (MATK) - base magic damage
     * Formula: INT + weapon base magic damage (simulated as 1 for display)
     */
    public static int calculateBaseMATK(LivingStats stats) {
        return stats.getStat(StatTypes.INTELLIGENCE).getValue();
    }

    /**
     * Calculate Magic Attack bonus from INT
     * Formula: INT / 7 (simplified)
     */
    public static int calculateBonusMATK(LivingStats stats) {
        return stats.getStat(StatTypes.INTELLIGENCE).getValue() / 7;
    }

    /**
     * Calculate Defense (DEF) - physical defense
     * Formula: VIT + equipment defense (simulated as 1 for display)
     */
    public static int calculateBaseDEF(LivingStats stats) {
        return stats.getStat(StatTypes.VITALITY).getValue();
    }

    /**
     * Calculate Defense bonus from VIT
     * Formula: VIT / 4 (simplified)
     */
    public static int calculateBonusDEF(LivingStats stats) {
        return stats.getStat(StatTypes.VITALITY).getValue() / 4;
    }

    /**
     * Calculate Magic Defense (MDEF) - magic defense
     * Formula: INT + equipment magic defense (simulated as 1 for display)
     */
    public static int calculateBaseMDEF(LivingStats stats) {
        return stats.getStat(StatTypes.INTELLIGENCE).getValue();
    }

    /**
     * Calculate Magic Defense bonus from INT
     * Formula: INT / 5 (simplified)
     */
    public static int calculateBonusMDEF(LivingStats stats) {
        return stats.getStat(StatTypes.INTELLIGENCE).getValue() / 5;
    }

    /**
     * Calculate Accuracy - hit rate
     * Formula: 175 + DEX + (level if available, otherwise base 175)
     */
    public static int calculateAccuracy(LivingStats stats) {
        int level = stats.getLevelSystem().getLevel();
        return 175 + stats.getStat(StatTypes.DEXTERITY).getValue() + level;
    }

    /**
     * Calculate Evasion - base evasion rate
     * Formula: 100 + AGI + level
     */
    public static int calculateBaseEvasion(LivingStats stats) {
        int level = stats.getLevelSystem().getLevel();
        return 100 + stats.getStat(StatTypes.AGILITY).getValue() + level;
    }

    /**
     * Calculate Evasion bonus from AGI
     * Formula: AGI / 10 (simplified)
     */
    public static int calculateBonusEvasion(LivingStats stats) {
        return stats.getStat(StatTypes.AGILITY).getValue() / 10;
    }

    /**
     * Calculate Critical Rate (CRIT) - critical hit chance
     * Formula: 1 + LUK / 3 (simplified)
     */
    public static int calculateCRIT(LivingStats stats) {
        return 1 + stats.getStat(StatTypes.LUCK).getValue() / 3;
    }

    /**
     * Calculate Attack Speed (ASPD) - attack speed rating
     * Formula: 150 + AGI + DEX/4 (simplified)
     */
    public static int calculateASPD(LivingStats stats) {
        int agi = stats.getStat(StatTypes.AGILITY).getValue();
        int dex = stats.getStat(StatTypes.DEXTERITY).getValue();
        return 150 + agi + (dex / 4);
    }

    /**
     * Data class to hold all derived stats for easy display
     */
    public static class DerivedStats {
        public final int baseATK;
        public final int bonusATK;
        public final int baseMATK;
        public final int bonusMATK;
        public final int baseDEF;
        public final int bonusDEF;
        public final int baseMDEF;
        public final int bonusMDEF;
        public final int accuracy;
        public final int baseEvasion;
        public final int bonusEvasion;
        public final int crit;
        public final int aspd;

        public DerivedStats(LivingStats stats) {
            this.baseATK = calculateBaseATK(stats);
            this.bonusATK = calculateBonusATK(stats);
            this.baseMATK = calculateBaseMATK(stats);
            this.bonusMATK = calculateBonusMATK(stats);
            this.baseDEF = calculateBaseDEF(stats);
            this.bonusDEF = calculateBonusDEF(stats);
            this.baseMDEF = calculateBaseMDEF(stats);
            this.bonusMDEF = calculateBonusMDEF(stats);
            this.accuracy = calculateAccuracy(stats);
            this.baseEvasion = calculateBaseEvasion(stats);
            this.bonusEvasion = calculateBonusEvasion(stats);
            this.crit = calculateCRIT(stats);
            this.aspd = calculateASPD(stats);
        }
    }
}