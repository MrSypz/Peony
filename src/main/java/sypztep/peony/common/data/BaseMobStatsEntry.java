package sypztep.peony.common.data;


import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public record BaseMobStatsEntry(
        int str,
        int agi,
        int dex,
        int vit,
        int anint,
        int luk,
        int lvl,
        int exp
) {
    public static final Map<EntityType<?>, BaseMobStatsEntry> BASEMOBSTATS_MAP = new HashMap<>();

    public static BaseMobStatsEntry getStatsFor(EntityType<?> entityType) {
        return BASEMOBSTATS_MAP.getOrDefault(entityType, getDefaultStats());
    }

    private static BaseMobStatsEntry getDefaultStats() {
        return new BaseMobStatsEntry(0, 0, 0, 0, 0, 0, 1, 0);
    }
}