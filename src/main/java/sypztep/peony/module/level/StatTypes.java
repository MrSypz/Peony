package sypztep.peony.module.level;

import sypztep.peony.module.level.element.*;

public enum StatTypes {
    STRENGTH("strength","Str", StrengthStat::new),
    AGILITY("agility","Agi", AgilityStat::new),
    VITALITY("vitality","Vit", VitalityStat::new),
    INTELLIGENCE("intelligence","Int", IntelligenceStat::new),
    DEXTERITY("dexterity","Dex", DexterityStat::new),
    LUCK("luck","Luk", LuckStat::new);

    private final String name;
    private final String aka;
    private final StatSupplier supplier;

    StatTypes(String name, String aka, StatSupplier supplier) {
        this.name = name;
        this.aka = aka;
        this.supplier = supplier;
    }

    public String getName() {
        return name;
    }

    public String getAka() {
        return aka;
    }

    public Stat createStat(int initialValue) {
        return supplier.create(initialValue);
    }

    @FunctionalInterface
    private interface StatSupplier {
        Stat create(int initialValue);
    }
}
