package sypztep.peony.module.combat;

import net.minecraft.world.entity.LivingEntity;
import sypztep.peony.common.init.ModAttributes;
import sypztep.peony.module.combat.util.CombatHelper;

public record EntityCombatAttributes(
        double accuracy,
        double evasion
) {
    public EntityCombatAttributes(LivingEntity entity) {
        this(
                CombatHelper.calculateAccuracy(entity.getAttributeValue(ModAttributes.ACCURACY)),
                CombatHelper.calculateEvasion(entity.getAttributeValue(ModAttributes.EVASION))
        );
    }

    public boolean calculateHit(EntityCombatAttributes defender, LivingEntity attacker) {
        double hitChance = CombatHelper.calculateHitChance(this.accuracy, defender.evasion);
        return attacker.level().random.nextDouble() < hitChance;
    }
}