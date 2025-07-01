package sypztep.peony.common.init;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import sypztep.peony.Peony;
import sypztep.peony.common.attachment.DamageTrackingAttachment;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Peony.MODID);

    public static final Supplier<AttachmentType<LivingEntityStatsAttachment>> LIVINGSTATS =
            ATTACHMENT_TYPES.register("livingstats", () ->
                    AttachmentType
                            .builder(LivingEntityStatsAttachment::new)
                            .serialize(new LivingEntityStatsAttachment.Serializer())
                            .copyOnDeath()
                            .build()
            );
    public static final Supplier<AttachmentType<DamageTrackingAttachment>> DAMAGE_TRACKING =
            ATTACHMENT_TYPES.register("damage_tracking", () ->
                    AttachmentType
                            .builder(DamageTrackingAttachment::new)
                            .serialize(new DamageTrackingAttachment.Serializer())
                            .build() // No copyOnDeath - damage tracking should reset on death
            );
}