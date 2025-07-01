package sypztep.peony.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageTrackingAttachment {
    private final Map<UUID, Float> damageDealt = new HashMap<>();
    private float maxHealth = 0.0f;
    private float totalDamageTracked = 0.0f;

    public void addDamage(UUID playerId, float damage) {
        damageDealt.put(playerId, damageDealt.getOrDefault(playerId, 0.0f) + damage);
        totalDamageTracked += damage;
    }

    public float getDamagePercentage(UUID playerId) {
        if (totalDamageTracked <= 0) return 0.0f;
        float playerDamage = damageDealt.getOrDefault(playerId, 0.0f);
        return Math.min(1.0f, playerDamage / maxHealth);
    }

    public Map<UUID, Float> getAllDamageDealt() {
        return new HashMap<>(damageDealt);
    }

    public boolean hasParticipants() {
        return !damageDealt.isEmpty();
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void reset() {
        damageDealt.clear();
        totalDamageTracked = 0.0f;
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, DamageTrackingAttachment> {
        @Override
        public DamageTrackingAttachment read(net.neoforged.neoforge.attachment.IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider lookup) {
            DamageTrackingAttachment attachment = new DamageTrackingAttachment();

            attachment.maxHealth = tag.getFloat("maxHealth");
            attachment.totalDamageTracked = tag.getFloat("totalDamageTracked");

            ListTag damageList = tag.getList("damageDealt", Tag.TAG_COMPOUND);
            for (int i = 0; i < damageList.size(); i++) {
                CompoundTag entry = damageList.getCompound(i);
                UUID playerId = entry.getUUID("playerId");
                float damage = entry.getFloat("damage");
                attachment.damageDealt.put(playerId, damage);
            }

            return attachment;
        }

        @Override
        public CompoundTag write(DamageTrackingAttachment attachment, HolderLookup.Provider lookup) {
            CompoundTag tag = new CompoundTag();

            tag.putFloat("maxHealth", attachment.maxHealth);
            tag.putFloat("totalDamageTracked", attachment.totalDamageTracked);

            ListTag damageList = new ListTag();
            for (Map.Entry<UUID, Float> entry : attachment.damageDealt.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putUUID("playerId", entry.getKey());
                entryTag.putFloat("damage", entry.getValue());
                damageList.add(entryTag);
            }
            tag.put("damageDealt", damageList);

            return tag;
        }
    }
}