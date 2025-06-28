package sypztep.peony.module.combat;

import net.minecraft.world.entity.LivingEntity;
import sypztep.peony.common.init.ModAttributes;
import sypztep.peony.module.combat.element.Accuracy;
import sypztep.peony.module.combat.element.Evasion;

public record EntityCombatAttributes(
        Accuracy accuracy,
        Evasion evasion
) {
    public EntityCombatAttributes(LivingEntity entity) {
        this(
                new Accuracy(entity.getAttributeValue(ModAttributes.ACCURACY)),
                new Evasion(entity.getAttributeValue(ModAttributes.EVASION))
        );
    }

    public boolean calculateHit(EntityCombatAttributes defender, LivingEntity attacker) {
        double hitChance = accuracy.calculateHitChance(defender.evasion);
        return attacker.level().random.nextDouble() < hitChance;
    }
}