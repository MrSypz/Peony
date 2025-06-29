package sypztep.peony.mixin.core.combat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.peony.client.util.ParticleUtil;

import java.awt.*;
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private float previousHealth;

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void healing(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (previousHealth == 0) {
            previousHealth = entity.getHealth();
            return;
        }

        float newHealth = entity.getHealth();
        float healthDiff = newHealth - previousHealth;
        previousHealth = newHealth;

        if (healthDiff < 0) {
            String damageText = formatNumber(-healthDiff);
            ParticleUtil.spawnTextParticle(entity, Component.literal("✖ " + damageText), new Color(0xD43333), -0.055f, -0.6f);
            return;
        }

        if (healthDiff > 0 && healthDiff != entity.getMaxHealth()) {
            String healText = formatNumber(healthDiff);
            ParticleUtil.spawnTextParticle(entity, Component.literal("❤ " + healText), new Color(0x4DBD44), -0.055f, -0.6f);
        }
    }

    public String formatNumber(double number) {
        String suffix = "";
        double displayNumber = number;

        if (number >= 1_000_000_000) {
            suffix = "B";
            displayNumber = number / 1_000_000_000;
        } else if (number >= 1_000_000) {
            suffix = "M";
            displayNumber = number / 1_000_000;
        } else if (number >= 1_000) {
            suffix = "K";
            displayNumber = number / 1_000;
        }

        // Remove .00, keep up to 2 decimals if needed
        if (displayNumber % 1 == 0) {
            return String.format("%.0f%s", displayNumber, suffix);
        } else if ((displayNumber * 10) % 1 == 0) {
            return String.format("%.1f%s", displayNumber, suffix);
        } else {
            return String.format("%.2f%s", displayNumber, suffix);
        }
    }
}
