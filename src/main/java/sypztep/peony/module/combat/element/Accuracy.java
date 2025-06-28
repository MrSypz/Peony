package sypztep.peony.module.combat.element;

import net.minecraft.util.Mth;
import sypztep.peony.module.combat.CombatAttribute;

public final class Accuracy extends CombatAttribute {
    private static final short BASE_ACCURACY = 70;

    public Accuracy(double baseValue) {
        super(baseValue);
    }

    @Override
    public double calculateEffect() {
        return BASE_ACCURACY + getTotalValue();
    }

    public double calculateHitChance(Evasion targetEvasion) {
        double accuracy = calculateEffect();
        double evasion = targetEvasion.calculateEffect();

        // Hit Rate Formula: (accuracy - evasion + 80) / 100
        double hitChance = (accuracy - evasion + 80.0) / 100.0;
        return Mth.clamp(hitChance, 0.05, 1.0); // 5% minimum, 100% maximum
    }
}