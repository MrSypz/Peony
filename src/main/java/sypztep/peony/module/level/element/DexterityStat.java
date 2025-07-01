package sypztep.peony.module.level.element;


import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.fml.common.Mod;
import sypztep.peony.Peony;
import sypztep.peony.common.init.ModAttributes;
import sypztep.peony.module.level.Stat;

public class DexterityStat extends Stat {

    public DexterityStat(int baseValue) {
        super(baseValue);
    }

    @Override
    public void applyPrimaryEffect(LivingEntity living) {
        applyEffect(living, ModAttributes.ACCURACY, this.getPrimaryId(), AttributeModifier.Operation.ADD_VALUE,
                value -> 1);
//        var accuracy = ModEntityComponents.STATS.get(living);
//        accuracy.addExtraAccuracy(1);
    }

    @Override
    public void applySecondaryEffect(LivingEntity living) {
//        List<AttributeModification> modifications = List.of(
//                new AttributeModification(
//                        ModEntityAttributes.GENERIC_PROJECTILE_ATTACK_DAMAGE,
//                        getSecondaryId(),
//                        AttributeModifier.Operation.ADD_VALUE,
//                        baseValue -> (0.02 * this.currentValue)
//                ),
//                new AttributeModification(
//                        Attributes.ATTACK_SPEED,
//                        getSecondaryId(),
//                        AttributeModifier.Operation.ADD_VALUE,
//                        baseValue -> (0.005 * this.currentValue)
//                )
//        );
//
//        applyEffects(living, modifications);
    }
    @Override
    public List<Component> getEffectDescription(int additionalPoints) {
        List<Component> description = new ArrayList<>();

        description.add(Component.literal("Primary Effect:").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD)));
        description.add(Component.literal("- Accuracy: ")
                .append(Component.literal("+ " + additionalPoints + " per DEX").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));

        description.add(Component.literal("Secondary Effect:").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD)));
        description.add(Component.literal("- Projectile Attack Damage: ")
                .append(Component.literal("+ " + (2 * additionalPoints) + "%").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));
        description.add(Component.literal("- Attack Speed: ")
                .append(Component.literal("+ " + (0.5 * additionalPoints) + "%").withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY)));

        return description;
    }

    @Override
    protected ResourceLocation getPrimaryId() {
        return Peony.id("dexterity_primary");
    }

    @Override
    protected ResourceLocation getSecondaryId() {
        return Peony.id("dexterity_secondary");
    }
}
