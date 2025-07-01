package sypztep.peony.module.level.element;


import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import sypztep.peony.Peony;
import sypztep.peony.module.level.Stat;

public class IntelligenceStat extends Stat {
    public IntelligenceStat(int baseValue) {
        super(baseValue);
    }

    @Override
    public void applyPrimaryEffect(LivingEntity living) {
//        applyEffect(
//                living,
//                ModEntityAttributes.GENERIC_MAGIC_ATTACK_DAMAGE,
//                getPrimaryId(),
//                AttributeModifier.Operation.ADD_VALUE,
//                baseValue -> 0.02 * this.currentValue
//        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity living) {
//        applyEffect(
//                living,
//                ModEntityAttributes.GENERIC_MAGIC_RESISTANCE,
//                getPrimaryId(),
//                AttributeModifier.Operation.ADD_VALUE,
//                baseValue -> 0.005 * this.currentValue
//        );
    }
    @Override
    public List<Component> getEffectDescription(int additionalPoints) {
        List<Component> description = new ArrayList<>();

        description.add(Component.literal("Primary Effect:").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD)));
        description.add(Component.literal("- Magic Attack Damage: ")
                .append(Component.literal("+ " + (2 * additionalPoints) + "%").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));

        description.add(Component.literal("Secondary Effect:").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD)));
        description.add(Component.literal("- Magic Resistance: ")
                .append(Component.literal("+ " + (0.5 * additionalPoints) + "%").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));

        return description;
    }


    @Override
    protected ResourceLocation getPrimaryId() {
        return Peony.id("intelligence_primary");
    }

    @Override
    protected ResourceLocation getSecondaryId() {
        return Peony.id("intelligence_secondary");
    }
}
