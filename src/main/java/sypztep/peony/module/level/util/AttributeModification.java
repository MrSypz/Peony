package sypztep.peony.module.level.util;

import java.util.function.ToDoubleFunction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record AttributeModification(Holder<Attribute> attribute, ResourceLocation modifierId,
                                    AttributeModifier.Operation operation,
                                    ToDoubleFunction<Double> effectFunction) {
}