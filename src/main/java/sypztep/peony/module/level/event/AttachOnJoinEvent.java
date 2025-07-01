package sypztep.peony.module.level.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import sypztep.peony.common.attachment.DamageTrackingAttachment;
import sypztep.peony.common.init.ModAttachments;

public class AttachOnJoinEvent {
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity && !event.getLevel().isClientSide()) {
            livingEntity.getData(ModAttachments.LIVINGSTATS.get());
            if (livingEntity instanceof Player player)
                player.getData(ModAttachments.LIVINGSTATS.get()).syncToPlayer((ServerPlayer) player); // force sync a data on join

            if (!(event.getEntity() instanceof Player)) {
                DamageTrackingAttachment tracking = livingEntity.getData(ModAttachments.DAMAGE_TRACKING.get());
                tracking.setMaxHealth(livingEntity.getMaxHealth());
            }

        }
    }
}
