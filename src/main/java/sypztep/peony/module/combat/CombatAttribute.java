package sypztep.peony.module.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class CombatAttribute {
    protected final double baseValue;
    private final HashMap<String, Double> bonuses = new HashMap<>();

    protected CombatAttribute(double baseValue) {
        this.baseValue = baseValue;
    }

    public void addBonus(String bonusId, double value) {
        bonuses.put(bonusId, value);
    }

    public boolean removeBonus(String bonusId) {
        return bonuses.remove(bonusId) != null;
    }

    public double getBonus(String bonusId) {
        return bonuses.getOrDefault(bonusId, 0.0);
    }

    public boolean hasBonus(String bonusId) {
        return bonuses.containsKey(bonusId);
    }

    public Set<String> getBonusIds() {
        return bonuses.keySet();
    }

    public double getTotalBonusValue() {
        return bonuses.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public final double getTotalValue() {
        return baseValue + getTotalBonusValue();
    }

    public void clearBonuses() {
        bonuses.clear();
    }

    public Map<String, Double> getAllBonuses() {
        return bonuses; // Direct reference - caller responsibility
    }

    public abstract double calculateEffect();
}