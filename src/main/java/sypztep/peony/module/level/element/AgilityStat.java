package sypztep.peony.module.level.element;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import sypztep.peony.Peony;
import sypztep.peony.common.init.ModAttributes;
import sypztep.peony.module.level.Stat;

public class AgilityStat extends Stat {
    public AgilityStat(int baseValue) {
        super(baseValue);
    }

    @Override
    public void applyPrimaryEffect(LivingEntity living) {
//        applyEffect(living, Attributes.ATTACK_SPEED, getPrimaryId(), AttributeModifier.Operation.ADD_VALUE,
//                baseValue -> (0.01 * this.currentValue));
    }

    @Override
    public void applySecondaryEffect(LivingEntity living) {
//        var evasion = ModEntityComponents.STATS.get(living);
//        evasion.addExtraEvasion(1);
        applyEffect(living, ModAttributes.EVASION, Peony.id("stats_extra_evasion"), AttributeModifier.Operation.ADD_VALUE,
                value -> 1);
//
//        applyEffect(living, ModEntityAttributes.GENERIC_PLAYER_DRAWSPEED, getSecondaryId(), AttributeModifier.Operation.ADD_VALUE,
//                baseValue -> (0.005 * this.currentValue));
    }

    @Override
    public List<Component> getEffectDescription(int additionalPoints) {
        List<Component> description = new ArrayList<>();

        description.add(Component.literal("Primary Effect:").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD)));
        description.add(Component.literal("- Attack Speed: ")
                .append(Component.literal("+ " +additionalPoints + "% per AGI").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));

        description.add(Component.literal("Secondary Effect:").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD)));
        description.add(Component.literal("- Evasion: ")
                .append(Component.literal("+ " + additionalPoints + " per AGI").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));
        description.add(Component.literal("- Bow Draw Speed: ")
                .append(Component.literal("+ " + (0.5 * additionalPoints) + "% per AGI").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));

        return description;
    }

    @Override
    protected ResourceLocation getPrimaryId() {
        return Peony.id("agility_primary");
    }

    @Override
    protected ResourceLocation getSecondaryId() {
        return Peony.id("agility_secondary");
    }
}

