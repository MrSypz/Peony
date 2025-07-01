package sypztep.peony.module.level;

import java.util.List;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import sypztep.peony.module.level.util.AttributeModification;

public abstract class Stat {
    protected int baseValue;
    protected int currentValue  ;
    protected int increasePerPoint;
    protected int totalPointsUsed; // Track total points used

    public Stat(int baseValue) {
        this(baseValue, 1);
    }

    public Stat(int baseValue, int increasePerPoint) {
        this.baseValue = baseValue;
        this.currentValue = baseValue;
        this.increasePerPoint = increasePerPoint;
        this.totalPointsUsed = 0;
    }

    public void readFromNbt(CompoundTag tag) {
        this.currentValue = tag.getInt("CurrentValue");
        this.totalPointsUsed = tag.getInt("TotalPointsUsed"); // Read total points used from NBT
    }

    public void writeToNbt(CompoundTag tag) {
        tag.putInt("CurrentValue", this.currentValue);
        tag.putInt("TotalPointsUsed", this.totalPointsUsed); // Write total points used to NBT
    }

    public int getBaseValue() {
        return baseValue;
    }

    public int getValue() {
        return currentValue;
    }

    public int getIncreasePerPoint() {
        return increasePerPoint = 1 + (currentValue / 10);
    }

    public void increase(int points) {
        this.currentValue += points;
        this.totalPointsUsed += (points * increasePerPoint); // Track แต้มที่ใช้
    }
    //for monster
    public void setPoints(int points) {
        this.currentValue = points;
    }
    public void add(int points) {
        this.currentValue += points;
    }

    public abstract void applyPrimaryEffect(LivingEntity player);
    public abstract void applySecondaryEffect(LivingEntity player);
    protected void applyEffect(LivingEntity living, Holder<Attribute> attribute, ResourceLocation modifierId, AttributeModifier.Operation operation, ToDoubleFunction<Double> effectFunction) {
        AttributeInstance attributeInstance = living.getAttribute(attribute);
        if (attributeInstance != null) {
            double baseValue = living.getAttributeBaseValue(attribute);
            double effectValue = effectFunction.applyAsDouble(baseValue);

            if (modifierId == null) {
                throw new IllegalArgumentException("modifierId cannot be null report this on github");
            }
            // Remove existing modifier
            AttributeModifier existingModifier = attributeInstance.getModifier(modifierId);
            if (existingModifier != null) {
                attributeInstance.removeModifier(existingModifier);
            }

            // Apply new modifier with the specified operation
            AttributeModifier mod = new AttributeModifier(modifierId, effectValue, operation);
            attributeInstance.addPermanentModifier(mod);
        }
    }
    protected void applyEffects(LivingEntity living, List<AttributeModification> modifications) {
        for (AttributeModification modification : modifications) {
            AttributeInstance attributeInstance = living.getAttribute(modification.attribute());
            if (attributeInstance != null) {
                double baseValue = living.getAttributeBaseValue(modification.attribute());
                double effectValue = modification.effectFunction().applyAsDouble(baseValue);

                if (modification.modifierId() == null) {
                    throw new IllegalArgumentException("modifierId cannot be null report this on github");
                }
                AttributeModifier existingModifier = attributeInstance.getModifier(modification.modifierId());
                if (existingModifier != null) {
                    attributeInstance.removeModifier(existingModifier);
                }

                AttributeModifier mod = new AttributeModifier(modification.modifierId(), effectValue, modification.operation());
                attributeInstance.addPermanentModifier(mod);
            }
        }
    }

    // Reset the stat to its base value and clean up attribute instances
    public void reset(ServerPlayer player, LevelSystem levelSystem) {
        levelSystem.addStatPoints(this.totalPointsUsed);

        this.currentValue = baseValue;
        this.totalPointsUsed = 0;

        applyPrimaryEffect(player);
        applySecondaryEffect(player);
    }
    public abstract List<Component> getEffectDescription(int additionalPoints);
    protected ResourceLocation getPrimaryId() {
        return null;
    }
    protected ResourceLocation getSecondaryId(){
        return null;
    }
}

