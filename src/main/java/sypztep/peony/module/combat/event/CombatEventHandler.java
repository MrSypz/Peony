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
    //TODO: this part are monster apply special attack to player but for noreason it dupe event in Critical hit.. So fix in the future 2/7/2025
//    @SubscribeEvent
//    public static void onDealDamage(DealDamageEvent.Pre event) {
//        LivingEntity target = event.getTarget();
//        LivingDamageEvent.Pre neoforgeEvent = event.getNeoForgeEvent();
//
//        float originalDamage = neoforgeEvent.getNewDamage();
//
//        float modifiedDamage = CombatHelper.modifyDamage(target, originalDamage, event.getDamageSource());
//
//        neoforgeEvent.setNewDamage(modifiedDamage);
//
//        float multiplier = modifiedDamage / originalDamage;
//        if (multiplier != 1.0F) {
//            ValueModifier existing = event.getDamageSource().getDamageModifier();
//            if (existing != null) existing.merge(ValueModifier.multiplier(multiplier));
//             else event.getDamageSource().setDamageModifier(ValueModifier.multiplier(multiplier));
//        }
//    }
//    @SubscribeEvent
//    public static void onPlayerHurt(HurtEvent.Post event) {
//        Player player = event.getPlayerPatch().getOriginal();
//        float originalDamage = event.getOriginalDamage();
//        float modifiedDamage = CombatHelper.modifyDamage(player, originalDamage, event.getDamageSource());
//        event.setDamage(modifiedDamage);
//    }
}