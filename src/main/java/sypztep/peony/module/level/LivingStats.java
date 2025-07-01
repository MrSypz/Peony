package sypztep.peony.module.level;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class LivingStats {
    private final Map<StatTypes, Stat> stats;
    private final LevelSystem levelSystem;

    public LivingStats() {
        this.stats = new EnumMap<>(StatTypes.class);
        this.levelSystem = new LevelSystem();
        for (StatTypes statType : StatTypes.values()) {
            this.stats.put(statType, statType.createStat(0));
        }
    }

    public Stat getStat(StatTypes statType) {
        return stats.get(statType);
    }

    protected void allocatePoints(StatTypes statType, int points) {
        Stat stat = getStat(statType);
        if (stat != null) {
            stat.increase(points);
        }
    }

    public void useStatPoint(StatTypes types, int points) {
        int perPoint = this.getStat(types).getIncreasePerPoint();
        int statPoints = this.getLevelSystem().getStatPoints();

        if (statPoints >= perPoint * points) {
            this.getLevelSystem().subtractStatPoints(perPoint * points);
            allocatePoints(types, points);
        }
    }
    public void resetStats(ServerPlayer player) {
        stats.values().forEach(stat -> stat.reset(player, levelSystem));
    }

    public LevelSystem getLevelSystem() {
        return levelSystem;
    }

    //------------------------utility---------------------
    public void writeToNbt(CompoundTag tag) {
        stats.forEach((type, stat) -> {
            CompoundTag statTag = new CompoundTag();
            stat.writeToNbt(statTag);
            tag.put(type.name(), statTag);
        });
        levelSystem.writeToNbt(tag);
    }

    public void readFromNbt(CompoundTag tag) {
        stats.forEach((type, stat) -> stat.readFromNbt(tag.getCompound(type.name())));
        levelSystem.readFromNbt(tag);
    }
}


