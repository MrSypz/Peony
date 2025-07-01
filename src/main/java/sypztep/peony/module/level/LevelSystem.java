package sypztep.peony.module.level;

import net.minecraft.nbt.CompoundTag;
import sypztep.peony.PeonyConfig;

import java.util.List;

public class LevelSystem {
    private int level;
    private int xp;
    private int xpToNextLevel;
    private int statPoints;
    private static final int MAX_LEVEL = PeonyConfig.MAX_LEVEL.getAsInt();

    public LevelSystem() {
        this.level = 1;
        this.xp = 0;
        this.statPoints = PeonyConfig.START_STAT_POINTS.getAsInt();
        this.xpToNextLevel = calculateXpForNextLevel(level);
    }

    private int calculateXpForNextLevel(int level) {
        List<? extends Integer> xpTable = PeonyConfig.XP_TABLE.get();
        if (level >= 1 && level - 1 < xpTable.size()) return xpTable.get(level - 1);
        if (!xpTable.isEmpty()) return xpTable.getLast();
        return 1000;
    }

    // MIGRATED: Enhanced addExperience with return value and max level check
    public boolean addExperience(int amount) {
        if (amount <= 0) return false;

        // Ragnarok-style: No XP gain at max level
        if (isAtMaxLevel() && xp >= xpToNextLevel) return false;

        xp += amount;

        while (xp >= xpToNextLevel && level < MAX_LEVEL) {
            levelUp();
        }

        // Cap XP at max level
        if (level >= MAX_LEVEL) {
            xp = Math.min(xp, xpToNextLevel);
        }

        return true;
    }

    // MIGRATED: Enhanced subtractExperience with max level check
    public void subtractExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to subtract cannot be negative");
        }

        if (isAtMaxLevel()) return;

        xp = Math.max(0, xp - amount);
    }

    // MIGRATED: Enhanced setXp with max level handling
    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
        if (isAtMaxLevel()) {
            this.xp = Math.min(this.xp, xpToNextLevel);
        }
    }

    private void levelUp() {
        if (level >= MAX_LEVEL) return;

        int excessXp = xp - xpToNextLevel;
        level++;
        statPoints += getGainStatPointsForLevel(level);

        if (level < MAX_LEVEL) {
            updateNextLvl();
            xp = Math.max(0, excessXp);
        } else {
            // At max level, set XP to requirement (full bar)
            xp = xpToNextLevel;
        }
    }

    public int getGainStatPointsForLevel(int level) {
        return (level / 5) + 3;
    }

    public void updateNextLvl() {
        xpToNextLevel = calculateXpForNextLevel(level);
    }

    // MIGRATED: Essential for Ragnarok-style max level behavior
    public boolean isAtMaxLevel() {
        return level >= MAX_LEVEL;
    }

    // MIGRATED: Get XP needed for next level (0 at max level)
    public int getXpNeeded() {
        if (isAtMaxLevel()) return 0;
        return Math.max(0, xpToNextLevel - xp);
    }

    // MIGRATED: Data validation and fixing
    public void validateAndFix() {
        level = Math.max(1, Math.min(level, MAX_LEVEL));
        statPoints = Math.max(0, statPoints);
        updateNextLvl();
        xp = Math.max(0, xp);
        if (isAtMaxLevel()) {
            xp = Math.min(xp, xpToNextLevel);
        }
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(level, MAX_LEVEL));
        updateNextLvl();
        this.xp = 0;
    }

    public int getXp() {
        return xp;
    }

    public int getXpToNextLevel() {
        return xpToNextLevel;
    }

    // MIGRATED: Enhanced XP percentage calculation
    public double getXpPercentage() {
        if (xpToNextLevel == 0) {
            return 100.0; // Show 100% at max level
        }
        return ((double) xp / xpToNextLevel) * 100.0;
    }

    public int getStatPoints() {
        return statPoints;
    }

    public void setStatPoints(int statPoints) {
        this.statPoints = statPoints;
    }

    public void addStatPoints(int statPoints) {
        this.statPoints += statPoints;
    }

    public void subtractStatPoints(int points) {
        this.statPoints -= points;
    }

    public void writeToNbt(CompoundTag tag) {
        tag.putInt("Level", level);
        tag.putInt("XP", xp);
        tag.putInt("XPToNextLevel", xpToNextLevel);
        tag.putInt("StatPoints", statPoints);
    }

    public void readFromNbt(CompoundTag tag) {
        this.level = tag.getInt("Level");
        this.xp = tag.getInt("XP");
        this.xpToNextLevel = tag.getInt("XPToNextLevel");
        this.statPoints = tag.getInt("StatPoints");

        // MIGRATED: Validate and fix data after loading
        validateAndFix();
    }
}