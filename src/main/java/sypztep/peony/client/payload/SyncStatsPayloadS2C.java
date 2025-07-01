package sypztep.peony.client.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sypztep.peony.Peony;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;
import sypztep.peony.common.init.ModAttachments;

public record SyncStatsPayloadS2C(
        int entityId,
        byte syncMask,  // Bitfield to determine what to sync
        int level,
        int xp,
        int xpToNext,
        int statPoints
) implements CustomPacketPayload {

    public static final Type<SyncStatsPayloadS2C> TYPE =
            new Type<>(Peony.id("sync_stats"));

    public static final byte SYNC_LEVEL = 1;      // 0001
    public static final byte SYNC_XP = 1 << 1;         // 0010
    public static final byte SYNC_XP_TO_NEXT = 1 << 2; // 0100
    public static final byte SYNC_STAT_POINTS = 1 << 3; // 1000
    public static final byte SYNC_ALL = SYNC_LEVEL | SYNC_XP | SYNC_XP_TO_NEXT | SYNC_STAT_POINTS;

    public static final StreamCodec<ByteBuf, SyncStatsPayloadS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncStatsPayloadS2C::entityId,
            ByteBufCodecs.BYTE, SyncStatsPayloadS2C::syncMask,
            ByteBufCodecs.VAR_INT, SyncStatsPayloadS2C::level,
            ByteBufCodecs.VAR_INT, SyncStatsPayloadS2C::xp,
            ByteBufCodecs.VAR_INT, SyncStatsPayloadS2C::xpToNext,
            ByteBufCodecs.VAR_INT, SyncStatsPayloadS2C::statPoints,
            SyncStatsPayloadS2C::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Helper methods to check what should be synced
    public boolean shouldSyncLevel() {
        return (syncMask & SYNC_LEVEL) != 0;
    }

    public boolean shouldSyncXp() {
        return (syncMask & SYNC_XP) != 0;
    }

    public boolean shouldSyncXpToNext() {
        return (syncMask & SYNC_XP_TO_NEXT) != 0;
    }

    public boolean shouldSyncStatPoints() {
        return (syncMask & SYNC_STAT_POINTS) != 0;
    }

    // Builder pattern for easy creation
    public static class Builder {
        private int entityId;
        private byte syncMask = 0;
        private int level = 0;
        private int xp = 0;
        private int xpToNext = 0;
        private int statPoints = 0;

        public Builder(int entityId) {
            this.entityId = entityId;
        }

        public Builder level(int level) {
            this.level = level;
            this.syncMask |= SYNC_LEVEL;
            return this;
        }

        public Builder xp(int xp) {
            this.xp = xp;
            this.syncMask |= SYNC_XP;
            return this;
        }

        public Builder xpToNext(int xpToNext) {
            this.xpToNext = xpToNext;
            this.syncMask |= SYNC_XP_TO_NEXT;
            return this;
        }

        public Builder statPoints(int statPoints) {
            this.statPoints = statPoints;
            this.syncMask |= SYNC_STAT_POINTS;
            return this;
        }

        public Builder all(int level, int xp, int xpToNext, int statPoints) {
            return level(level).xp(xp).xpToNext(xpToNext).statPoints(statPoints);
        }

        public SyncStatsPayloadS2C build() {
            return new SyncStatsPayloadS2C(entityId, syncMask, level, xp, xpToNext, statPoints);
        }
    }

    public static void handle(SyncStatsPayloadS2C packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.level == null) return;

                Entity entity = minecraft.level.getEntity(packet.entityId());
                if (!(entity instanceof LivingEntity livingEntity)) return;

                LivingEntityStatsAttachment stats = livingEntity.getData(ModAttachments.LIVINGSTATS.get());

                if (packet.shouldSyncLevel()) {
                    stats.getLevelSystem().setLevel(packet.level());
                }

                if (packet.shouldSyncXp()) {
                    stats.getLevelSystem().setXp(packet.xp());
                }

                if (packet.shouldSyncXpToNext()) {
                    // Update xpToNextLevel - you might need to add a setter in LevelSystem
                    stats.getLevelSystem().updateNextLvl();
                }

                if (packet.shouldSyncStatPoints()) {
                    stats.getLevelSystem().setStatPoints(packet.statPoints());
                }
            }
        });
    }
}