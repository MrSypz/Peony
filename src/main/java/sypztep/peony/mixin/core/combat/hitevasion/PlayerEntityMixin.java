package sypztep.peony.mixin.core.combat.hitevasion;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {
    @Unique
    private boolean bl;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }
    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    private float storedamage(float original) {
        float modifiedDamage = this.calCritDamage(original);
        this.bl = original != modifiedDamage;
        return modifiedDamage;
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "CONSTANT", args = "floatValue=1.5"))
    private float applyCritDmg(float original) {
        float modifiedCritDamage = this.bl ? 1.0F : (this.isCritical() ? (1.0F + this.getTotalCritDamage()) : original);
        this.bl = false;
        return modifiedCritDamage;
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 2)
    private boolean doCrit(boolean original, Entity target) {
        if (!this.level().isClientSide()) {
            if (this.isCritical() && isHit()) {
                this.setCritical(true);
                applyCriticalParticle(target);
                return true;
            }
        }
        return false;
    }
}
