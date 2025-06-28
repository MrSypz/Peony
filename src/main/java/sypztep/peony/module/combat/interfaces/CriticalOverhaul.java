package sypztep.peony.module.combat.interfaces;

import java.util.Random;

public interface CriticalOverhaul {
    void setCritical(boolean setCrit);
    boolean isCritical();
    Random getRand();

    default float calCritDamage(float amount) {
        float totalCritRate = this.getTotalCritRate();
        float totalCritDMG = this.getTotalCritDamage();

        if (!this.isCritical() &&
                (!(totalCritDMG > 0.0F) || !(totalCritRate > 0.0F) ||
                        !(this.getRand().nextFloat() < totalCritRate))) {
            return amount;
        } else {
            this.setCritical(true);
            return amount * (1.0F + totalCritDMG);
        }
    }

    default float getTotalCritRate() {
        return (this.getCritRate() + this.getCritRateFromEquipped());
    }

    default float getTotalCritDamage() {
        return (this.getCritDamage() + this.getCritDamageFromEquipped());
    }

    default float getCritRate() {
        return 0.0F;
    }

    default float getCritDamage() {
        return 0.0F;
    }

    default float getCritRateFromEquipped() {
        return 0.0F;
    }

    default float getCritDamageFromEquipped() {
        return 0.0F;
    }
}
