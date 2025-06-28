package sypztep.peony.module.combat.element;


import sypztep.peony.module.combat.CombatAttribute;

public final class Evasion extends CombatAttribute {
    private static final short BASE_EVASION = 80;

    public Evasion(double baseValue) {
        super(baseValue);
    }

    @Override
    public double calculateEffect() {
        return BASE_EVASION + getTotalValue();
    }
}