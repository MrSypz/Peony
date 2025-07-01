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
import sypztep.peony.module.level.Stat;
import sypztep.peony.module.level.util.AttributeModification;


public class VitalityStat extends Stat {
    public VitalityStat(int baseValue) {
        super(baseValue);
    }

    @Override
    public void applyPrimaryEffect(LivingEntity living) {
        applyEffect(
                living,
                Attributes.MAX_HEALTH,
                getPrimaryId(),
                AttributeModifier.Operation.ADD_VALUE,
                baseValue -> baseValue * (0.05 * this.currentValue)
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity living) {
//        List<AttributeModification> modifications = List.of(
//                new AttributeModification(
//                        ModEntityAttributes.GENERIC_HEALTH_REGEN,
//                        getSecondaryId(),
//                        AttributeModifier.Operation.ADD_VALUE,
//                        baseValue -> (0.02 * this.currentValue)
//                ),
//                new AttributeModification(
//                        ModEntityAttributes.GENERIC_PHYSICAL_RESISTANCE,
//                        getSecondaryId(),
//                        AttributeModifier.Operation.ADD_VALUE,
//                        baseValue -> (0.005 * this.currentValue)
//                )
//        );
//        applyEffects(living, modifications);
    }
    @Override
    public List<Component> getEffectDescription(int additionalPoints) {
        List<Component> description = new ArrayList<>();

        description.add(Component.literal("Primary Effect:").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD)));
        description.add(Component.literal("- Max Health: ")
                .append(Component.literal("+ " + (5 * additionalPoints) + "%").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));

        description.add(Component.literal("Secondary Effect:").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD)));
        description.add(Component.literal("- Health Regen: ")
                .append(Component.literal("+ " + (0.02 * additionalPoints) + "%").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));
        description.add(Component.literal("- Physical Resistance: ")
                .append(Component.literal("+ " + (0.5 * additionalPoints) + "%").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));

        return description;
    }

    @Override
    protected ResourceLocation getPrimaryId() {
        return Peony.id("vitality_primary");
    }

    @Override
    protected ResourceLocation getSecondaryId() {
        return Peony.id("vitality_secondary");
    }
}
