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
import sypztep.peony.module.level.util.AttributeModification;

public class LuckStat extends Stat {
    public LuckStat(int baseValue) {
        super(baseValue);
    }

    @Override
    public void applyPrimaryEffect(LivingEntity living) {
        applyEffect(living,
                ModAttributes.CRIT_CHANCE,
                getPrimaryId(),
                AttributeModifier.Operation.ADD_VALUE,
                baseValue -> (0.0025 * this.currentValue) // every 4 point gain 1 chance
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity living) {
//        List<AttributeModification> modifications = List.of(
//                new AttributeModification(
//                        ModEntityAttributes.GENERIC_MAGIC_ATTACK_DAMAGE,
//                        getSecondaryId(),
//                        AttributeModifier.Operation.ADD_VALUE,
//                        baseValue -> (0.002 * this.currentValue)
//                ),
//                new AttributeModification(
//                        Attributes.ATTACK_SPEED,
//                        getSecondaryId(),
//                        AttributeModifier.Operation.ADD_VALUE,
//                        baseValue -> (0.002 * this.currentValue)
//                )
//        );
//        StatsComponent statsComponent = ModEntityComponents.STATS.get(living);
//        if (this.currentValue % 3 == 0) statsComponent.addExtraAccuracy(1);
//        if (this.currentValue % 5 == 0) statsComponent.addExtraEvasion(1);
//        applyEffects(living, modifications);
    }
    @Override
    public List<Component> getEffectDescription(int additionalPoints) {
        List<Component> description = new ArrayList<>();

        description.add(Component.literal("Primary Effect:").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD)));
        description.add(Component.literal("- Critical Hit Chance: ")
                .append(Component.literal("+ " + additionalPoints + "%").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));

        description.add(Component.literal("Secondary Effect:").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD)));
        description.add(Component.literal("- Magic Attack Damage: ")
                .append(Component.literal("+ " + (0.02 * additionalPoints) + "%").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));
        description.add(Component.literal("- Attack Speed: ")
                .append(Component.literal("+ " + (0.02 * additionalPoints) + "%").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));
        description.add(Component.literal("- Additional Accuracy and Evasion: ")
                .append(Component.literal("+ " + additionalPoints + " per 5 LUK").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));

        return description;
    }

    @Override
    protected ResourceLocation getPrimaryId() {
        return Peony.id("luck_primary");
    }

    @Override
    protected ResourceLocation getSecondaryId() {
        return Peony.id("luck_secondary");
    }

}
