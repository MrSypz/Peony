package sypztep.peony.module.combat.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import sypztep.peony.module.combat.util.CombatHelper;
import yesman.epicfight.api.neoforgeevent.playerpatch.DealDamageEvent;
import yesman.epicfight.api.utils.math.ValueModifier;

public class CombatEventHandler {
    @SubscribeEvent()
    public static void onDealDamage(DealDamageEvent.Pre event) {
        LivingEntity target = event.getTarget();
        LivingEntity attacker = event.getPlayerPatch().getOriginal();
        LivingDamageEvent.Pre neoforgeEvent = event.getNeoForgeEvent();

        float originalDamage = neoforgeEvent.getNewDamage();

        float modifiedDamage = CombatHelper.modifyDamage(target, originalDamage, event.getDamageSource());

        neoforgeEvent.setNewDamage(modifiedDamage);

        float multiplier = modifiedDamage / originalDamage;
        if (multiplier != 1.0F) {
            ValueModifier existing = event.getDamageSource().getDamageModifier();
            if (existing != null) existing.merge(ValueModifier.multiplier(multiplier));
             else event.getDamageSource().setDamageModifier(ValueModifier.multiplier(multiplier));
        }
    }
}