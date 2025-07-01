package sypztep.peony.module.combat.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import sypztep.peony.client.util.TextParticleProvider;
import sypztep.peony.client.payload.AddTextParticlesPayloadS2C;
import sypztep.peony.common.init.ModAttributes;
import sypztep.peony.common.init.ModParticle;

import java.util.function.BiPredicate;

public final class CombatHelper {
    private static final short BASE_ACCURACY = 70;
    private static final short BASE_EVASION = 80;

    public static double calculateAccuracy(double baseValue) {
        return BASE_ACCURACY + baseValue;
    }

    public static double calculateEvasion(double baseValue) {
        return BASE_EVASION + baseValue;
    }

    public static double calculateHitChance(double accuracy, double evasion) {
        double hitChance = (accuracy - evasion + 80.0) / 100.0;
        return Mth.clamp(hitChance, 0.05, 1.0); // 5% minimum, 100% maximum
    }

    public static boolean calculateHit(LivingEntity attacker, LivingEntity defender) {
        double attackerAccuracy = calculateAccuracy(attacker.getAttributeValue(ModAttributes.ACCURACY));
        double defenderEvasion = calculateEvasion(defender.getAttributeValue(ModAttributes.EVASION));

        double hitChance = calculateHitChance(attackerAccuracy, defenderEvasion);
        return attacker.level().random.nextDouble() < hitChance;
    }

    //Damage Modifier Event
    @FunctionalInterface
    private interface DamageModifier {
        float apply(LivingEntity attacker, LivingEntity target, float currentDamage);
    }

    private enum CombatModifierType {
        BACK_ATTACK(
               CombatHelper::isBackAttack,
                (attacker, target, damage) -> {
                    float multiplier = (float) attacker.getAttributeValue(ModAttributes.BACK_ATTACK) + 1.0F;
                    sendCombatParticles(target, attacker, ModParticle.BACK_ATTACK);
                    return damage * multiplier;
                }
        );

        private final BiPredicate<LivingEntity, LivingEntity> condition;
        private final DamageModifier effect;

        CombatModifierType(BiPredicate<LivingEntity, LivingEntity> condition, DamageModifier effect) {
            this.condition = condition;
            this.effect = effect;
        }

        float applyIfValid(LivingEntity attacker, LivingEntity target, float damage) {
            if (condition.test(attacker, target))
                return effect.apply(attacker, target, damage);

            return damage;
        }
    }

    public static float modifyDamage(LivingEntity target, float baseDamage, DamageSource source) {
        if (!(source.getEntity() instanceof LivingEntity attacker)) return baseDamage;

        float modified = baseDamage;
        for (CombatModifierType type : CombatModifierType.values())
            modified = type.applyIfValid(attacker, target, modified);
        return modified;
    }

    // ==== Modifier Condition Methods ====

    private static boolean isBackAttack(LivingEntity attacker, LivingEntity target) {
        float angleDiff = Math.abs(Mth.degreesDifference(target.getYHeadRot(), attacker.getYRot()));
        return angleDiff <= 75.0F;
    }

    // ==== Particle Sending ====

    private static void sendCombatParticles(LivingEntity target, LivingEntity attacker, TextParticleProvider particleText) {
        if (!(attacker instanceof ServerPlayer || target instanceof ServerPlayer)) return;

        sendToTracking(target, particleText);
        if (attacker != target)
            sendToTracking(attacker, particleText);
    }


    private static void sendToTracking(LivingEntity entity, TextParticleProvider particleText) {
        if (entity.level() instanceof ServerLevel) {
            AddTextParticlesPayloadS2C packet = new AddTextParticlesPayloadS2C(entity.getId(), particleText.getFlag());
            PacketDistributor.sendToPlayersTrackingEntity(entity, packet);
        }
    }

    private CombatHelper() {} // Utility class
}
