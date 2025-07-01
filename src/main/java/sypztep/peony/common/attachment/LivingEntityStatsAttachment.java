package sypztep.peony.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import net.neoforged.neoforge.network.PacketDistributor;
import sypztep.peony.client.payload.SyncStatsPayloadS2C;
import sypztep.peony.module.level.LevelSystem;
import sypztep.peony.module.level.LivingStats;

import java.text.DecimalFormat;

public class LivingEntityStatsAttachment {
    private final LivingStats livingStats;

    public LivingEntityStatsAttachment() {
        this.livingStats = new LivingStats();
    }

    public LivingStats getLivingStats() { return livingStats; }

    public void addExperience(int amount) {
        livingStats.getLevelSystem().addExperience(amount);
    }

    public void setStatPoints(int amount) {
        livingStats.getLevelSystem().setStatPoints(amount);
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

    public int getNextXpLevel() {
        return livingStats.getLevelSystem().getXpToNextLevel();
    }
    // HELPER
    public static String formatNumber(int number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fK", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }
    public void syncLevel(LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        SyncStatsPayloadS2C packet = new SyncStatsPayloadS2C.Builder(entity.getId())
                .level(getLevel())
                .build();

        sendToTrackingPlayers(entity, packet);
    }

    public void syncXp(LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        SyncStatsPayloadS2C packet = new SyncStatsPayloadS2C.Builder(entity.getId())
                .xp(getXp())
                .xpToNext(getNextXpLevel())
                .build();

        sendToTrackingPlayers(entity, packet);
    }

    public void syncStatPoints(LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        SyncStatsPayloadS2C packet = new SyncStatsPayloadS2C.Builder(entity.getId())
                .statPoints(getLevelSystem().getStatPoints())
                .build();

        sendToTrackingPlayers(entity, packet);
    }

    public void syncAll(LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        SyncStatsPayloadS2C packet = new SyncStatsPayloadS2C.Builder(entity.getId())
                .all(getLevel(), getXp(), getNextXpLevel(), getLevelSystem().getStatPoints())
                .build();

        sendToTrackingPlayers(entity, packet);
    }

    // Smart sync - only sync what changed
    public void smartSync(LivingEntity entity, int oldLevel, int oldXp, int oldStatPoints) {
        if (entity.level().isClientSide()) return;

        SyncStatsPayloadS2C.Builder builder = new SyncStatsPayloadS2C.Builder(entity.getId());

        if (getLevel() != oldLevel) {
            builder.level(getLevel());
        }

        if (getXp() != oldXp) {
            builder.xp(getXp()).xpToNext(getNextXpLevel());
        }

        if (getLevelSystem().getStatPoints() != oldStatPoints) {
            builder.statPoints(getLevelSystem().getStatPoints());
        }

        SyncStatsPayloadS2C packet = builder.build();
        if (packet.syncMask() != 0) { // Only send if something changed
            sendToTrackingPlayers(entity, packet);
        }
    }

    private void sendToTrackingPlayers(LivingEntity entity, SyncStatsPayloadS2C packet) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, packet);
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