package sypztep.peony.mixin.core.combat.hitevasion;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.peony.client.payload.AddTextParticlesPayloadS2C;
import sypztep.peony.common.init.ModAttributes;
import sypztep.peony.common.init.ModParticle;
import sypztep.peony.module.combat.EntityCombatAttributes;
import sypztep.peony.module.combat.interfaces.CriticalOverhaul;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Stack;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CriticalOverhaul {
    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attribute);

    @Shadow
    @Nullable
    protected Stack<DamageContainer> damageContainers;
    @Unique
    private static final float SOUND_VOLUME = 1.0F;
    @Unique
    private static final float SOUND_PITCH = 1.0F;
    @Unique
    private static final float MISS_SOUND_VOLUME = 0.8F;
    @Unique
    private static final float MISS_SOUND_PITCH = 1.2F;
    @Unique
    private final Random critRandom = new Random();
    @Unique
    private boolean isCrit;
    @Unique
    private boolean isHit;
    @Unique
    protected LivingEntity target = (LivingEntity) (Object) this;


    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;onLivingDamagePre(Lnet/minecraft/world/entity/LivingEntity;Lnet/neoforged/neoforge/common/damagesource/DamageContainer;)F"))
    private void monsterCrit(DamageSource source, float amount, CallbackInfo ci) {
        if (!this.level().isClientSide()) {
            Entity attacker = source.getEntity();

            if (attacker instanceof CriticalOverhaul criticalAttacker) {
                if (!(attacker instanceof Player)) {
                    float critDamage = criticalAttacker.calCritDamage(amount);
                    boolean shouldCrit = critDamage != amount;
                    this.setCritical(shouldCrit);
                    if (shouldCrit) {
                        applyCriticalParticle(this);
                        playCriticalSound(attacker);
                        this.damageContainers.peek().setNewDamage(critDamage);
                    }
                }
            }
        }
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Ljava/util/Stack;push(Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER), cancellable = true)
    private void handleDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.level().isClientSide()) return;

        Entity attacker = source.getEntity();
        if (!(attacker instanceof LivingEntity livingEntity)) return;

        isHit = calculateHit(livingEntity);
        if (!isHit) {
            sendMissingParticles(livingEntity);
            playMissingSound();
            cir.setReturnValue(false);
        } else if (attacker instanceof CriticalOverhaul criticalAttacker && criticalAttacker.isCritical()) {
            playCriticalSound(attacker);
            applyCriticalParticle(this);
        }
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void handleCrit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!this.level().isClientSide() && source.getEntity() instanceof CriticalOverhaul criticalAttacker) {
            if (this.isCritical()) applyCriticalParticle(this);
            criticalAttacker.setCritical(false); //reset
        }
    }

    @Override
    public void setCritical(boolean setCrit) {
        if (this.level().isClientSide()) return;
        this.isCrit = setCrit;
    }

    @Override
    public Random getRand() {
        return this.critRandom;
    }

    @Override
    public boolean isCritical() {
        return this.isCrit;
    }


    @Override
    public float getCritDamage() {
        return (float) this.getAttributeValue(ModAttributes.CRIT_DAMAGE);
    }

    @Override
    public float getCritRate() {
        return (float) this.getAttributeValue(ModAttributes.CRIT_CHANCE);
    }

    @Unique
    public boolean isHit() {
        return isHit;
    }

    @Unique
    private void playCriticalSound(Entity attacker) {
        attacker.level().playSound(this, this.getOnPos(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.HOSTILE, SOUND_VOLUME, SOUND_PITCH);
    }

    @Unique
    private void playMissingSound() {
        target.level().playSound(this, target.getOnPos(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, MISS_SOUND_VOLUME, MISS_SOUND_PITCH + (float) random.nextGaussian());
    }


    @Unique
    public void applyCriticalParticle(Entity target) {
        if (!isHit) return;
        if (target.level() instanceof ServerLevel)
            PacketDistributor.sendToPlayersTrackingEntity(target, new AddTextParticlesPayloadS2C(this.getId(), ModParticle.CRITICAL.getFlag()));
    }

    @Unique
    private boolean calculateHit(LivingEntity attacker) {
        EntityCombatAttributes attackerAttributes = new EntityCombatAttributes(attacker);
        EntityCombatAttributes defenderAttributes = new EntityCombatAttributes(target);
        return attackerAttributes.calculateHit(defenderAttributes, attacker);
    }

    @Unique
    private void sendMissingParticles(LivingEntity attacker) {
        if (target.level() instanceof ServerLevel)
            PacketDistributor.sendToPlayersTrackingEntity(target, new AddTextParticlesPayloadS2C(this.getId(), ModParticle.MISSING.getFlag()));
        if (attacker.level() instanceof ServerLevel)
            PacketDistributor.sendToPlayersTrackingEntity(attacker, new AddTextParticlesPayloadS2C(this.getId(), ModParticle.MISSING_MONSTERS.getFlag()));
    }
}