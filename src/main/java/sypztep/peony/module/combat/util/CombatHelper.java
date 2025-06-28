package sypztep.peony.module.combat.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import sypztep.peony.client.particle.TextParticleProvider;
import sypztep.peony.client.payload.AddTextParticlesPayloadS2C;
import sypztep.peony.common.init.ModAttributes;
import sypztep.peony.common.init.ModParticle;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public final class CombatHelper {
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
            if (condition.test(attacker, target)) {
                return effect.apply(attacker, target, damage);
            }
            return damage;
        }
    }

    public static float modifyDamage(LivingEntity target, float baseDamage, DamageSource source) {
        if (!(source.getEntity() instanceof LivingEntity attacker)) return baseDamage;

        float modified = baseDamage;
        for (CombatModifierType type : CombatModifierType.values()) {
            modified = type.applyIfValid(attacker, target, modified);
        }
        return modified;
    }

    // ==== Modifier Condition Methods ====

    private static boolean isBackAttack(LivingEntity attacker, LivingEntity target) {
        float angleDiff = Math.abs(Mth.degreesDifference(target.getYHeadRot(), attacker.getYRot()));
        return angleDiff <= 75.0F;
    }

    // ==== Particle Sending ====

    private static void sendCombatParticles(LivingEntity target, LivingEntity attacker, TextParticleProvider particleText) {
        if (!(attacker instanceof Player || target instanceof Player)) return;
        getTrackingPlayers(target, attacker).forEach(player ->
                AddTextParticlesPayloadS2C.send(player, target.getId(), particleText)
        );
    }

    private static Set<ServerPlayer> getTrackingPlayers(LivingEntity... entities) {
        return Arrays.stream(entities)
                .filter(e -> e.level() instanceof ServerLevel)
                .flatMap(e -> PlayerLookup.tracking((ServerLevel) e.level(), e.chunkPosition()).stream())
                .collect(Collectors.toSet());
    }

    private CombatHelper() {} // Utility class
}
