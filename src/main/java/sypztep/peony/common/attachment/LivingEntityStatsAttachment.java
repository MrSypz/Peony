package sypztep.peony.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import sypztep.peony.common.util.StatsSync;
import sypztep.peony.module.level.LevelSystem;
import sypztep.peony.module.level.LivingStats;
import sypztep.peony.module.level.StatTypes;

import java.text.DecimalFormat;

public class LivingEntityStatsAttachment {
    private final LivingStats livingStats;

    public LivingEntityStatsAttachment() {
        this.livingStats = new LivingStats();
    }

    public LivingStats getLivingStats() { return livingStats; }

    // ===== FLATTENED API - Direct access methods =====
    
    /**
     * Adds experience directly, returning whether any was actually added.
     * Handles level ups automatically and triggers sync.
     */
    public boolean addExperience(LivingEntity entity, int amount) {
        int oldLevel = getLevel();
        int oldXp = getXp();
        int oldStatPoints = getStatPoints();
        
        boolean added = livingStats.getLevelSystem().addExperience(amount);
        
        if (added) {
            StatsSync.smartSync(entity, this, oldLevel, oldXp, oldStatPoints);
        }
        
        return added;
    }

    /**
     * Sets experience directly with sync.
     */
    public void setXp(LivingEntity entity, int xp) {
        int oldXp = getXp();
        livingStats.getLevelSystem().setXp(xp);
        
        if (oldXp != getXp()) {
            StatsSync.syncXp(entity, this);
        }
    }

    /**
     * Sets stat points directly with sync.
     */
    public void setStatPoints(LivingEntity entity, int amount) {
        int oldStatPoints = getStatPoints();
        livingStats.getLevelSystem().setStatPoints(amount);
        
        if (oldStatPoints != getStatPoints()) {
            StatsSync.syncStatPoints(entity, this);
        }
    }
    
    /**
     * Sets level directly with sync.
     */
    public void setLevel(LivingEntity entity, int level) {
        int oldLevel = getLevel();
        livingStats.getLevelSystem().setLevel(level);
        
        if (oldLevel != getLevel()) {
            StatsSync.syncLevel(entity, this);
        }
    }

    // ===== SIMPLE ACCESSOR METHODS =====
    
    public void addExperience(int amount) {
        livingStats.getLevelSystem().addExperience(amount);
    }

    public void setStatPoints(int amount) {
        livingStats.getLevelSystem().setStatPoints(amount);
    }
    public int getStatPoints() {
        return livingStats.getLevelSystem().getStatPoints();
    }

    public int getLevel() {
        return livingStats.getLevelSystem().getLevel();
    }

    public LevelSystem getLevelSystem() {
        return livingStats.getLevelSystem();
    }

    public int getNextLevel() {
        return getLevel() + 1;
    }

    public String getXpPercentage() {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(livingStats.getLevelSystem().getXpPercentage()) + "%";
    }

    public int getXp() {
        return livingStats.getLevelSystem().getXp();
    }

    public int getXpToNextLevel() {
        return livingStats.getLevelSystem().getXpToNextLevel();
    }
    
    // ===== STAT ACCESS METHODS =====
    
    /**
     * Use stat points for a specific stat type with sync.
     */
    public void useStatPoint(LivingEntity entity, StatTypes statType, int points) {
        int oldStatPoints = getStatPoints();
        livingStats.useStatPoint(statType, points);
        
        if (oldStatPoints != getStatPoints()) {
            StatsSync.syncStatPoints(entity, this);
        }
    }
    
    /**
     * Reset all stats and return points with sync.
     */
    public void resetStats(LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            int oldStatPoints = getStatPoints();
            livingStats.resetStats(serverPlayer);
            
            if (oldStatPoints != getStatPoints()) {
                StatsSync.syncStatPoints(entity, this);
            }
        }
    }
    
    // ===== HELPER METHODS =====
    public static String formatNumber(int number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fK", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }
    
    // ===== CENTRALIZED SYNC METHODS =====
    
    /**
     * Synchronizes level only.
     */
    public void syncLevel(LivingEntity entity) {
        StatsSync.syncLevel(entity, this);
    }

    /**
     * Synchronizes experience and experience to next level.
     */
    public void syncXp(LivingEntity entity) {
        StatsSync.syncXp(entity, this);
    }

    /**
     * Synchronizes stat points only.
     */
    public void syncStatPoints(LivingEntity entity) {
        StatsSync.syncStatPoints(entity, this);
    }

    /**
     * Synchronizes all stats.
     */
    public void syncAll(LivingEntity entity) {
        StatsSync.syncAll(entity, this);
    }
    
    /**
     * Synchronizes stats to a specific player.
     */
    public void syncToPlayer(ServerPlayer player) {
        StatsSync.syncToPlayer(player, this);
    }

    /**
     * Smart synchronization - only syncs changed values.
     */
    public void smartSync(LivingEntity entity, int oldLevel, int oldXp, int oldStatPoints) {
        StatsSync.smartSync(entity, this, oldLevel, oldXp, oldStatPoints);
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, LivingEntityStatsAttachment> {
        @Override
        public LivingEntityStatsAttachment read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider lookup) {
            LivingEntityStatsAttachment attachment = new LivingEntityStatsAttachment();
            attachment.livingStats.readFromNbt(tag);
            return attachment;
        }

        @Override
        public CompoundTag write(LivingEntityStatsAttachment attachment, HolderLookup.Provider lookup) {
            CompoundTag tag = new CompoundTag();
            attachment.livingStats.writeToNbt(tag);
            return tag;
        }
    }
}